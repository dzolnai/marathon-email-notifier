package com.egeniq.marathon

import com.egeniq.marathon.entity.Param

/**
  * Main application.
  * Created by Daniel Zolnai on 2016-07-03.
  */
object MarathonEmailNotifier {

  val APPLICATION_NAME = "Marathon Email Notifier"


  object Params {
    val ZOOKEEPER_URL = Param('z', "zookeeper-url",
      "The Zookeeper hostname, port, and path. Leaving this empty will result in the notifier running " +
        "on the local host, saving all configuration and states on the local filesystem.")
    val LOCAL_WORKING_DIRECTORY = Param('l', "local-working-directory",
    "The local file system where the configuration files and application state will be stored. Only used if no " +
      "Zookeeper URL was provided.")
    val IMPORT_CONFIG = Param('i', "import-config",
    "Path to a configuration file. This parameter should be used together with 'zookeeper-url' to upload a new config " +
      "in Zookeeper. If not using Zookeeper, this command should always be provided")
  }


  object Defaults {
    val zookeeperUrl = None
    val localWorkingDirectory = "/opt/marathon-app-notifier/"
    val importConfig = None
  }

  case class Config(zookeeperUrl: Option[String] = Defaults.zookeeperUrl,
                    localWorkingDirectory: String = Defaults.localWorkingDirectory,
                    importConfig: Option[String] = Defaults.importConfig)

  /**
    * Application entry point.
    *
    * @param args The command line arguments to be parsed by scopt.
    */
  def main(args: Array[String]): Unit = {
    val config = _parseConfig(args)
    _validateConfig(config)
    if (config.importConfig.isDefined && config.zookeeperUrl.isDefined) {
      // TODO upload new configuration to Zookeeper
    }
    // TODO start application
  }

  /**
    * Validates the configuration provided by the user.
    * @param config The parsed configuration.
    */
  private def _validateConfig(config: Config) = {
    if (config.zookeeperUrl.isEmpty) {
      assert(config.importConfig.isDefined, s"Please provide a path to the configuration " +
        s"using the '--${Params.IMPORT_CONFIG.longName}' parameter!")
    }
  }


  /** *
    * Parses the CLI arguments and returns a Config object which contains the
    * final configuration for the job.
    *
    * @param args The command line arguments as a string array.
    * @return The configuration to be used.
    */
  private def _parseConfig(args: Array[String]): Config = {
    val parser = new scopt.OptionParser[Config](APPLICATION_NAME) {
      head(APPLICATION_NAME, getClass.getPackage.getImplementationVersion)
      opt[String](Params.ZOOKEEPER_URL.char, Params.ZOOKEEPER_URL.longName) action { (value, config) =>
        config.copy(zookeeperUrl = Some(value))
      } text Params.ZOOKEEPER_URL.description
      opt[String](Params.LOCAL_WORKING_DIRECTORY.char, Params.LOCAL_WORKING_DIRECTORY.longName) action { (value, config) =>
        config.copy(zookeeperUrl = Some(value))
      } text Params.LOCAL_WORKING_DIRECTORY.description
    }
    // parser.parse returns Option[Config]
    parser.parse(args, Config()) match {
      case Some(config) =>
        // do stuff
        config
      case None =>
        // arguments are bad, error message will have been displayed
        System.exit(1)
        null
    }
  }
}
