package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.MarathonEmailNotifier.Config
import daniel.zolnai.marathon.storage.{LocalStorage, Storage, ZookeeperStorage}


/**
  * Service provider which is responsible for handling the saving and restoring of application state and configuration.
  * Created by Daniel Zolnai on 2016-07-04.
  */
class StorageService(val config: Config) {

  private val _storage: Storage = {
    if (config.zookeeperUrl.isDefined) {
      new ZookeeperStorage(config.zookeeperUrl.get)
    } else {
      new LocalStorage(config.localWorkingDirectory)
    }
  }



}
