package com.edcast.util

import java.io.FileInputStream
import java.util.Properties

object EnvSettings {

  def loadEnv(env: String): Map[String, String] = env match {

    case "demo" => {
      val properties = new Properties()
      properties.load(new FileInputStream("src/main/resources/demo.properties"))
      Map("host" -> properties.getProperty("host"),
        "url" -> properties.getProperty("url"),
        "pathToUsers" -> properties.getProperty("pathToUsers"))
    }

    case _ => throw new NoSuchElementException("Environment's properties do not exist")
  }

}
