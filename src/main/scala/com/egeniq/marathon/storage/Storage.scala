package com.egeniq.marathon.storage

/**
  * Interface for saving and restoring application contents.
  * Created by Daniel Zolnai on 2016-07-03.
  */
trait Storage {

  def saveToFile(path: String, content: String)

  def getFileContents(path: String) : String

}
