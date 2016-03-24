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

package models

import com.typesafe.config.ConfigFactory
import play.api.libs.json.Json

/** simple msg - used for web results */
case class Msg(msg: String)
object Msg {
  implicit val jsonWriter = Json.writes[Msg]
  implicit val jsonReader = Json.reads[Msg]
}

case class Subject(id: Long, name: String)
object Subject {
  implicit val jsonWriter = Json.writes[Subject]
  implicit val jsonReader = Json.reads[Subject]
}

/** used to diplay simple results */
case class NameValue(name: String, value: String)
object NameValue {
  implicit val jsonWriter = Json.writes[NameValue]
  implicit val jsonReader = Json.reads[NameValue]
}

/** used to diplay simple results - rounded */
case class NameCalculation(name: String, value: Long)
object NameCalculation {
  implicit val jsonWriter = Json.writes[NameCalculation]
  implicit val jsonReader = Json.reads[NameCalculation]
}

/** some nonsense data point */
case class GeneratedMessage(id: Long, name: String, size: Long, color: String, score: Long, timestamp: Long)
object GeneratedMessage {
  implicit val jsonWriter = Json.writes[GeneratedMessage]
  implicit val jsonReader = Json.reads[GeneratedMessage]

  def maxScore(a: GeneratedMessage, b: GeneratedMessage): GeneratedMessage = {
    if (a.score >= b.score) a else b
  }

}

case class Results(leader: NameCalculation, colorCountList: List[NameCalculation])
object Results {
  implicit val jsonWriter = Json.writes[Results]
  implicit val jsonReader = Json.reads[Results]
}


/* constants and config */
object KSpeedBagModels {

  val CONF = ConfigFactory.load

  // Kafka
  val KAFKA_BROKER_LIST = CONF.getString("kafka.broker.list")
  val KAFKA_TOPIC_NAME = CONF.getString("kafka.topic.name")
  val KAFKA_GROUP_ID = CONF.getString("kafka.group.id")
  val ZOOKEEPER_CONNECT = CONF.getString("zookeeper.connect")
  val ZOOKEEPER_CONN_TIMEOUT = CONF.getLong("zookeeper.connection.timeout.ms")

  // Spark
  val SPARK_MASTER = CONF.getString("spark.master") // e.g. "local[2]"
  val SPARK_STREAM_WINDOW_SEC = CONF.getInt("spark.stream.window.seconds") // e.g. 2
  val SPARK_STREAM_APP_NAME = CONF.getString("spark.stream.app.name")
  val SPARK_STREAM_NUM_PARTITIONS = CONF.getInt("spark.stream.num.partitions.per.topic")

  // Load
  val MESSAGES_PER_SECOND = CONF.getInt("run.load.messages.per.second") // e.g. 10
  val MESSAGE_GENERATION_DURATION_SECONDS = CONF.getInt("run.generate.duration.seconds") // e.g. 60

  val SUBJECTS: Seq[Subject] = Seq(
    Subject(1, "Sue"),
    Subject(2, "Mike"),
    Subject(3, "Amit"),
    Subject(4, "Zhang"),
    Subject(5, "Bob"),
    Subject(6, "Xena"),
    Subject(7, "Amy"))

  val COLORS: Seq[String] = Seq(
    "red",
    "orange",
    "yellow",
    "yellow",
    "yellow",
    "green",
    "blue",
    "indigo",
    "violet",
    "violet")


}
