package com.atguigu.flink.wc

import org.apache.flink.api.scala._

object Flink01_WordCount_Batch {
  def main(args: Array[String]): Unit = {
    //1.创建上下文对象
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //2.读取数据
    val lineDS: DataSet[String] = env.readTextFile("input/word.txt")

    //3.数据处理逻辑
    //3.1对读取的数据进行扁平化处理
    val wordDS: DataSet[String] = lineDS.flatMap(_.split(" "))
    //3.2转换成(word,1)
    val word2OneDS: DataSet[(String, Int)] = wordDS.map((_, 1))
    val groupDS: GroupedDataSet[(String, Int)] = word2OneDS.groupBy(0)
    val sumDS: AggregateDataSet[(String, Int)] = groupDS.sum(1)
    sumDS.print()
  }
}
