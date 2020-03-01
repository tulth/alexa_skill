package alexa_skill

import scala.io.Source

import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration

import cats.syntax.all._
import cats.effect.{ExitCode, IO, IOApp}

import java.io.File

case class AppOptions(
  port: Int
    , addLogger: Boolean
    , homeTheaterSkillId: Option[String]
    , jarvisSkillId: Option[String]
)

object AlexaSkillMain extends IOApp {

  def parseArgs(args: List[String]): AppOptions = {
    def optionMapping(opt: String):(String, String) = opt match {
      case s"--port=$optVal" => ("port", optVal)
      case s"--log=$logVal" => ("addLogger", logVal)
      case s"--homeTheaterSkillId=$homeTheaterSkillIdVal" => ("homeTheaterSkillId", homeTheaterSkillIdVal)
      case s"--jarvisSkillId=$jarvisSkillIdVal" => ("jarvisSkillId", jarvisSkillIdVal)
      case "-l" => ("addLogger", "true")
      case _ => ("error", opt)
    }
    val options = args.map(optionMapping).toMap
    ////
    val port = (for {
      portStr <- options get "port"
      port <- portStr.toIntOption
    } yield port).getOrElse(8080)
    ////
    val addLogger = (for {
      addLoggerStr <- options get "addLogger"
      addLogger <- addLoggerStr.toBooleanOption
    } yield addLogger).getOrElse(false)
    ////
    val homeTheaterSkillId = (options get "homeTheaterSkillId")
    val jarvisSkillId = (options get "jarvisSkillId")
    ////
    new AppOptions(port, addLogger, homeTheaterSkillId, jarvisSkillId)
  }

  def run(args: List[String]): IO[ExitCode] = {
    val options = parseArgs(args)
    val port = options.port
    val addLogger = options.addLogger
    val skillIds = (options.homeTheaterSkillId, options.jarvisSkillId)
    skillIds match {
      case (Some(homeTheaterSkillId), Some(jarvisSkillId)) =>
        Server.mainStream[IO](port, addLogger, homeTheaterSkillId, jarvisSkillId).compile.drain.as(ExitCode.Success)
      case _ =>
        IO(System.err.println("Usage: both --homeTheaterSkillId=<id> and --jarvisSkillId=<id> command line arguments must be provided")).as(ExitCode(1))
    }
  }

}
