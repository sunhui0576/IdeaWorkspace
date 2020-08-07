package com.atguigu.flink.chapter05

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink09_Transform_FlatMap {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val dataDS: DataStream[List[Int]] = env.fromCollection(
      List(
        List(1, 2, 3, 4),
        List(5, 6, 7)
      )
    )

    val resultDS: DataStream[Int] = dataDS.flatMap( list => list)

    resultDS.print()

    env.execute()

  }

  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
