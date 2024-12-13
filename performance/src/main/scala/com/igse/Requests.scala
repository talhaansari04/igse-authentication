package com.igse

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object Requests {

  val config: Config = ConfigFactory.load

  val getEmployee: HttpRequestBuilder = http("get-employee-by-id")
    .post(config.getString("uri-get-employee"))
    .body(ElFileBody("Request/login.json"))
    .check(status.is(200))

  val healthCheck: HttpRequestBuilder = http("health-check")
    .get(config.getString("uri-health"))
    .check(status.is(200))
}
