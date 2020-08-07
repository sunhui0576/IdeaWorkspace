package com.atguigu.flink.chapter05

import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink24_Sink_MySQL {
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


    sensorDS.addSink(new MySQLSink())


    env.execute()

  }

  class MySQLSink extends RichSinkFunction[WaterSensor] {

    var connection: Connection = _
    var pstmt: PreparedStatement = _


    override def open(parameters: Configuration): Unit = {
      connection = DriverManager.getConnection(
        "jdbc:mysql://hadoop102:3306/test",
        "root",
        "000000"
      )

      pstmt = connection.prepareStatement("INSERT INTO sensor VALUES(?,?,?)")
    }

    override def close(): Unit = {
      pstmt.close()
      connection.close()
    }

    override def invoke(value: WaterSensor, context: SinkFunction.Context[_]): Unit = {
      pstmt.setString(1,value.id)
      pstmt.setLong(2,value.ts)
      pstmt.setInt(3,value.vc)
      pstmt.execute()
    }
  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
