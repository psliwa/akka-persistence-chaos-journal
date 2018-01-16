package akka.persistence.chaos.journal

import java.util.UUID

import akka.actor.ActorSystem
import akka.persistence.PersistentRepr
import akka.persistence.inmemory.extension.InMemoryJournalStorage.ClearJournal
import akka.testkit.TestProbe
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._

import scala.concurrent.duration._
import scala.util.Try

abstract class TestSpec(val config: Config) extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures {
  def this(config: String) = this(ConfigFactory.load(config))

  implicit val system: ActorSystem = ActorSystem("test", config)
  implicit val timeout: Timeout = 10 seconds

  val probe = TestProbe()
  val writerUuid: String = UUID.randomUUID.toString
  val persistenceId = "persistenceId"

  protected def persistentRepr(sequenceNr: Long) =
    PersistentRepr(
      payload = s"$sequenceNr",
      sequenceNr = sequenceNr,
      persistenceId = persistenceId,
      sender = probe.ref,
      writerUuid = writerUuid
    )

  override protected def beforeEach(): Unit = {
    import akka.pattern.ask
    Try(ChaosStorageExtension(system).journalStorage ? ClearJournal) should be a 'success
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    Try(system.terminate().futureValue) should be a 'success
  }
}
