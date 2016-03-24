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

import akka.actor.{PoisonPill, Props, ActorSystem}
import models.{KSpeedBagModels, GeneratedMessage}

/**
 * Make stuff to go into a Kafka Topic
 */
object InputGenerator {

  val RAND = scala.util.Random

  def randomSize: Int = { RAND.nextInt(11) }
  def randomScore: Int = { RAND.nextInt(101) }

  // GeneratedMessage(subj: Subject, size: Int, color: String, score: Int, timestamp: Long)
  def makeOneMessage(): GeneratedMessage = {
    val subj = KSpeedBagModels.SUBJECTS(RAND.nextInt(KSpeedBagModels.SUBJECTS.size))
    val color = KSpeedBagModels.COLORS(RAND.nextInt(KSpeedBagModels.COLORS.size))

    GeneratedMessage(subj.id, subj.name, randomSize, color, randomScore, System.currentTimeMillis)
  }

}
