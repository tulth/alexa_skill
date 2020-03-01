package alexa_skill
import cats.syntax.functor._
import io.circe.{ Decoder, Encoder }, io.circe.generic.auto._
import io.circe.syntax._

case class AlexaSkillRequest
(version: String
  , session: AlexaSkillSession
  , context: AlexaSkillContext
  , request: AlexaSkillRequestRequest
)

object AlexaSkillRequest {

}

case class AlexaSkillSession
(`new`: Boolean
  , sessionId: String
  , attributes: Map[String, String] = Map.empty[String, String]
  , application: AlexaSkillApplication
  , user: AlexaSkillUser
  , 
)

case class AlexaSkillContext
(System: AlexaSkillSystem
  // , Alexa.Presentation.APL: ??
  // , AudioPlayer: ??
  // , Viewport: AlexaSkillViewPort
  // , viewports: ??
)

case class AlexaSkillSystem
(apiAccessToken: String
  , apiEndpoint: String
  , application: AlexaSkillApplication
  , device: AlexaSkillDevice
  , person: AlexaSkillPerson = AlexaSkillPerson("", "")
  , user: AlexaSkillUser
)

sealed trait AlexaSkillRequestRequest

case class AlexaSkillIntentRequest
(`type`: String
  , requestId: String
  , timestamp: String
//  , dialogState: String = "COMPLETED"
  , intent: AlexaSkillIntent
  , locale: String = ""
) extends AlexaSkillRequestRequest

case class AlexaSkillSessionEndedRequest
(`type`: String
  , requestId: String
  , timestamp: String
//  , dialogState: String = "COMPLETED"
  , reason: String
  , locale: String
) extends AlexaSkillRequestRequest

object AlexaSkillRequestRequest {

  // implicit val encodeAlexaSkillRequestRequest: Encoder[AlexaSkillRequestRequest] = Encoder.instance {
  //   case alexaSkillIntentRequest @ AlexaSkillIntentRequest(_) => alexaSkillIntentRequest.asJson
  //   case alexaSkillSessionEndedRequest @ AlexaSkillSessionEndedRequest(_) => alexaSkillSessionEndedRequest.asJson
  // }

  implicit val decodeAlexaSkillRequestRequest: Decoder[AlexaSkillRequestRequest] =
    List[Decoder[AlexaSkillRequestRequest]](
      Decoder[AlexaSkillIntentRequest].widen
        , Decoder[AlexaSkillSessionEndedRequest].widen
    ).reduceLeft(_ or _)
}

// object AlexaSkillRequestRequest {
//   implicit val encodeAlexaSkillRequestRequest: Encoder[AlexaSkillRequestRequest] =
//     deriveEncoder[AlexaSkillRequestRequest]
//   implicit val decodeAlexaSkillRequestRequest: Decoder[AlexaSkillRequestRequest] =
//     deriveDecoder[AlexaSkillRequestRequest]
// }

case class AlexaSkillApplication(applicationId: String)

case class AlexaSkillUser
(userId: String
  , accessToken: String = ""
)

case class AlexaSkillDevice
(deviceId: String
//  , supportedInterfaces: ??
)

case class AlexaSkillPerson
(personId: String
  , accessToken: String = ""
)

case class AlexaSkillIntent
(name: String
  , confirmationStatus: String
  , slots: Map[String, AlexaSkillSlot]
)

case class AlexaSkillSlot
(name: String
  , value: String
  , confirmationStatus: String
  , resolutions: AlexaSkillResolution
)

case class AlexaSkillResolution(resolutionsPerAuthority: List[AlexaSkillResolutionPerAuthority])

sealed trait AlexaSkillResolutionPerAuthority
case class AlexaSkillResolutionPerAuthorityNoValues
(authority: String
  , status: AlexaSkillResolutionStatus
) extends AlexaSkillResolutionPerAuthority

case class AlexaSkillResolutionPerAuthorityHasValues
(authority: String
  , status: AlexaSkillResolutionStatus
  , values: List[AlexaSkillResolutionValue]
) extends AlexaSkillResolutionPerAuthority

object AlexaSkillResolutionPerAuthority {
    implicit val decodeAlexaSkillResolutionPerAuthority: Decoder[AlexaSkillResolutionPerAuthority] =
    List[Decoder[AlexaSkillResolutionPerAuthority]](
      Decoder[AlexaSkillResolutionPerAuthorityHasValues].widen
        , Decoder[AlexaSkillResolutionPerAuthorityNoValues].widen
    ).reduceLeft(_ or _)

}

case class AlexaSkillResolutionStatus(code: String)

case class AlexaSkillResolutionValue(value: AlexaSkillResolutionValueValue)

case class AlexaSkillResolutionValueValue(name: String, id: String)
