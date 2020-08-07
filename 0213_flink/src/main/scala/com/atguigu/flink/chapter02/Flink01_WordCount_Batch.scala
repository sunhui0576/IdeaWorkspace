package com.atguigu.flink.chapter02

import org.apache.flink.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/4 9:29
  */
object Flink01_WordCount_Batch {
  def main(args: Array[String]): Unit = {

    // 1.创建执行环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    // 2.读取数据
    val lineDS: DataSet[String] = env.readTextFile("E:\\WorkSpace-master\\IdeaWorkspace\\0213_flink\\input\\word.txt")
//    val lineDS: DataSet[String] = env.readTextFile("hdfs://hadoop102/input/word.txt")

    // 3.数据的逻辑处理
    // 3.1 对读取的数据进行扁平化处理
    val wordDS: DataSet[String] = lineDS.flatMap(_.split(" "))
    // 3.2 转换成（word，1）格式
    val word2OneDS: DataSet[(String, Int)] = wordDS.map((_, 1))
    // 3.3 按照word进行分组
    // java.lang.UnsupportedOperationException: Aggregate does not support grouping with KeySelector functions, yet.
    val groupDS: GroupedDataSet[(String, Int)] = word2OneDS.groupBy(0)
    // 3.4 按照分组结果进行求和
    val sumDS: AggregateDataSet[(String, Int)] = groupDS.sum(1)

    // 4.执行
    sumDS.print()

  }
}
