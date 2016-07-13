package daniel.zolnai.marathon

/**
  * Main application.
  * Created by Daniel Zolnai on 2016-07-03.
  */
object MarathonEmailNotifier extends App with MainModule {
  zookeeperService.connectIfRequired()
  marathonService.connect()
}
