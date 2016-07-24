package daniel.zolnai.marathon

import daniel.zolnai.marathon.service.ZooKeeperService.ConnectCallback

/**
  * Main application.
  * Created by Daniel Zolnai on 2016-07-03.
  */
object MarathonEmailNotifier extends App with MainModule {
  if (zookeeperService.enabled) {
    zookeeperService.connectIfRequired(new ConnectCallback {
      override def becomeLeader(): Unit = {
        historyService.load()
        marathonService.connect()
      }
    })
  } else {
    historyService.load()
    marathonService.connect()
  }
}
