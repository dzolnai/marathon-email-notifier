package com.egeniq.marathon

/**
  * Main application.
  * Created by Daniel Zolnai on 2016-07-03.
  */
class MarathonEmailNotifier {

  case class Config(
                     zookeeperUrl: Option[String] = Defaults.zookeeperUrl
                   )

  object Defaults {
    val zookeeperUrl = None
  }

  def main(args: Array[String]): Unit = {

  }
}
