package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink26_Case_PV_Analysis1 {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 1. 读取数据，并封装成样例类
    val userBehaviorDS: DataStream[UserBehavior] = env
      .readTextFile("input/UserBehavior.csv")
      .map(
        line => {
          val datas: Array[String] = line.split(",")
          UserBehavior(
            datas(0).toLong,
            datas(1).toLong,
            datas(2).toInt,
            datas(3),
            datas(4).toLong
          )
        }
      )

    // 2.过滤出PV的行为
    val filterDS: DataStream[UserBehavior] = userBehaviorDS.filter(_.behavior == "pv")

    // 3.按照用户的行为分组
    val behavior2OneKS: KeyedStream[UserBehavior, String] = filterDS.keyBy(_.behavior)


    behavior2OneKS.process(
      new KeyedProcessFunction[String,UserBehavior,Long] {

        private var pvCount:Long = 0L

        override def processElement(value: UserBehavior, ctx: KeyedProcessFunction[String, UserBehavior, Long]#Context, out: Collector[Long]): Unit = {
          pvCount += 1L
          out.collect(pvCount)
        }
      }
    ).print("pv count")

    env.execute()

  }


  /**
    * 用户行为数据样例类
    *
    * @param userId 用户ID
    * @param itemId 商品ID
    * @param categoryId 商品类目ID
    * @param behavior 用户的行为：点击、购买、收藏、喜欢
    * @param timestamp  时间戳
    */
  case class UserBehavior(userId: Long, itemId: Long, categoryId: Int, behavior: String, timestamp: Long)

}
