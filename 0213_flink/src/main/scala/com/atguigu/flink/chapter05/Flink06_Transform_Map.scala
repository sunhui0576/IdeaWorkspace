package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

import scala.util.Random

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink06_Transform_Map {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val lineDS: DataStream[String] = env.readTextFile("input/sensor-data.log")


    val sensorDS: DataStream[WaterSensor] = lineDS.map(
      line => {
        val datas: Array[String] = line.split(",")
        WaterSensor(datas(0), datas(1).toLong, datas(2).toInt)
      }
    )
    sensorDS.print()

    env.execute()

  }

  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
