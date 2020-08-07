package com.atguigu.flink.chapter05

import java.util

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink23_Sink_ES {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val sensorDS: DataStream[WaterSensor] = env
      .readTextFile("input/sensor-data.log")
      .map(
        line => {
          val datas: Array[String] = line.split(",")
          WaterSensor(datas(0), datas(1).toLong, datas(2).toInt)
        }
      )

    val config: FlinkJedisPoolConfig = new FlinkJedisPoolConfig.Builder()
      .setHost("hadoop102")
      .setPort(6379)
      .build()

    val httpHosts = new util.ArrayList[HttpHost]()
    httpHosts.add(new HttpHost("hadoop102",9200))
    httpHosts.add(new HttpHost("hadoop103",9200))
    httpHosts.add(new HttpHost("hadoop104",9200))

    val esSink: ElasticsearchSink[WaterSensor] = new ElasticsearchSink.Builder[WaterSensor](
      httpHosts,
      new ElasticsearchSinkFunction[WaterSensor] {
        override def process(element: WaterSensor, ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
          val sourceDataMap = new util.HashMap[String, String]()
          sourceDataMap.put("data", element.toString)
          val indexRequest: IndexRequest =
            Requests
              .indexRequest("sensopr")
              .`type`("readingData")
              .source(sourceDataMap)
          indexer.add(indexRequest)
        }
      }
    ).build()

    sensorDS.addSink(esSink)


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
