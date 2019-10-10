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

import java.time.{OffsetDateTime, ZoneOffset}

import org.mockito.Mockito.{verify, when}
import org.scalatest.WordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.customs.financials.emailthrottler.domain.{EmailRequest, SendEmailJob}

import scala.concurrent.{ExecutionContext, Future}

class EmailJobHandlerSpec extends WordSpec with MockitoSugar with FutureAwaits with DefaultAwaitTimeout {

  import scala.concurrent.ExecutionContext.Implicits.global

  class MockedEmailJobHandlerScenario()(implicit ec: ExecutionContext) {

    val sendEmailJob = SendEmailJob(
      EmailRequest(List.empty, "id_1", Map.empty, force = false, None, None),
      OffsetDateTime.of(2019,10,8,15,1,0,0,ZoneOffset.UTC),
      processed = true
    )

    val mockEmailQueue = mock[EmailQueue]
    when(mockEmailQueue.getNextEmailJob()).thenReturn(Future.successful(Some(sendEmailJob)))

    val mockEmailNotificationService = mock[EmailNotificationService]

    val service = new EmailJobHandler(mockEmailQueue, mockEmailNotificationService)

  }

  "EmailJobHandlerSpec" should {

    "sendEmail" should {

      "fetch job from email queue" in new MockedEmailJobHandlerScenario {

        await(service.processEmailJob())

        verify(mockEmailQueue).getNextEmailJob()
      }

    }

    "numberOfEmailsPerSecond" in {
      pending
    }

    "source" in {
      pending
    }

  }
}
