package akka.persistence.chaos.journal

import akka.actor.ActorRef
import akka.persistence.JournalProtocol.{WriteMessages, WriteMessagesFailed, WriteMessagesSuccessful}
import akka.persistence.{AtomicWrite, _}

import scala.collection.immutable

class ChaosJournalAlwaysFailsSpec extends TestSpec("application-1-failure-rate.conf") {

  val writeJournal: ActorRef = Persistence(system).journalFor("akka.persistence.chaos.journal")

  "ChaosJournal when the failure rate is 100%"  should "always fails to write the message" in {
    probe.ignoreMsg {
      case _: WriteMessagesFailed => false
      case WriteMessagesSuccessful => false
      case _ => true
    }

    0 to 100 foreach { sequenceNr =>
      writeJournal ! WriteMessages(immutable.Seq(AtomicWrite(persistentRepr(sequenceNr))), probe.ref, 1)
      probe.expectMsgType[WriteMessagesFailed]
    }
  }
}
