package json

import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonReadsExamples {
  def main(args: Array[String]): Unit = {
    read1()
    println("Now time for read2")
    read2()
    read3()
  }

  case class Player(name: String, age: Int)

  case class Location(lat: Double, long: Double)

  case class Team(teamName: String, players: List[Player], location: Location)

  val jsonString: String =
    """{
      |  "teamName" : "Real Madrid FC",
      |  "players" : [ {
      |    "name" : "Ronaldo",
      |    "age" : 36
      |  }, {
      |    "name" : "Modric",
      |    "age" : 30
      |  }, {
      |    "name" : "Bale",
      |    "age" : 27
      |  } ],
      |  "location" : {
      |    "lat" : 40.4168,
      |    "long" : 3.7038
      |  }
      |}
      |""".stripMargin

  // SimplePath \ and
  def read1(): Unit = {
    // JsValue is a trait representing any Json value
    // The JSON library has a case class extending JsValue to represent each valid JSON type
    // JsString, JsNumber, JsObject, JsArray, JsBoolean, JsNull

    // Json object provides utilities primarily for conversion to and from JsValue structures
    // Convert to a JsValue using string parsing
    val jsValue = Json.parse(jsonString)
    println((jsValue \ "teamName").as[String])
    println((jsValue \ "location" \ "lat").as[Double])
    println((jsValue \ "players" \ 0 \ "name").as[String])

    // we can use the apply methods also to get the same as above
    println("getting json values using apply")
    println(jsValue("teamName"))
    println(jsValue("location"))
    println(jsValue("players")(0)("name"))

    val validate: JsResult[String] = (jsValue \ "teamName").validate[String]
    validate match {
      case x: JsSuccess[String] => println(s"JsSuccess ${x.get}")
      case x: JsError => println(x.errors)
    }

    val names: Seq[JsValue] = jsValue \\ "name"
    println("The name field from the jvalue " + names.map(x => x.as[String]))
  }

  def read2(): Unit = {
    val json = Json.parse(jsonString)
    // JsPath represents a path into a JsValue structure, analogous to XPath for XML.
    // This is used for traversing JsValue structures
    // and in patterns for implicit converters
    val temp = (JsPath \ "location" \ "lat").read[Double]
    println(json.as[Double](temp))

    implicit val playerReads: Reads[Player] = ((JsPath \ "name").read[String]
      and (JsPath \ "age").read[Int]) (Player.apply _)
    /*
    val plreads:  Reads[Player] = ((JsPath \ "name").read[String]
      and (JsPath \ "age").read[Int]) (Player.apply _)
      val pljson = Json.parse("""{"name": "Raman Shastri", "age": 34}""")
      println(pljson.as[Player](plreads))
     */

    implicit val locationReads: Reads[Location] = (
      (JsPath \ "lat").read[Double] and (JsPath \ "long").read[Double]) (Location.apply _)
    /*
    val lreads:Reads[Location] = (
          (JsPath \ "lat").read[Double] and (JsPath \ "long").read[Double]) (Location.apply _)
          val aljson = Json.parse("""{"lat": 29.23, "long": 98.53}""")
          println(aljson.as[Location](lreads))
     */
    implicit val teamReads: Reads[Team] = (
      (JsPath \ "teamName").read[String] and
        (JsPath \ "players").read[List[Player]] and
        (JsPath \ "location").read[Location]) (Team.apply _)

    val teams = json.as[Team]
    println(s"teams in read2 is $teams")
  }

  def read3(): Unit = {
    val jValue: JsValue = Json.parse(jsonString)

    implicit val playerReads: Reads[Player] = Json.reads[Player]
    implicit val locationReads: Reads[Location] = Json.reads[Location]
    implicit val teamReads: Reads[Team] = Json.reads[Team]

    val teams = Json.fromJson[Team](jValue).get
    println(s"teams is $teams")
  }
}