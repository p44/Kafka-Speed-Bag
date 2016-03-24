/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.mcw

import akka.actor._
import models.KSpeedBagModels

case class Run(seconds: Int)
case class Generate(seconds: Int)

/**
 *
 */
object RunActor {
  val runActorName = "GeorgeForeman"
  val propsRunActor = Props[RunActor]
  def getConsumeActor(system: ActorSystem): ActorRef = {
    system.actorOf(propsRunActor, runActorName)
  }
}

class RunActor extends Actor with ActorLogging {

  val producer = KafkaProducer.initProducer(KafkaProducer.defaultProps)
  val topic = KSpeedBagModels.KAFKA_TOPIC_NAME
  val mPerSec = KSpeedBagModels.MESSAGES_PER_SECOND
  val oneSecMillis = 1000L

  def started: Receive = {
    case Run(seconds) => {
      if(log.isInfoEnabled) log.info("Stream consumers have already been started")
    }
    case Generate(seconds) => {
      // start producing to the topic
      generate(seconds)
    }
  }

  def notStarted: Receive = {
    case Run(seconds) => {
      // Start the Spark Streaming Kafka topic consumer
      KafkaStreamConsumer.init()
      context.become(started)

      // Start producing to the topic
      self ! Generate(seconds)
    }
    case Generate(seconds) => {
      if(log.isInfoEnabled) log.info("Generate requires the stream consumers to be started")
    }
  }

  def receive = notStarted

  /**
   * generate and feed to the producer actor
   *
   * @param forThisLongSec
   */
  def generate(forThisLongSec: Int): Unit = {
    if(log.isInfoEnabled) log.info("RunActor.generate running for seconds: " + forThisLongSec)
    val pa = context.system.actorOf(Props[ProducerActor])
    var sec = 0

    while(sec < forThisLongSec) {
      (1 to mPerSec).foreach { x =>
        val gm = InputGenerator.makeOneMessage
        pa ! OneMessage(gm)
      }
      sec = sec+1
      Thread.sleep(oneSecMillis)
    }

    /*
    Stop - lets current message finish then discards the rest in the queue
    PoisonPill - adds the kill to the mailbox and lets finish what is queued
    */
    pa ! PoisonPill
    println("RunActor.generate PoisonPill sent to producer actor")
  }

}



