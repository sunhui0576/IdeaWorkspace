package com.atguigu.flink.chapter05

import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink25_Case_PV_Analysis {
  def main(args: Array[String]): Unit = {
    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
//"file://"+this.getClass.getResource("/register.log").getPath, 10
    // 1. 读取数据，并封装成样例类
    val userBehaviorDS: DataStream[UserBehavior] = env
      .readTextFile("E:\\WorkSpace-master\\IdeaWorkspace\\0213_flink\\input\\UserBehavior.csv")
//        .readTextFile("file://"+this.getClass.getResource("/UserBehavior.csv").getPath)
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
    val pv2OneDS: DataStream[(String, Long)] = filterDS.map( data => ("pv",1L))

    // 3.按照pv进行分组
    val pv2OneKS: KeyedStream[(String, Long), String] = pv2OneDS.keyBy(_._1)

    // 4.求和
    pv2OneKS.sum(1).writeAsText("./",WriteMode.OVERWRITE)


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
