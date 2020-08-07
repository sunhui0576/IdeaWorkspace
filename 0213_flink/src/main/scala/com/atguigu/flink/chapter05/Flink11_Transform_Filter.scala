package com.atguigu.flink.chapter05

import org.apache.flink.api.common.functions.{FilterFunction, FlatMapFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink11_Transform_Filter {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val dataDS: DataStream[Int] = env.fromCollection(
      List(1, 2, 3, 4, 5, 6, 7)
    )

    //    val filterDS: DataStream[Int] = dataDS.filter(
    //      data => data % 2 != 1
    //    )
    val filterDS: DataStream[Int] = dataDS.filter(
      new FilterFunction[Int] {
        override def filter(value: Int): Boolean = {
          value % 2 != 1
        }
      }
    )

    filterDS.print()


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
