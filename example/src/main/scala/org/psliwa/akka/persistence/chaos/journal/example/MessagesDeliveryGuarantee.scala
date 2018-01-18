package org.psliwa.akka.persistence.chaos.journal.example

import akka.actor.{Actor, Props}
import akka.pattern.{Backoff, BackoffSupervisor}
import akka.persistence.{AtLeastOnceDelivery, Recovery}
import org.psliwa.akka.persistence.chaos.journal.example.Messages.Protocol.{GetMessage, SaveMessage}
import org.psliwa.akka.persistence.chaos.journal.example.MessagesDeliveryGuarantee.{DeliveryConfirmation, DeliveryWrapper}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Represents delivery guarantee logic in the application which we want to test using akka-persistence-chaos-journal
  */
class MessagesDeliveryGuarantee extends AtLeastOnceDelivery {

  private val messages = context.actorOf(
    BackoffSupervisor.props(
      Backoff.onStop(
        Messages.props,
        childName = "messages",
        minBackoff = 10 millis,
        maxBackoff = 10 millis,
        randomFactor = 0.1
      )
    ),
    name = "messagesSupervisor"
  )

  override def receiveRecover: Receive = Actor.emptyBehavior

  override def recovery: Recovery = Recovery.none

  override def receiveCommand: Receive = {
    case msg: SaveMessage =>
      deliver(messages.path)(DeliveryWrapper(_, msg))

    case DeliveryConfirmation(deliveryId) =>
      confirmDelivery(deliveryId)

    case msg: GetMessage =>
      messages forward msg
  }

  override def persistenceId: String = "not-used"
}

object MessagesDeliveryGuarantee {

  def props: Props = Props[MessagesDeliveryGuarantee]

  final case class DeliveryWrapper(deliveryId: Long, msg: Any)
  final case class DeliveryConfirmation(deliveryId: Long)
}