package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink17_Transform_Union {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val sensor1DS: DataStream[WaterSensor] = env.fromCollection(
      List(
        WaterSensor("sensor_1", 111111111L, 10),
        WaterSensor("sensor_2", 111111111L, 20),
        WaterSensor("sensor_1", 111111111L, 30),
        WaterSensor("sensor_2", 111111111L, 40),
        WaterSensor("sensor_1", 111111111L, 50)

      )
    )

    val sensor2DS: DataStream[WaterSensor] = env.fromCollection(
      List(
        WaterSensor("sensor_3", 111111111L, 60),
        WaterSensor("sensor_1", 111111111L, 70),
        WaterSensor("sensor_2", 111111111L, 80),
        WaterSensor("sensor_1", 111111111L, 90),
        WaterSensor("sensor_2", 111111111L, 100)
      )
    )

    val sensor3DS: DataStream[WaterSensor] = env.fromCollection(
      List(
        WaterSensor("sensor_3", 111111111L, 160),
        WaterSensor("sensor_2", 111111111L, 170),
        WaterSensor("sensor_2", 111111111L, 180),
        WaterSensor("sensor_2", 111111111L, 190),
        WaterSensor("sensor_2", 111111111L, 1100)
      )
    )

   val resultDS: DataStream[WaterSensor] = sensor1DS
     .union(sensor2DS)
     .union(sensor3DS)


    resultDS.print()

    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
