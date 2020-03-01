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

abstract class HomeTheaterActionResponse[F[_]: Sync](actionId: String) extends
    ActionResponse(actionId=actionId, intentId="Activity")  {
  def shellCmd: String = s"bash -c /home/alexa/activity/activity_${this._action}.sh"
  def action: F[_] = Sync[F].delay({
    import scala.sys.process._
    this.shellCmd run
  })
}

class NetflixHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("netflix")
class KodiHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("kodi")
class YoutubeHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("youtube")
class TwitchHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("twitch")
class PlexHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("plex")
class ShutdownHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("shutdown")
class SteamHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("steam")
class PrimeHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("prime")
class DisneyHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("disney")
class SamsungDexHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("dex")
class BedTimeHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("bedtime") {
  val sleepSeconds = 5 * 60
  override def shellCmd: String = s"bash -c /home/alexa/activity/activity_${this._action}.sh ${this.sleepSeconds}"
}
class TestHomeTheaterActionResponse[F[_]: Sync] extends HomeTheaterActionResponse("test") {
  override def shellCmd = "ls"
}
class UnhandledHomeTheaterActionResponse[F[_]: Sync](actionId: String) extends HomeTheaterActionResponse(actionId) {
  override def speechText: String = s"I did not understand activity ${this._action}"
  override def action: F[_] = Sync[F].delay({()})
}


abstract class VolumeActionResponse[F[_]: Sync](actionId: String) extends
    ActionResponse(actionId=actionId, intentId="Volume")  {
  def shellCmd: String = s"bash -c /home/alexa/activity/volume_${this._action}.sh"
  def action: F[_] = Sync[F].delay({
    import scala.sys.process._
    this.shellCmd run
  })
}

class upVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("up")
class downVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("down")
class highVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("high")
class mediumVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("medium")
class lowVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("low")
class muteVolumeActionResponse[F[_]: Sync] extends VolumeActionResponse("mute")
class UnhandledVolumeActionResponse[F[_]: Sync](actionId: String) extends VolumeActionResponse(actionId) {
  override def speechText: String = s"I did not understand volume ${this._action}"
  override def action: F[_] = Sync[F].delay({()})
}

// case class HomeTheaterIntentHandler(expectAppId: String) extends IntentHandler {
object HomeTheaterIntentHandler extends IntentHandler {

  def activityDecode[F[_]: Sync](slotVal: String): HomeTheaterActionResponse[F] = {
    slotVal.toLowerCase() match {
      case "flex" | "netflix"                        => new NetflixHomeTheaterActionResponse()
      case "kodi"                                    => new KodiHomeTheaterActionResponse()
      case "youtube" | "you tube"                    => new YoutubeHomeTheaterActionResponse()
      case "twitch"                                  => new TwitchHomeTheaterActionResponse()
      case "my movies"                               => new PlexHomeTheaterActionResponse()
      case "off" | "shutdown"| "shut down"           => new ShutdownHomeTheaterActionResponse()
      case "game" | "steam" | "gaming" | "steen"     => new SteamHomeTheaterActionResponse()
      case  "seen" | "gaining" | "steam link"        => new SteamHomeTheaterActionResponse()
      case "amazon prime" | "prime"                  => new PrimeHomeTheaterActionResponse()
      case "disney" | "disney plus"                  => new DisneyHomeTheaterActionResponse()
      case "dex" | "samsung dex"                     => new SamsungDexHomeTheaterActionResponse()
      case "bed time" | "bedtime" | "timer" | "time" => new BedTimeHomeTheaterActionResponse()
      case "test"                                    => new TestHomeTheaterActionResponse()
      case unhandled                                 => new UnhandledHomeTheaterActionResponse(unhandled)
    }
  }

  def volumeDecode[F[_]: Sync](slotVal: String): VolumeActionResponse[F] = {
    slotVal.toLowerCase() match {
      case "up"                         => new upVolumeActionResponse()
      case "down"                       => new downVolumeActionResponse()
      case "hi" | "high" | "five" | "5" => new highVolumeActionResponse()
      case "med" | "medium" | "tpm"     => new mediumVolumeActionResponse()
      case "lo" | "low" | "love"        => new lowVolumeActionResponse()
      case "mute" | "muted" | "off"     => new muteVolumeActionResponse()
      case unhandled                    => new UnhandledVolumeActionResponse(unhandled)
    }
  }

  def getActionResponse[F[_]: Sync](intent: AlexaSkillIntent): F[Response[F]] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    val slotKeyDecoderE: Either[String, (String, String=>ActionResponse[F])] = (
      intent.name.toLowerCase() match {
        case "activityintent" => Right(("activitySlot", activityDecode[F]))
        case "volumeintent" => Right(("volumeSlot", volumeDecode[F]))
        case unhandledIntent => Left(s"unhandled Intent $unhandledIntent")
      })
    val activityResponseE : Either[String, ActionResponse[F]] = for {
      (slotKey, decoder) <- slotKeyDecoderE
      activitySlot <- (intent.slots get slotKey).toRight("activitySlot missing")
      slotVal = activitySlot.value
      activityDecoded = decoder(slotVal)
    } yield activityDecoded
    activityResponseE match {
      case Right(activityResponse) =>
        activityResponse.action *>
        Ok(activityResponse.response.asJson)
      case Left(message) =>
        Sync[F].delay(println(s"ERROR! $message")) *>
        BadRequest()
    }
  }
}
