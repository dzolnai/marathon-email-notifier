package daniel.zolnai.marathon.entity.event

import daniel.zolnai.marathon.entity.event.StatusUpdateEvent.Status.Status

/**
  * Base event for a change in a job status.
  * This will send a failed status when there's a problem with a job.
  * Created by Daniel Zolnai on 2016-07-14.
  */
object StatusUpdateEvent {

  object Status extends Enumeration {
    type Status = Value
    val TASK_RUNNING, TASK_FAILED = Value
  }

}

class StatusUpdateEvent extends MarathonEvent {
  var slaveId: String = _
  var taskId: String = _
  var taskStatus: Status = _
  var message: String = _
  var appId: String = _
  var host: String = _
  var ports: List[Long] = _
  var version: String = _
}
