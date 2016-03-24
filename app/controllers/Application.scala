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
* Unless required by applicable law or agreed to in writing, softwarer
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package controllers

import com.mcw.{ResultsBroadcast, Run, RunActor}
import models.KSpeedBagModels
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.mvc._
import play.api.Play.current
import play.api.libs.concurrent.Akka

class Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def results = Action {
    Ok(views.html.index())
  }

  /**
   * Start the simulator that generates json and produces to kafka
   *
   * @return
   */
  def start = Action {
    // Start the consumer if not already started - the actor will change state at first run
    // Subsequent Generate calls can be made against a running
    RunActor.getConsumeActor(Akka.system) ! Run(models.KSpeedBagModels.MESSAGE_GENERATION_DURATION_SECONDS)

    Ok(views.html.index())
  }

  /**
   * Re-starts the message generation to kafka producing actor which runs for seconds specified
   * in the config for
   */
  def generate = Action {
    NotImplemented
  }



  // Streaming results out

  /**
   * Controller action serving activity for new whale sightings
   */
  def resultsFeed = Action { req =>
    println("FEED resultsFeed - " + req.remoteAddress + " -  resultsOut connected")
    Ok.chunked(ResultsBroadcast.resultsOut
      &> Concurrent.buffer(100)
      &> ResultsBroadcast.connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream") // &>  Compose this Enumerator with an Enumeratee. Alias for 'through'
  }

}
