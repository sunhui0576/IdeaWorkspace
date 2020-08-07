package com.atguigu.flink.chapter05

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink07_Transform_MapFunction {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val lineDS: DataStream[String] = env.readTextFile("input/sensor-data.log")


    val sensorDS: DataStream[WaterSensor] = lineDS.map(
      new MyMapFunction()
    )

    sensorDS.print()

    env.execute()

  }

  // 自定义MapFunction
  // 1. 混入 MapFunction，指定输入和输出的类型
  // 2. 重写map方法
  class MyMapFunction extends MapFunction[String, WaterSensor] {
    override def map(line: String): WaterSensor = {
      val datas: Array[String] = line.split(",")
      WaterSensor(datas(0), datas(1).toLong, datas(2).toInt)
    }
  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
