import org.scalatest._

import org.http4s.Uri

import alexa_skill._

class ParseArgsSpec extends FlatSpec with Matchers {
  "parseArgs" should "default port to 8080" in {
    AlexaSkillMain.parseArgs(List()).port should be (8080)
  }

  "parseArgs" should " support --port=<portnum> " in {
    AlexaSkillMain.parseArgs(List("--port=1337")).port should be (1337)
  }

  "parseArgs" should " use the last entry of --port=<portnum> " in {
    AlexaSkillMain.parseArgs(List("--port=7777", "--port=1337")).port should be (1337)
  }

  "parseArgs" should "default addLogger to false" in {
    AlexaSkillMain.parseArgs(List()).addLogger should be (false)
  }

  "parseArgs" should "set addLogger true with a -l argument" in {
    AlexaSkillMain.parseArgs(List("-l")).addLogger should be (true)
  }

  "parseArgs" should "set addLogger using --log=<value> using true/false notation" in {
    AlexaSkillMain.parseArgs(List("--log=True")).addLogger should be (true)
    AlexaSkillMain.parseArgs(List("--log=true")).addLogger should be (true)
    AlexaSkillMain.parseArgs(List("--log=False")).addLogger should be (false)
    AlexaSkillMain.parseArgs(List("--log=false")).addLogger should be (false)
  }

  "parseArgs" should " use the last entry for addLogger" in {
    AlexaSkillMain.parseArgs(List("-l", "--log=false")).addLogger should be (false)
  }

  "parseArgs" should " support mixing arguments together" in {
    val testArg = List("--log=True", "--port=1212")
    AlexaSkillMain.parseArgs(testArg).addLogger should be (true)
    AlexaSkillMain.parseArgs(testArg).port should be (1212)
    AlexaSkillMain.parseArgs(testArg.reverse).addLogger should be (true)
    AlexaSkillMain.parseArgs(testArg.reverse).port should be (1212)
  }

  "parseArgs" should " get a Some(homeTheaterSkillId) if present or None" in {
    val testVal = "testVal"
    val testArg = List(s"--homeTheaterSkillId=$testVal")
    AlexaSkillMain.parseArgs(testArg).homeTheaterSkillId should be (Some(testVal))
    AlexaSkillMain.parseArgs(List("--log=True", "--port=1212")).homeTheaterSkillId should be (None)
  }

  "parseArgs" should " get a Some(jarvisSkillId) if present or None" in {
    val testVal = "testVal"
    val testArg = List(s"--jarvisSkillId=$testVal")
    AlexaSkillMain.parseArgs(testArg).jarvisSkillId should be (Some(testVal))
    AlexaSkillMain.parseArgs(List("--log=True", "--port=1212")).jarvisSkillId should be (None)
  }

  // "parseArgs" should "set addLogger using --log=<value> using 0/1 notation" in {
  //   AlexaSkillMain.parseArgs(List("--log=1")).addLogger should be (true)
  //   AlexaSkillMain.parseArgs(List("--log=0")).addLogger should be (false)
  // }
}
