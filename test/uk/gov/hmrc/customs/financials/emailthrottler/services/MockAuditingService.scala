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

package uk.gov.hmrc.customs.financials.emailthrottler.services

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, verify}
import org.scalatest.{BeforeAndAfterEach, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.customs.financials.emailthrottler.domain.AuditModel
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait MockAuditingService extends WordSpec with MockitoSugar with BeforeAndAfterEach {

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditingService)
  }

  lazy val mockAuditingService: AuditingService = mock[AuditingService]

  def verifyAudit(model: AuditModel, path: Option[String] = None): Unit = {
    verify(mockAuditingService).audit(
      ArgumentMatchers.eq(model)
    )(
      ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )
  }
}