package com.atguigu.flink.chapter05

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink19_Transform_Operator2 {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

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

    val sensorKS: KeyedStream[WaterSensor, String] = sensorDS.keyBy(_.id)

//    sensorKS.reduce(
//      (sensor1, sensor2) => {
//        WaterSensor(sensor1.id, System.currentTimeMillis(), sensor1.vc + sensor2.vc)
//      }
//    ).print("reduce")

    sensorKS.reduce(
      new ReduceFunction[WaterSensor]{
        override def reduce(value1: WaterSensor, value2: WaterSensor): WaterSensor = {
          WaterSensor(value1.id, System.currentTimeMillis(), value1.vc + value2.vc)
        }
      }
    ).print("reduce function")


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
