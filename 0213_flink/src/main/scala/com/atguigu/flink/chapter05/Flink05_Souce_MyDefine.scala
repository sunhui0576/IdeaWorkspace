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
object Flink05_Souce_MyDefine {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)


    // 从自定义数据源读取数据
    val dataDS: DataStream[WaterSensor] = env.addSource( new MySourceFunction())


    dataDS.print()


    env.execute()

  }


  // 自定义SourceFunction
  // 1、实现（混入）SourceFunction，指定泛型
  // 2、重写run和cancel方法
  class MySourceFunction extends SourceFunction[WaterSensor] {

    // @volatile类型修饰符，  内存可见性，Java
    @volatile var flag:Boolean = true

    override def run(ctx: SourceFunction.SourceContext[WaterSensor]): Unit = {
      while (flag) {
        val sensor = WaterSensor("sensor_" + Random.nextInt(3), System.currentTimeMillis(), 40 + Random.nextInt(10))
        ctx.collect(sensor)
        Thread.sleep(1000L)
      }
    }

    override def cancel(): Unit = {
      flag = false
    }
  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
