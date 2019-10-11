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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsNull
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.AuditModel
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import scala.concurrent.Future

//noinspection TypeAnnotation
class AuditingServiceSpec extends WordSpec with MockitoSugar with DefaultAwaitTimeout with FutureAwaits with MustMatchers {

  val mockAppConfig = mock[AppConfig]
  val mockAuditConnector = mock[FinancialsAuditConnector]
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "AuditingService" should {
    "audit the events" in {
      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Success))

      val auditingService = new AuditingService(mockAppConfig, mockAuditConnector)
      await(auditingService.audit(AuditModel("transactionName", JsNull, "auditType"))) mustBe AuditResult.Success
    }

    "not throw an exception when failed to audit the events" in {
      val auditResult = AuditResult.Failure("failed to audit", Some(new Exception("error")))
      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(auditResult))

      val auditingService = new AuditingService(mockAppConfig, mockAuditConnector)
      await(auditingService.audit(AuditModel("transactionName", JsNull, "auditType"))) mustBe auditResult
    }

    "not audit if auditing is disabled" in {
      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future.successful(AuditResult.Disabled))

      val auditingService = new AuditingService(mockAppConfig, mockAuditConnector)
      await(auditingService.audit(AuditModel("transactionName", JsNull, "auditType"))) mustBe AuditResult.Disabled
    }
  }

  "AuditConnector" should {
    "load the auditing config" in {
      val env = Environment.simple()
      val configuration = Configuration.load(env)
      val auditConnector = new FinancialsAuditConnector(configuration, env, mockAppConfig)

      when(mockAppConfig.appName).thenReturn("customs-financials-email-throttler")

      auditConnector.auditingConfig.auditSource must be("customs-financials-email-throttler")
      auditConnector.auditingConfig.enabled must be(true)
    }
  }
}
