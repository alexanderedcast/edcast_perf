package com.edcast.util

import java.io.FileInputStream
import java.util.Properties

object EnvSettings {

  def loadEnv(env: String): Map[String, String] = env match {

    case env => {
      val properties = new Properties()
      properties.load(new FileInputStream("src/main/resources/" + env + ".properties"))
      Map(
        "host" -> properties.getProperty("host"),
        "numberOfUsers" -> properties.getProperty("numberOfUsers"))
    }

    case _ => throw new NoSuchElementException("Environment's properties do not exist")
  }

}
