package akka.persistence.chaos.journal

import akka.actor.ActorRef
import akka.persistence.inmemory.journal.InMemoryAsyncWriteJournal
import com.typesafe.config.Config

class ChaosWriteJournal(config: Config) extends InMemoryAsyncWriteJournal(config) {
  override val journal: ActorRef = ChaosStorageExtension(system).journalStorage
}
