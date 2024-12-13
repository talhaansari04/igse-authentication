package com.igse

import io.gatling.core.scenario.Simulation;
import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.language.postfixOps
import scala.concurrent.duration._

class PerformanceTests extends Simulation {

  val config: Config = ConfigFactory.load

  setUp(
    scenario("get-employee")
      //.feed(csv("employeeId-feeder.csv").circular)
      .exec(Requests.getEmployee)
      .inject(
        rampUsers(config.getInt("num-users")) during
          (config.getInt("user-ramp-seconds") seconds)))
    .maxDuration(config.getInt("test-duration-seconds") seconds)
    .protocols(
      http
        .header(HttpHeaderNames.Accept, HttpHeaderValues.ApplicationJson)
        .header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson)
        .baseUrl(s"${config.getString("host")}")
    )
}