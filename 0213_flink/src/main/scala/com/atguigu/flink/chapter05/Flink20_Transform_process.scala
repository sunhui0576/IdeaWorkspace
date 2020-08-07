package com.atguigu.flink.chapter05

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink20_Transform_process {
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

    // 自定义KeyedProcessFunction
    // 1.实现，指定泛型
//        K:key的类型
    //    I：输入数据的类型
    //    O：输出数据的类型
    // 2.

    // 求数据条数
    sensorKS.process(
      new KeyedProcessFunction[String,WaterSensor,Int] {

        private var totalVc:Int = 0
        //
        override def processElement(value: WaterSensor, ctx: KeyedProcessFunction[String, WaterSensor, Int]#Context, out: Collector[Int]): Unit = {
          totalVc += 1
          out.collect(totalVc)
        }
      }
    ).print("process")


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
