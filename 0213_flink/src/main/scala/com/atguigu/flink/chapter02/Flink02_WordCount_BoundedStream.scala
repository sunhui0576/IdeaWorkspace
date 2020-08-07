package com.atguigu.flink.chapter02

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/4 9:40
  */
object Flink02_WordCount_BoundedStream {
  def main(args: Array[String]): Unit = {

    // 1.创建执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 2.读取数据
//    val lineDS: DataStream[String] = env.readTextFile("F:\\atguigu\\01_course\\code\\bigdata\\flink0213\\src\\main\\resources\\word.txt")
    val lineDS: DataStream[String] = env.readTextFile("/opt/module/data/input/word.txt")

    // 3.对数据进行处理
    // 3.1 扁平化操作
    val wordDS: DataStream[String] = lineDS.flatMap(_.split(" "))
    // 3.2 转换数据格式（word，1）
    val word2OneDS: DataStream[(String, Int)] = wordDS.map((_,1))
    // 3.3 按照word进行分组
    val keyDS: KeyedStream[(String, Int), Tuple] = word2OneDS.keyBy(0)
    // 3.4 求和
    val sumDS: DataStream[(String, Int)] = keyDS.sum(1)

    // 4.执行
    sumDS.print()

    // 5.启动执行环境
    env.execute()

  }
}
