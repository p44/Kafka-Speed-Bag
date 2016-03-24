
import com.mcw.KafkaStreamConsumer
import play.api.libs.json.Json
import play.api.{Logger, GlobalSettings}
import play.api.mvc.Results._
import play.api.mvc.{Result, RequestHeader}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import models._

/** */
object Global extends GlobalSettings {

  override def onError(rh: RequestHeader, ex: Throwable) = {
    val mm = "ERROR " + ex.getMessage
    Logger.error(mm, ex)
    Future(InternalServerError(Json.prettyPrint(Json.toJson(Msg(mm)))))
  }

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    val errObj = Msg(error)
    Future(BadRequest(Json.prettyPrint(Json.toJson(errObj))))
  }

  override def onHandlerNotFound(rh: RequestHeader) = {
    Future.successful(NotFound(Json.prettyPrint(Json.toJson(Msg("404 Not Found")))))
  }

  override def onStart(app: play.api.Application): Unit = {
  }

  override def onStop(app: play.api.Application) {
  }

}

