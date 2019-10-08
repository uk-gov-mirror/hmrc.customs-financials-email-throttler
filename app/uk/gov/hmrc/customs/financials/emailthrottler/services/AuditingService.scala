/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.customs.financials.emailthrottler.services

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.{Configuration, Environment, Logger, LoggerLike}
import play.api.http.HeaderNames
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.AuditModel
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}
import uk.gov.hmrc.play.bootstrap.config.AuditingConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditingService @Inject()(appConfig: AppConfig, auditConnector: FinancialsAuditConnector) {

  val log: LoggerLike = Logger(this.getClass)

  val referrer: HeaderCarrier => String = _.headers.find(_._1 == HeaderNames.REFERER).map(_._2).getOrElse("-")

  def audit(auditModel: AuditModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val dataEvent = ExtendedDataEvent(
      auditSource = appConfig.appName,
      auditType = auditModel.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, referrer(hc)),
      detail = auditModel.detail
    )

    log.debug(s"Splunk Audit Event:\n${dataEvent}\n")
    auditConnector.sendExtendedEvent(dataEvent)
      .map{ auditResult =>
        logAuditResult(auditResult)
        auditResult
      }
  }

  private def logAuditResult(auditResult: AuditResult)(implicit ec: ExecutionContext): Unit = auditResult match {
    case Success =>
      log.debug("Splunk Audit Successful")
    case Failure(err, _) =>
      log.debug(s"Splunk Audit Error, message: $err")
    case Disabled =>
      log.debug(s"Auditing Disabled")
  }
}
import uk.gov.hmrc.play.bootstrap.config.RunMode
class FinancialsAuditConnector @Inject() (configuration: Configuration, environment: Environment) extends AuditConnector {
  override lazy val auditingConfig: AuditingConfig = new AuditingConfigProvider(configuration, new RunMode(configuration, environment.mode), s"auditing").get()
}

