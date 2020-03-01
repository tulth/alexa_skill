import org.scalatest._

import org.http4s.Uri

import cats.syntax.all._
import cats.effect.{ExitCode, IO}

import alexa_skill._

class MainSpec extends FlatSpec with Matchers {
  "main" should "fail unless both --homeTheaterSkillId=<id> and --jarvisSkillId=<id> command line arguments are provided" in {
    AlexaSkillMain.run(List()).unsafeRunSync() should be (ExitCode(1))
    AlexaSkillMain.run(List("--homeTheaterSkillId=htsid")).unsafeRunSync() should be (ExitCode(1))
    AlexaSkillMain.run(List("--jarvisSkillId=jsid")).unsafeRunSync() should be (ExitCode(1))
    // this will run forever
    // AlexaSkillMain.run(List("--jarvisSkillId=jsid", "--homeTheaterSkillId=htsid")).unsafeRunSync() should be (ExitCode(1))
  }
}
