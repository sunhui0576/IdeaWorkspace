package com.atguigu.flink.chapter05

import com.atguigu.flink.chapter05.Flink06_Transform_Map.WaterSensor
import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.scala._

/**
  * TODO
  *
  * @version 1.0
  * @author create by cjp on 2020/8/5 10:27
  */
object Flink12_Transform_KeyBy {
  def main(args: Array[String]): Unit = {

    // 流处理执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(3)

    val lineDS: DataStream[String] = env.readTextFile("input/sensor-data.log")

    val sensorDS: DataStream[WaterSensor] = lineDS.map(
      line => {
        val datas: Array[String] = line.split(",")
        WaterSensor(datas(0), datas(1).toLong, datas(2).toInt)
      }
    )

    //    val sensorKS: KeyedStream[WaterSensor, Tuple] = sensorDS.keyBy(0)
    //    val sensorKS: KeyedStream[WaterSensor, Tuple] = sensorDS.keyBy("id")
    //    val sensorKS: KeyedStream[WaterSensor, String] = sensorDS.keyBy(_.id)

    val sensorKS: KeyedStream[WaterSensor, String] = sensorDS.keyBy(
      new KeySelector[WaterSensor, String] {
        override def getKey(value: WaterSensor): String = {
          value.id
        }
      }
    )
    sensorKS
    sensorKS.print()


    env.execute()

  }


  // 定义样例类：水位传感器：用于接收空高数据
  // id:传感器编号
  // ts:时间戳
  // vc:空高
  case class WaterSensor(id: String, ts: Long, vc: Int)

}
