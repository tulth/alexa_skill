package alexa_skill

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl


object Routes {
  def routes[F[_]: Sync](
    homeTheaterSkillId: String
    , jarvisSkillId: String
  ): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "ping" =>
        Ok("pong")
      case req @ POST -> Root / "home-theater" =>
        HomeTheaterIntentHandler.handleIntent(homeTheaterSkillId, req)
      case req @ POST -> Root / "jarvis" =>
        JarvisIntentHandler.handleIntent(jarvisSkillId, req)
    }
  }
}
