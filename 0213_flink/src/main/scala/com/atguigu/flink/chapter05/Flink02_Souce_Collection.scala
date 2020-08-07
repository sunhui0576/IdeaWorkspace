package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink02_Souce_Collection {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)


    // 从集合中读取数据
    val sourceDS: DataStream[Int] = env.fromCollection(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    )
    sourceDS.print()


    env.execute()

  }
}
