package com.atguigu.flink.chapter05

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink01_Environment {
  def main(args: Array[String]): Unit = {


    // 批处理执行环境
    val batchEnv: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 流处理执行环境
    val streamEnv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
  }
}
