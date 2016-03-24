package com.mcw

import akka.actor._
import models.{KSpeedBagModels, GeneratedMessage}
import play.api.libs.json.Json

case class OneMessage(msg: GeneratedMessage)

/** Takes a Genereated Message and produces to Kafka as JSON */
class ProducerActor extends Actor with ActorLogging {

  val producer = KafkaProducer.initProducer(KafkaProducer.defaultProps)
  val topic = KSpeedBagModels.KAFKA_TOPIC_NAME

  def receive: Receive = {
    case OneMessage(msg: GeneratedMessage) => {
      //println("actor received " + msg)
      try {
        val m = Json.stringify(Json.toJson(msg))
        //println("actor json " + m)
        KafkaProducer.produceMessageToTopic(producer, topic, msg.id.toString, m) match {
          case true => //println("success")
          case false => log.error(s"Failed to produce to $topic message $m")
        }
      } catch {
        case e: Exception => log.error(s"Failed to produce to $topic message $msg e: " + e.getMessage)
      }
    }
  }

}
