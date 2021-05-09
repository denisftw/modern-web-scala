package filters

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}
import services.StatsService

import scala.concurrent.Future

class StatsFilter(statsService: StatsService, implicit val mat: Materializer)
    extends Filter {
  private val log = Logger(this.getClass)

  override def apply(nextFilter: RequestHeader => Future[Result])(
      header: RequestHeader): Future[Result] = {
    log.info(s"Serving another request: ${header.path}")
    statsService.recordRequest()
    nextFilter(header)
  }
}
