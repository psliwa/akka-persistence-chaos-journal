package akka.persistence.chaos.journal

import akka.actor.ExtendedActorSystem
import akka.persistence.query.ReadJournalProvider
import com.typesafe.config.Config

class ChaosReadJournalProvider(system: ExtendedActorSystem, config: Config) extends ReadJournalProvider {
  override val scaladslReadJournal = new ChaosReadJournal(config)(system)

  override val javadslReadJournal = new JavaChaosReadJournal(scaladslReadJournal)
}
