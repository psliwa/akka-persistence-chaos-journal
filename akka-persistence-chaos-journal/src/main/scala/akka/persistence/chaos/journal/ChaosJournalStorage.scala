package akka.persistence.chaos.journal

import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import akka.actor.{ActorRef, Status}
import akka.persistence.PersistentRepr
import akka.persistence.inmemory.JournalEntry
import akka.persistence.inmemory.extension.InMemoryJournalStorage
import akka.serialization.Serialization

import scala.concurrent.duration.FiniteDuration
import scala.util.control.NoStackTrace

class WriteFailedException(payloads: Seq[PersistentRepr])
    extends RuntimeException(s"write failed for payloads = [${ payloads.map(_.payload) }]") with NoStackTrace

class ReplayFailedException(payloads: Seq[PersistentRepr])
    extends RuntimeException(s"recovery failed after replaying payloads = [${ payloads.map(_.payload) }]") with NoStackTrace

class ReadHighestFailedException
    extends RuntimeException("recovery failed when reading highest sequence number") with NoStackTrace

class ChaosJournalStorage(serialization: Serialization) extends InMemoryJournalStorage(serialization) {

  private val config = context.system.settings.config.getConfig("akka.persistence.chaos.journal")
  private val writeFailureRate = config.getDouble("write-failure-rate")
  private val replayFailureRate = config.getDouble("replay-failure-rate")
  private val readHighestFailureRate = config.getDouble("read-highest-failure-rate")
  private val minWriteDelay = FiniteDuration(config.getDuration("min-write-delay", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
  private val maxWriteDelay = FiniteDuration(config.getDuration("max-write-delay", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)

  private def random = ThreadLocalRandom.current

  override def highestSequenceNr(ref: ActorRef, persistenceId: String, fromSequenceNr: Long): Unit =
    chaosly(
      ref,
      readHighestFailureRate,
      super.highestSequenceNr(ref, persistenceId, fromSequenceNr),
      new ReadHighestFailedException()
    )

  override def writelist(ref: ActorRef, xs: scala.collection.immutable.Seq[JournalEntry]): Unit =
    chaosly(ref, writeFailureRate, delayedWritelist(ref, xs), new WriteFailedException(xs.map(_.repr)))

  private def delayedWritelist(ref: ActorRef, xs: scala.collection.immutable.Seq[JournalEntry]): Unit = {
    import context.dispatcher
    val delay = randomDelay(minWriteDelay, maxWriteDelay)
    context.system.scheduler.scheduleOnce(delay, self, SuperWriteList(ref, xs))
  }

  private def randomDelay(min: FiniteDuration, max: FiniteDuration): FiniteDuration = {
    if(min == max) min
    else FiniteDuration(random.nextLong(min.toMillis, max.toMillis), TimeUnit.MILLISECONDS)
  }

  override def messages(ref: ActorRef, persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long, max: Long, all: Boolean): Unit = {
    val wholeJournal = journal.filter(_._1 == persistenceId).flatMap(_._2).map(_.repr).toList
    chaosly(
      ref,
      replayFailureRate,
      super.messages(ref, persistenceId, fromSequenceNr, toSequenceNr, max, all),
      new ReplayFailedException(wholeJournal)
    )
  }

  private def chaosly(ref: ActorRef, rate: Double, success: => Unit, failure: => Exception): Unit = {
    if (shouldFail(rate)) {
      ref ! Status.Failure(failure)
    } else {
      success
    }
  }

  private def shouldFail(rate: Double): Boolean =
    random.nextDouble() < rate

  override def receive: Receive = super.receive orElse {
    case SuperWriteList(ref, xs) => super.writelist(ref, xs)
  }

  private case class SuperWriteList(ref: ActorRef, xs: scala.collection.immutable.Seq[JournalEntry])
}
