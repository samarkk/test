package json

import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonWritesExamples {
  def main(args: Array[String]): Unit = {
    write1
    write2()
    write3
    write4
    write5
  }

  case class Player(name: String, age: Int)

  case class Location(lat: Double, long: Double)

  case class Team(teamName: String, players: List[Player], location: Location)

  val team: Team = Team("Real Madrid FC", List(
    Player("Ronaldo", 31),
    Player("Modric", 30),
    Player("Bale", 27)), Location(40.4168, 3.7038))

  private def write1 = {
    println("write 1 testing")
    val json: JsValue = JsObject(
      Seq(
        "teamName" -> JsString("Real Madrid FC"),
        "players" -> JsArray(
          Seq(
            JsObject(
              Seq(
                "name" -> JsString("Modric"),
                "age" -> JsNumber(28))),
            JsObject(
              Seq(
                "name" -> JsString("Bale"),
                "age" -> JsNumber(28))))),
        "location" -> JsObject(
          Seq(
            "lat" -> JsNumber(40.4168),
            "long" -> JsNumber(3.7038)))))
    println(json.toString)
  }

  private def write2(): Unit = {
    println("write 2 testing")
    val json: JsValue = Json.obj(
      "teamName" -> JsString("Real Madrid FC"),
      "players" -> Json.arr(
        Json.obj(
          "name" -> JsString("Modric"),
          "age" -> JsNumber(28)),
        Json.obj(
          "name" -> JsString("Bale"),
          "age" -> JsNumber(28))),
      "location" -> Json.obj(
        "lat" -> JsNumber(40.4168),
        "long" -> JsNumber(3.7038)))
    println(json.toString)
  }

  private def write3 = {
    val json: JsValue = Json.obj(
      "teamName" -> team.teamName,
      "players" -> Json.arr(
        team.players.map(x => Json.obj(
          "name" -> x.name,
          "age" -> x.age))),
      "location" -> Json.obj(
        "lat" -> team.location.lat,
        "long" -> team.location.long))
    println(s"Write 3 \n ${json.toString()}")
  }

  private def mchwrite() = {

  }

  private def write4(): Unit = {
    implicit val locationWrites: Writes[Location] = (
      (JsPath \ "lat").write[Double] and (JsPath \ "long").write[Double]) (unlift(Location.unapply))
    /*
    val lwrites: Writes[Location] = (
          (JsPath \ "lat").write[Double] and (JsPath \ "long").write[Double])(unlift(Location.unapply))
        val aloc = Location(23.457, 35.55)
        Json.toJson(aloc)(lwrites)
     */
    implicit val playerWrites: Writes[Player] = (
      (JsPath \ "name").write[String] and (JsPath \ "age").write[Int]) (unlift(Player.unapply))
    /*
val plwrites: Writes[Player] = ((JsPath \ "name").write[String] and (JsPath \ "age").write[Int])(unlift(Player.unapply))
val aplr = Player("Raman Grunting", 49)
Json.toJson(aplr)(plwrites)
     */
    implicit val teamWrites: Writes[Team] = (
      (JsPath \ "teamName").write[String] and
        (JsPath \ "players").write[List[Player]] and
        (JsPath \ "location").write[Location]) (unlift(Team.unapply))
    println(s"Write 4 ${Json.toJson(team).toString()}")
  }

  def write5 = {
    implicit val playerWrites: OWrites[Player] = Json.writes[Player]
    implicit val locationWrites: OWrites[Location] = Json.writes[Location]
    implicit val teamWrites: OWrites[Team] = Json.writes[Team]
    println(s"write 5  ${Json.toJson(team).toString()}")
  }
}
