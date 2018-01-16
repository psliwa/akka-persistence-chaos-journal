package org.psliwa.akka.persistence.chaos.journal.example

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.psliwa.akka.persistence.chaos.journal.example.Messages.Domain.{Message, MessageId}
import org.psliwa.akka.persistence.chaos.journal.example.Messages.Protocol.{GetMessage, GetMessageResponse, SaveMessage}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class MessagesDeliveryGuaranteeSpec
  extends TestKit(ActorSystem(UUID.randomUUID().toString.substring(0, 10)))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with Eventually {

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 20 millis)

  private val messagesDeliveryGuarantee = system.actorOf(MessagesDeliveryGuarantee.props)

  "MessagesDeliveryGuarantee" should {
    (1 to 20).foreach { index =>
      s"save $index. message" in {
        val messageId = MessageId(index)
        val message = Message(s"message $index")

        messagesDeliveryGuarantee ! SaveMessage(messageId, message)

        eventually {
          messagesDeliveryGuarantee ! GetMessage(messageId)
          expectMsg(GetMessageResponse(messageId, Some(message)))
        }
      }
    }
  }

  override def afterAll(): Unit = {
    Await.ready(system.terminate(), 10 seconds)
  }
}
