package alexa_skill_json

import scala.io.Source

// import io.circe.parser.parse
// import io.circe.generic.JsonCodec
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration

object AlexaSkillJsonDemo extends App {
  implicit val customConfig: Configuration = Configuration.default.withDefaults

  def parseResponseStringDemo(): Unit = {
    val responseJsonRaw = Source.fromFile("test.nocm/flask_response_body.json").getLines.mkString
    // println(responseJsonRaw)
    val parseResponseResult = decode[AlexaSkillResponse](responseJsonRaw)
    println("=" * 80)
    println("=" + "parseResponseStringDemo: ")
    println(parseResponseResult)
  }

  def createResponseStringDemo(): Unit = {
    val responseObj = AlexaSkillResponse(
      version="1.0",
      sessionAttributes = Map.empty[String, String],
      response=AlexaSkillResponseResponse(
        outputSpeech=AlexaSkillOutputSpeech("PlainText", "shutdown"),
        card=AlexaSkillCard("Simple", "Activity", "shutdown"),
        shouldEndSession=true))

    println("=" * 80)
    println("=" + "createResponseStringDemo: ")
    println("Object: ")
    println(responseObj)
    println("=" * 10)
    println("Object as json: ")
    println(responseObj.asJson)
  }

  def parseRequestStringDemo(): Unit = {
    val requestJsonRaw = Source.fromFile("test.nocm/flask_request_body.json").getLines.mkString
    // println(requestJsonRaw)
    val parseRequestResult = decode[AlexaSkillRequest](requestJsonRaw)
    println("=" * 80)
    println("=" + "parseRequestStringDemo: ")
    println(parseRequestResult)
  }

  def parseRequestEndStringDemo(): Unit = {
    val requestJsonRaw = Source.fromFile("test.nocm/flask_request_end_body.json").getLines.mkString
    // println(requestJsonRaw)
    val parseRequestResult = decode[AlexaSkillRequest](requestJsonRaw)
    println("=" * 80)
    println("=" + "parseRequestEndStringDemo: ")
    println(parseRequestResult)
  }

  parseResponseStringDemo()
  createResponseStringDemo()
  parseRequestStringDemo()
  parseRequestEndStringDemo()
}

