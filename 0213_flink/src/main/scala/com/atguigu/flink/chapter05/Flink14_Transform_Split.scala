package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink14_Transform_Split {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)

    val sensorDS: DataStream[WaterSensor] = env.fromCollection(
      List(
        WaterSensor("sensor_1", 111111111L, 10),
        WaterSensor("sensor_2", 111111111L, 20),
        WaterSensor("sensor_1", 111111111L, 30),
        WaterSensor("sensor_2", 111111111L, 40),
        WaterSensor("sensor_1", 111111111L, 50),
        WaterSensor("sensor_3", 111111111L, 60),
        WaterSensor("sensor_1", 111111111L, 70),
        WaterSensor("sensor_2", 111111111L, 80),
        WaterSensor("sensor_1", 111111111L, 90),
        WaterSensor("sensor_2", 111111111L, 100)
      )
    )


    val splitSS: SplitStream[WaterSensor] = sensorDS.split(
      sensor => {
        if (sensor.vc < 50) {
          Seq("normal")
        } else if (sensor.vc < 80) {
          Seq("warning")
        } else {
          Seq("alarm")
        }
      }
    )

    splitSS


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
