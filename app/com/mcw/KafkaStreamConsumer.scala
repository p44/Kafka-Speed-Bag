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

import models.{NameCalculation, GeneratedMessage, KSpeedBagModels}
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
//import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.sql.{Dataset, SQLContext}
//import org.apache.spark.sql.functions._
import play.api.libs.json.{JsArray, Json}
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.Encoder
//import org.apache.spark.sql.expressions.Aggregator
//import org.apache.spark.sql.TypedColumn

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object KafkaStreamConsumer {

  val master = KSpeedBagModels.SPARK_MASTER
  val appName = KSpeedBagModels.SPARK_STREAM_APP_NAME
  val windowSec: Int = KSpeedBagModels.SPARK_STREAM_WINDOW_SEC
  val topic = KSpeedBagModels.KAFKA_TOPIC_NAME
  val partitions: Int = KSpeedBagModels.SPARK_STREAM_NUM_PARTITIONS

  def init(): Unit = {
    Future {
      println(s"init stream consumption of $topic")
      val conf = new SparkConf().setMaster(master).setAppName(appName) // .set("spark.cassandra.connection.host", "127.0.0.1")
      val sc = new SparkContext(conf)
      val ssc = new StreamingContext(sc, Seconds(windowSec))
      //val sqlContext = new SQLContext(sc)

      val topicPartitions = (topic, partitions)

      val storage = StorageLevel.MEMORY_ONLY
      lazy val rInputStream: ReceiverInputDStream[(String, String)] =
        KafkaUtils.createStream(ssc, KSpeedBagModels.ZOOKEEPER_CONNECT, KSpeedBagModels.KAFKA_GROUP_ID, Map(topicPartitions), storage)

      rInputStream.foreachRDD { rdd =>
        processWithDataSets(rdd)
      }

      ssc.start()

      println(s"consuming $topic")

      ssc.awaitTermination()
    }
  }

  /**
   * http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.GroupedDataset
   * https://docs.cloud.databricks.com/docs/spark/1.6/examples/Dataset%20Aggregator.html
   *
   * // TODO introduce time window
   *
   * @param rdd
   */
  def processWithDataSets(rdd: RDD[(String, String)]): Unit = {
    val now = System.currentTimeMillis
    println("************ START *************")
    if (rdd.toLocalIterator.nonEmpty) {
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._

      val df = sqlContext.read.json(rdd.map(_._2))
      val ds = df.as[GeneratedMessage]
      ds.cache()

      val cnt = ds.count()
      val colorCountList = ds.groupBy(_.color).count().map { cc =>
        val raw = cc._2.toDouble / cnt.toDouble
        val percentColor = BigDecimal(raw).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        //println(s"percentColor for ${cc._1} rounded $percentColor")
        NameCalculation(cc._1, percentColor, now)
      }.collect().toList
      //val colorCountsList = dsColorCount.collect.toList.map(cc => NameCalculation(cc._1, cc._2))

      val highScorer = ds.reduce((a, b) => GeneratedMessage.maxScore(a, b))
      println("highScorer " + highScorer)
      val leader = NameCalculation(highScorer.name, highScorer.score, now)

      ds.unpersist()

      publishTheResults(colorCountList, leader)
    }

    println(now)
    println("************ END *************")
  }

  /**
   * Broadcast with Iteratee framework
   *
   * @param colorCountList
   * @param leader
   */
  def publishTheResults(colorCountList: List[NameCalculation], leader: NameCalculation): Unit = {
    ResultsBroadcast.pushToResultsChannel(models.Results(leader, colorCountList))
  }

}
