name := "edcast_demo"

version := "1.0"

scalaVersion := "2.12.5"

enablePlugins(GatlingPlugin)

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.1" % "test"

libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.3.1" % "test"

libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.7"


