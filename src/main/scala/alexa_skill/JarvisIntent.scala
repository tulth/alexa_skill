package alexa_skill

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import fs2.{text}

import io.circe.Json
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration

import org.http4s.Response

abstract class JarvisActionResponse[F[_]: Sync](actionId: String, state: Boolean) extends
    ActionResponse(actionId=actionId, intentId="SetSwitch")  {
  override def speechText: String = s"setting ${this.actionId} to ${boolToOnOff(this.state)}"
  def boolToOnOff(b: Boolean): String = if (b) "ON" else "OFF"
  def action: F[_]
}

class DenLampJarvisActionResponse[F[_]: Sync](state: Boolean) extends
    JarvisActionResponse("den lamp", state) {
  def action: F[_] = Sync[F].delay({
    import scala.sys.process._
    val state01 = if (state) "1" else "0"
    val shellCmd: String = s"mosquitto_pub -t home/downstairs/corner_lamp/powerstate/set -m $state01"
    println(s"Executing $shellCmd")
    shellCmd run
  })
}

class UpstairsOccupiedJarvisActionResponse[F[_]: Sync](state: Boolean) extends
    JarvisActionResponse("upstairs occupied", state) {
  def action: F[_] = Sync[F].delay({
    import scala.sys.process._
    val cmdVal = boolToOnOff(state)
    val shellCmd: String = s"""curl http://burpelson:8080/rest/items/upstairs_occupied --header "Content-Type: text/plain" --request POST --data $cmdVal"""
    println(s"Executing $shellCmd")
    shellCmd run
  })
}

class UnhandledJarvisActionResponse[F[_]: Sync](actionId: String) extends
    JarvisActionResponse(actionId, state=false) {
  override def speechText: String = s"I did not understand ${this._action}"
  def action: F[_] = Sync[F].delay({()})
}

object JarvisIntentHandler extends IntentHandler {

  def getActionResponse[F[_]: Sync](intent: AlexaSkillIntent): F[Response[F]] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    val slotsE = intent.name.toLowerCase() match {
      case "setswitchintent" => Right(intent.slots)
      case unhandledIntent => Left(s"unhandled Intent $unhandledIntent")
    } 
    val nameStateE : Either[String, (String, Either[String, Boolean])] = for {
      slots <- slotsE
      switchNameSlot <- (intent.slots get "switchNameSlot").toRight("switchNameSlot missing")
      slotName = switchNameSlot.value
      switchStateSlot <- (intent.slots get "switchStateSlot").toRight("switchStateSlot missing")
      switchStateStr = switchStateSlot.value
      switchStateE = (interpretSwitchState(switchStateStr)
        .toRight(s"I did not understand switch state $switchStateStr"))
    } yield (slotName, switchStateE)

    nameStateE match {
      case Right((switchName, switchStateE)) =>
        val actionResponse = actionDecode(switchName, switchStateE)
        actionResponse.action *>
        Ok(actionResponse.response.asJson)
      case Left(message) =>
        Sync[F].delay(println(s"ERROR! $message")) *>
        BadRequest()
    }
  }

  def interpretSwitchState(requestedState: String): Option[Boolean] = {
    requestedState.toLowerCase() match {
      case "on" | "active" | "activate" | "occupied" => Some(true)
      case "off" | "inactive" | "deactivate" | "away" | "unoccupied" => Some(false)
      case _ => None
    }
  }

  def actionDecode[F[_]: Sync](name: String, state: Either[String, Boolean]): JarvisActionResponse[F] = {
    state match {
      case Left(message) => new UnhandledJarvisActionResponse(message)
      case Right(state) =>
        name.toLowerCase() match {
          case "corner lamp" | "den lamp" | "den"                 => new DenLampJarvisActionResponse(state)
          case "upstairs" | "upstairs occupied" | "computer room" => new UpstairsOccupiedJarvisActionResponse(state)
          case unhandled                                          =>
            new UnhandledJarvisActionResponse(s"I did not understand switch $unhandled")
        }
    }
  }
}
