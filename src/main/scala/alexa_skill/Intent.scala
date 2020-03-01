package alexa_skill

import cats.effect.Sync
import cats.implicits._

import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import org.http4s.{Uri, Response, Request}
import org.http4s.Uri.Scheme

import io.circe.Json
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration

import fs2.{text}

abstract class ActionResponse[F[_]: Sync] (actionId: String, intentId: String) {
  var _action:String = actionId
  var _intent:String = intentId
  def speechText: String = s"doing ${this.intentId} ${this.actionId}"
  def response: AlexaSkillResponse =
    AlexaSkillResponse.createSimple(speechText = this.speechText, card = this.intentId)
  def action: F[_]
}

trait IntentHandler {
  implicit val customConfig: Configuration = Configuration.default.withDefaults

  def getActionResponse[F[_]: Sync](intent: AlexaSkillIntent): F[Response[F]]

  def handleIntent[F[_]: Sync](skillId:String, req: Request[F]): F[Response[F]] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    val signatureCertUriE: Either[String, (String, Uri)] = for {
      signature <- (req.headers.get(`Signature`)
        .map(_.value).toRight("Missing Signature Header"))
      certUri <- (req.headers.get(`SignatureCertChainUrl`)
        .map(_.value).toRight("Missing SignatureCertChainUrl Header"))
      uri <- Uri.fromString(certUri).left.map(_.toString)
      checkedUri <- checkSignatureUri(uri)
    } yield (signature, checkedUri)
    signatureCertUriE match {
      case Left(message) =>
        Sync[F].delay(println(message)) *>
        // BadRequest(message)
        BadRequest()
      case Right((signature, certUri)) => {
        val reqBodyStrF: F[String]= req.body.through(text.utf8Decode).compile.string
        reqBodyStrF.flatMap(s => {
          val intentE = decodeIntent(skillId, s)
          intentE match {
            case Left(message) =>
              Sync[F].delay(println(s"BadRequest because: $message")) *>
              // BadRequest(message)
              BadRequest()
            case Right(intent) =>
              this.getActionResponse(intent)
          }
        })
      }
    }
  }

  def decodeIntent(skillId: String, reqBody: String): Either[String, AlexaSkillIntent] = {
    val parsedRequestE: Either[io.circe.Error, AlexaSkillRequest] = decode[AlexaSkillRequest](reqBody)
    for {
      parsedRequest <- parsedRequestE.left.map(_.toString)
      rxSkillId = parsedRequest.context.System.application.applicationId
      applicationIdGood <- (
        if (rxSkillId == skillId) Right(true)
        else Left("Mismatch on applicationId"))
      intent <- parsedRequest.request match  {
        case AlexaSkillIntentRequest(_,requestId,timeStamp,intent,locale) =>
          Right(intent)
        case _ => Left(s"Not AlexaSkillIntentRequest, parsedRequest=$parsedRequest")
      }
    } yield intent
  }

  def checkSignatureUri(uri: Uri): Either[String, Uri] = {
    for {
      scheme <- uri.scheme.toRight("invalid protocol")
      schemeIsHttps <- Option.when(scheme == Scheme.https)(true).toRight("invalid protocol")
      host <- uri.host.toRight("invalid hostname").map(s => (s.toString.toLowerCase))
      hostIsAmazon <- Option.when(host == "s3.amazonaws.com")(true).toRight("invalid hostname")
      uriPathGood <- Option.when(uri.path.startsWith("/echo.api/"))(true).toRight("invalid path")
      port = uri.port.getOrElse(443)
      portGood <- Option.when(port==443)(true).toRight("invalid port")
    } yield uri
  }  
}

object IntentHandlerEx extends IntentHandler {
  val expectAppId = ""
  def getActionResponse[F[_]: Sync](intent: AlexaSkillIntent): F[Response[F]] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    Ok()
  }
}
