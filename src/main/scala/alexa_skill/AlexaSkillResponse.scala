package alexa_skill_json

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
