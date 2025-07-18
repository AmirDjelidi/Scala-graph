package efrei.app

import zio._
import efrei.app.service.GraphService

object Main extends ZIOAppDefault {
  override def run: ZIO[Any, Throwable, Unit] =
    ConsoleApp.appLogic
      .provide(
        GraphService.live
      )
}