package com.atguigu.flink.chapter05

import com.atguigu.flink.chapter05.Flink25_Case_PV_Analysis.UserBehavior
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
object Flink28_Case_UV_Analysis {
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

    // 2.过滤出pv行为
    val filterDS: DataStream[UserBehavior] = userBehaviorDS.filter(_.behavior == "pv")

    // 3.转换数据格式（uv，用户id）
    //  第一个给uv，是为了keyBy后到一个分组里，调用分组的聚合算子
    //  因为后面要用用户id去重，所以不能给个1，要给用户id
    val uvDS: DataStream[(String, Long)] = filterDS.map( data => ("uv",data.userId))

    // 4.根据uv进行分组
    val uvKS: KeyedStream[(String, Long), String] = uvDS.keyBy(_._1)

    // 5.自定义process，使用Set对userId进行去重，Set的长度就是uv数
    uvKS.process(
      new KeyedProcessFunction[String,(String,Long),Long] {

        private var uvSet = mutable.Set[Long]()
        override def processElement(value: (String, Long), ctx: KeyedProcessFunction[String, (String, Long), Long]#Context, out: Collector[Long]): Unit = {
          uvSet.add(value._2)
          out.collect(uvSet.size)
        }
      }
    ).print("uv")

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
