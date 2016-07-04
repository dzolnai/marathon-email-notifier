package com.egeniq.marathon.storage

/**
  * Implementation of the storage interface with Zookeeper as the storage system.
  * Created by Daniel Zolnai on 2016-07-04.
  */
class ZookeeperStorage(val zookeeperUrl: String) extends Storage{

  override def saveToFile(path: String, content: String): Unit = ???

  override def getFileContents(path: String): String = ???
}
