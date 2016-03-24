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

import org.specs2.mutable._
import play.api.libs.json.Json

/** */
object InputGeneratorSpec extends Specification {

  //sbt > test-only com.mcw.InputGeneratorSpec

  val now = System.currentTimeMillis()

  "InputGenerator" should {
    "randomSize and randomScore" in {
      val r1 = InputGenerator.randomSize
      (0 <= r1 && r1 <= 10)
      val r2 = InputGenerator.randomSize
      (0 <= r2 && r2 <= 10)
      val r3 = InputGenerator.randomSize
      (0 <= r3 && r3 <= 10)

      val rsc1 = InputGenerator.randomScore
      (0 <= rsc1 && rsc1 <= 10)
      val rsc2 = InputGenerator.randomSize
      (0 <= rsc2 && rsc2 <= 10)
      val rsc3 = InputGenerator.randomSize
      (0 <= rsc3 && rsc3 <= 10)
    }

    "makeOne" in {
      val g1 = InputGenerator.makeOneMessage
      println("g1 " + g1)
      (g1.timestamp >= now) === true
      //(g1.subject.name.length > 1) === true
      (g1.name.length > 1) === true

      val s = Json.stringify(Json.toJson(g1))
      println("g1 json " + s)
      s.contains(g1.name) === true
    }
  }

}
