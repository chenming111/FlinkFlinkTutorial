package com.atguigu.apitest.sinktest

import java.util.Properties

import com.atguigu.apitest.SensorReading
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, FlinkKafkaProducer011}

/**
  * Copyright (c) 2018-2028 尚硅谷 All Rights Reserved 
  *
  * Project: FlinkTutorial
  * Package: com.atguigu.apitest.sinktest
  * Version: 1.0
  *
  * Created by wushengran on 2019/9/17 15:43
  */
object KafkaSinkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // source
//    val inputStream = env.readTextFile("D:\\Projects\\BigData\\FlinkTutorial\\src\\main\\resources\\sensor.txt")
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "consumer-group")
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("auto.offset.reset", "latest")

    val inputStream = env.addSource(new FlinkKafkaConsumer011[String]("sensor", new SimpleStringSchema(), properties))

    // Transform操作

    val dataStream = inputStream
      .map(
        data => {
          val dataArray = data.split(",")
          SensorReading( dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble ).toString  // 转成String方便序列化输出
        }
      )

    // sink
    dataStream.addSink( new FlinkKafkaProducer011[String]("localhost:9092", "sinkTest", new SimpleStringSchema()) )
    dataStream.print()

    env.execute("kafka sink test")
  }
}
