package com.atguigu.flink.wc

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala._
object Flink02_WordCount_BoundStream {
  def main(args: Array[String]): Unit = {
    //1.创建上下文对象
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //2.读取数据
    val lineDS: DataStream[String] = env.readTextFile("input/word.txt")

    //3.数据处理逻辑
    //3.1对读取的数据进行扁平化处理
    val wordDS: DataStream[String] = lineDS.flatMap(_.split(" "))
    //3.2转换成(word,1)
    val word2OneDS: DataStream[(String, Int)] = wordDS.map((_, 1))
    val keyDS: KeyedStream[(String, Int), Tuple] = word2OneDS.keyBy(0)
    val sumDS: DataStream[(String, Int)] = keyDS.sum(1)
    sumDS.print()
    env.execute("test flink")
  }
}
