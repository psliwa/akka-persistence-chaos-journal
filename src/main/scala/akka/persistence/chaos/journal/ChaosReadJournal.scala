package akka.persistence.chaos.journal

import akka.NotUsed
import akka.actor.ExtendedActorSystem
import akka.persistence.inmemory.query.scaladsl.InMemoryReadJournal
import akka.persistence.query.EventEnvelope
import akka.persistence.query.javadsl.{CurrentEventsByPersistenceIdQuery => JavaCurrentEventsByPersistenceIdQuery, EventsByPersistenceIdQuery => JavaEventsByPersistenceIdQuery}
import akka.stream.javadsl.{Source => JavaSource}
import com.typesafe.config.Config

class ChaosReadJournal(config: Config)(implicit override val system: ExtendedActorSystem) extends InMemoryReadJournal(config) {

  //TODO: it is really ugly hack - sorry for that :( Fix when https://github.com/dnvriend/akka-persistence-inmemory/pull/44 is merged and released
  unsafeOverrideJournalStorage()

  private def unsafeOverrideJournalStorage(): Unit = {
    import scala.reflect.runtime.universe._

    val mirror = runtimeMirror(this.getClass.getClassLoader)
    val instanceMirror = mirror.reflect(this)
    val declaration = typeOf[InMemoryReadJournal].member(TermName("journal")).asTerm
    val fieldMirror = instanceMirror.reflectField(declaration)
    fieldMirror.set(ChaosStorageExtension(system).journalStorage)
  }
}

class JavaChaosReadJournal(scalaChaosReadJournal: ChaosReadJournal)
  extends JavaEventsByPersistenceIdQuery with JavaCurrentEventsByPersistenceIdQuery {

  override def eventsByPersistenceId(persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long): JavaSource[EventEnvelope, NotUsed] =
    scalaChaosReadJournal.eventsByPersistenceId(persistenceId, fromSequenceNr, toSequenceNr).asJava

  override def currentEventsByPersistenceId(persistenceId: String, fromSequenceNr: Long, toSequenceNr: Long): JavaSource[EventEnvelope, NotUsed] =
    scalaChaosReadJournal.currentEventsByPersistenceId(persistenceId, fromSequenceNr, toSequenceNr).asJava
}