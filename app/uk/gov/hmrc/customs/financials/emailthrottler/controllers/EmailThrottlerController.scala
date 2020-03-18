/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customs.financials.emailthrottler.controllers

import javax.inject.{Inject, Singleton}
import play.api.{Logger, LoggerLike}
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.EmailRequest
import uk.gov.hmrc.customs.financials.emailthrottler.services.EmailQueue
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.Future

@Singleton()
class EmailThrottlerController @Inject()(emailQueue: EmailQueue, appConfig: AppConfig, cc: ControllerComponents)
    extends BackendController(cc) {

  val log: LoggerLike = Logger(this.getClass)

  log.info("Service started")

  def enqueueEmail(): Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[EmailRequest].fold(
      errors => {
        log.error(s"enqueueEmail: Bad Request, error: $errors")
        Future.successful(BadRequest(Json.obj("Status" -> "Bad Request", "message" -> JsError.toJson(errors))))
      },
      emailRequest => {
        log.info(s"enqueueEmail: send email request enqueued")
        emailQueue.enqueueJob(emailRequest)
        Future.successful(Accepted(Json.obj("Status" -> "Ok", "message" -> "Email successfully queued")))
      }
    )

  }
}
