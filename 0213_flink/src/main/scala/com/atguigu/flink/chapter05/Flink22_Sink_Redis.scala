package com.atguigu.flink.chapter05

import com.atguigu.flink.chapter05.Flink06_Transform_Map.WaterSensor
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink22_Sink_Redis {
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

    sensorDS.addSink(
      new RedisSink[WaterSensor](
        config,
        new RedisMapper[WaterSensor] {
          override def getCommandDescription: RedisCommandDescription = {
            new RedisCommandDescription(RedisCommand.HSET, "sensor")
          }

          override def getKeyFromData(data: WaterSensor): String = data.id

          override def getValueFromData(data: WaterSensor): String = data.vc.toString
        }
      )
    )


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
