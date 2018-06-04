package com.edcast.load

import com.edcast.util.EnvSettings
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class EdcastDemoSimulation extends Simulation {

  val env = "demo"
  val numberOfUsers = 100
  val UsersDuration = numberOfUsers * 2 seconds

  val envProperties = EnvSettings.loadEnv(env)
  val users = envProperties.get("pathToUsers").get
  val url = envProperties.get("url").get

  val httpProtocol = http
    .baseURL(url)
    .inferHtmlResources()

  val user = csv(users).circular

  val headers = Map(
    "Content-Type" -> "application/json",
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "cache-control" -> "no-cache",
    "content-type" -> "application/json",
    "origin" -> "https://alexander.cmnetwork.co",
    "pragma" -> "no-cache",
    "referer" -> "https://alexander.cmnetwork.co/",
    "user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")


  val headers_0 = Map(
    "Content-Type" -> "application/json",
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "cache-control" -> "no-cache",
    "content-type" -> "application/json",
    "origin" -> "https://alexander.cmnetwork.co",
    "pragma" -> "no-cache",
    "user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36",
    "x-api-token" -> "${jwtToken}",
    "x-edcast-jwt" -> "true")

  val headers_1 = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "cache-control" -> "no-cache",
    "pragma" -> "no-cache",
    "user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36")

  val auth = """{"user":{"email":"${email}","password":"${password}"}}"""
  val pathway = """{"card":{"message":"${pathway}","title":"${pathway}","state":"draft","card_type":"pack","card_subtype":"simple","users_with_access_ids":[],"team_ids":[],"auto_complete":true,"is_public":"true","topics":[],"channel_ids":[],"filestack":[{"mimetype":"image/jpeg","size":261132,"source":"local_file_system","url":"https://cdn.filestackcontent.com/45p2d7jGQPumPkPPfwlL","handle":"45p2d7jGQPumPkPPfwlL","status":"Stored"}]}}"""
  val card = """{"resource":{"link":"${card}"}}"""
  val newCard = """{"card":{"message":"${message}","users_with_access_ids":[],"channel_ids":[],"team_ids":[],"is_public":"true","prices_attributes":[],"title":"","readable_card_type_name":"","resource_id":${idRes},"topics":[],"duration":0}}"""
  val addCardToPathway = """{"authenticity_token":"${authenticity_token}","pathway_ids":[${idPathway}]}"""
  val likeCard = """{"vote":{"content_id":"${idCard}","content_type":"Card"}}"""
  val delete = """{"delete_dependent":"true"}"""


  val scn = scenario("Edcast Demo Simulation")
    .feed(user)
    .exec(
      http("login")
        .post("/auth/users/sign_in.json")
        .headers(headers)
        .body(StringBody(auth)).asJSON
        .check(status is (200))
        .check(regex("""\"jwtToken\":\"(.*?)\"""").find(0).saveAs("jwtToken"))
        .check(regex("""\"csrfToken\":\"(.*?)\"""").find(0).saveAs("authenticity_token"))
    )
    .exec(
      http("create pathway")
        .post("/api/v2/cards")
        .headers(headers_0)
        .body(StringBody(pathway)).asJSON
        .check(status is (200))
        .check(regex("""\"id\":\"(.*?)\"""").find(0).saveAs("idPathway"))
    )
    .exec(
      http("create card_resource")
        .post("/api/v2/resources.json")
        .headers(headers_0)
        .body(StringBody(card)).asJSON
        .check(status is (200))
        .check(regex("""\"id\":(.*?),""").find(0).saveAs("idRes"))
    )
    .exec(
      http("create card")
        .post("/api/v2/cards")
        .headers(headers_0)
        .body(StringBody(newCard)).asJSON
        .check(status is (200))
        .check(regex("""\"id\":\"(.*?)\"""").find(0).saveAs("idCard"))
    )
    .exec(
      http("add_to_pathways")
        .post("/api/v2/cards/${idCard}/add_to_pathways")
        .headers(headers_0)
        .body(StringBody(addCardToPathway)).asJSON
        .check(status is (200))
    )
    .exec(
      http("like card")
        .post("/api/v2/votes.json")
        .headers(headers_0)
        .body(StringBody(likeCard)).asJSON
        .check(status is (200))

    )
    .exec(
      http("search for user")
        .get("/api/v2/users.json?q=${user}&limit=21")
        .headers(headers_0)
        .check(status is (200))
        .check(regex("""\"id\":(.*?),\"handle\"""").findAll.saveAs("persons_ids"))
    )
    .exec(
      http("search for card")
        .get("/api/v2/search.json?q=${message}&limit=12&cards_limit=39&segregate_pathways=true&load_tags=false&load_mentions=false")
        .headers(headers_0)
        .check(status is (200))
        .check(regex("""\"id\":\"(.*?)\",\"cardType\"""").findAll.saveAs("cards_ids"))
    )
    .exec(
      http("delete card")
        .delete("/api/v2/cards/${idCard}.json")
        .headers(headers_0)
        .body(StringBody(delete))
        .check(status is (200))
    )
    .exec(
      http("delete pathway")
        .delete("/api/v2/cards/${idPathway}.json")
        .headers(headers_0)
        .body(StringBody(delete))
        .check(status is (200))
    )
    .exec(
      http("sign out")
        .get("/sign_out")
        .headers(headers_1)
        .check(status is (200))
    )


  setUp(scn.inject(rampUsers(numberOfUsers) over (200 seconds))).protocols(httpProtocol)
}
