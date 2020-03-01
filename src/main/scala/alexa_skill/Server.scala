package alexa_skill

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global

object Server {

  def mainStream[F[_]: ConcurrentEffect](
    port: Int
      , addLogger: Boolean
      , homeTheaterSkillId: String
      , jarvisSkillId: String
  )
    (implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream

      httpApp = (Routes.routes[F](homeTheaterSkillId, jarvisSkillId)).orNotFound

      // With Middlewares in place
      finalHttpApp = (
        if (addLogger) Logger.httpApp(true, true)(httpApp)
        else httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
