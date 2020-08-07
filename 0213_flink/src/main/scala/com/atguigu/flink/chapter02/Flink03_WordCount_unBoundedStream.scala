package com.atguigu.flink.chapter02

import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/4 10:06
  */
object Flink03_WordCount_unBoundedStream {
  def main(args: Array[String]): Unit = {

    // 1.创建流式执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


    // 2.读取数据：socket、kafka
    val lineDS: DataStream[String] = env.socketTextStream("localhost", 9999)

    // 3.数据逻辑处理
    // 3.1 对每一行数据进行扁平化操作
    val wordDS: DataStream[String] = lineDS.flatMap(_.split(" "))
    // 3.2 转换成（word，1）数据格式
    val word2OneDS: DataStream[(String, Int)] = wordDS.map((_, 1))
    // 3.3 按照word进行分组：keyBy
    val wordKS: KeyedStream[(String, Int), String] = word2OneDS.keyBy(_._1)
    // 3.4 根据分组结果进行求和
    val sumDS: DataStream[(String, Int)] = wordKS.sum(1)

    // 4. 执行
    sumDS.print()

    // 5. 启动执行环境
    env.execute()
  }
}
