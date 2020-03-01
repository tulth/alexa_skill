package alexa_skill

case class AlexaSkillResponse
(version: String
  , sessionAttributes: Map[String, String]
  , response: AlexaSkillResponseResponse
)

case class AlexaSkillResponseResponse
(outputSpeech: AlexaSkillOutputSpeech
  , card: AlexaSkillCard
  , shouldEndSession: Boolean
  , `type`: String = "_DEFAULT_RESPONSE"
  // , directives : ??
)

case class AlexaSkillOutputSpeech
(`type`: String
  , text: String
)

case class AlexaSkillCard
(`type`: String
  , title: String
  , content: String
)

object AlexaSkillResponse {
  def createSimple(speechText: String, card: String): AlexaSkillResponse = {
    AlexaSkillResponse(
      version="1.0",
      sessionAttributes = Map.empty[String, String],
      response=AlexaSkillResponseResponse(
        outputSpeech=AlexaSkillOutputSpeech("PlainText", speechText),
        card=AlexaSkillCard("Simple", card, speechText),
        shouldEndSession=true))
  }
}
