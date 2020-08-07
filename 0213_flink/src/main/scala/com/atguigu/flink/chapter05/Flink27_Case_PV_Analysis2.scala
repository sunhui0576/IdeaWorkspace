package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

import scala.collection.mutable

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink27_Case_PV_Analysis2 {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 1. 读取数据，并封装成样例类
    val pv2OneDS: DataStream[(String, Long)] = env
      .readTextFile("input/UserBehavior.csv")
      .flatMap(
        line => {
          val datas: mutable.ArrayOps[String] = line.split(",")
          if (datas(3) == "pv") {
            List(("pv", 1L))
          } else {
            Nil
          }
        }
      )

    pv2OneDS.keyBy(_._1).sum(1).print("pv by flatMap")


    env.execute()

  }


  /**
    * 用户行为数据样例类
    *
    * @param userId     用户ID
    * @param itemId     商品ID
    * @param categoryId 商品类目ID
    * @param behavior   用户的行为：点击、购买、收藏、喜欢
    * @param timestamp  时间戳
    */
  case class UserBehavior(userId: Long, itemId: Long, categoryId: Int, behavior: String, timestamp: Long)

}
