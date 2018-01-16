package akka.persistence.chaos.journal

import akka.actor._
import akka.serialization.SerializationExtension

object ChaosStorageExtension extends ExtensionId[ChaosStorageExtensionImpl] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): ChaosStorageExtensionImpl = new ChaosStorageExtensionImpl()(system)

  override def lookup(): ExtensionId[_ <: Extension] = ChaosStorageExtension
}

class ChaosStorageExtensionImpl()(implicit val system: ExtendedActorSystem) extends Extension {
  val serialization = SerializationExtension(system)

  val journalStorage: ActorRef = system.actorOf(Props(new ChaosJournalStorage(serialization)), "ChaosJournalStorage")
}