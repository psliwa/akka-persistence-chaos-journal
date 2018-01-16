package org.psliwa.akka.persistence.chaos.journal.example

import akka.actor.Props
import akka.persistence.PersistentActor
import org.psliwa.akka.persistence.chaos.journal.example.Messages.Domain.{Message, MessageId}
import org.psliwa.akka.persistence.chaos.journal.example.Messages.Protocol._
import org.psliwa.akka.persistence.chaos.journal.example.MessagesDeliveryGuarantee.{DeliveryConfirmation, DeliveryWrapper}

/**
  * Represents application business logic with persistent actors inside
  */
private class Messages extends PersistentActor {
  private var messages: Map[MessageId, Message] = Map.empty

  override def receiveRecover: Receive = {
    case SaveMessage(messageId, message) =>
      messages += messageId -> message
  }

  override def receiveCommand: Receive = {
    case DeliveryWrapper(deliveryId, saveMessage: SaveMessage) =>
      persist(saveMessage) { event =>
        messages += event.messageId -> event.message
        sender() ! DeliveryConfirmation(deliveryId)
      }

    case GetMessage(messageId) =>
      sender() ! GetMessageResponse(messageId, messages.get(messageId))
  }

  override def persistenceId: String = "messages"
}

object Messages {

  private[example] def props: Props = Props[Messages]

  object Domain {
    final case class Message(msg: String)
    final case class MessageId(id: Int)
  }

  object Protocol {
    final case class SaveMessage(messageId: MessageId, message: Message)
    final case class GetMessage(messageId: MessageId)
    final case class GetMessageResponse(messageId: MessageId, message: Option[Message])
  }
}
