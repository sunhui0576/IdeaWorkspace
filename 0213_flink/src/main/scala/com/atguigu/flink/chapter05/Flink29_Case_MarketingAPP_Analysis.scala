package com.atguigu.flink.chapter05

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

import scala.util.Random

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink29_Case_MarketingAPP_Analysis {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 1. 读取数据，并封装成样例类
    val marketDS: DataStream[MarketingUserBehavior] = env.addSource(new MarketingSource())

    // 2. 分组：按照 渠道 + behavior（下载）
    marketDS
      .filter(_.behavior == "DOWNLOAD")
      .map(data => (data.channel + "_" + data.behavior, 1L))
      .keyBy(_._1)
      .sum(1)
      .print("market")


    env.execute()

  }

  class MarketingSource extends SourceFunction[MarketingUserBehavior] {

    private var flag: Boolean = true

    private val behaviorList = List("INSTALL", "DOWNLOAD", "UNINSTALL", "UPDATE")

    private val channelList = List("HUAWEI", "XIAOMI", "OPPO")

    override def run(ctx: SourceFunction.SourceContext[MarketingUserBehavior]): Unit = {
      while (flag) {
        ctx.collect(
          MarketingUserBehavior(
            Random.nextInt(10),
            behaviorList(Random.nextInt(behaviorList.length)),
            channelList(Random.nextInt(channelList.length)),
            System.currentTimeMillis()
          )
        )
        Thread.sleep(500L)
      }
    }

    override def cancel(): Unit = {
      flag = false
    }
  }


  /**
    * 市场分析样例类
    *
    * @param userId    用户ID
    * @param behavior  用户行为: 下载、卸载、更新
    * @param channel   渠道：来自于哪个应用商店-- 小米、华为、oppo
    * @param timestamp 时间戳
    */
  case class MarketingUserBehavior(
                                    userId: Long,
                                    behavior: String,
                                    channel: String,
                                    timestamp: Long)

}
