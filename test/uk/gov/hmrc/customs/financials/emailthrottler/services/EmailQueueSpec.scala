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

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{spy, verify, when}
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.{EmailRequest, SendEmailJob}
import uk.gov.hmrc.mongo.MongoConnector

//noinspection TypeAnnotation
class EmailQueueSpec extends WordSpec with MockitoSugar with FutureAwaits with DefaultAwaitTimeout with BeforeAndAfterEach with MustMatchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  val mockAppConfig = mock[AppConfig]
  val mockDateTimeService = mock[DateTimeService]
  when(mockDateTimeService.getTimeStamp).thenCallRealMethod()

  val reactiveMongoComponent = new ReactiveMongoComponent {
    val mongoUri = "mongodb://127.0.0.1:27017/test-customs-email-throttler"

    override def mongoConnector: MongoConnector = MongoConnector(mongoUri)
  }

  val emailQueue = new EmailQueue(reactiveMongoComponent, mockAppConfig, mockDateTimeService)

  override def beforeEach: Unit = {
    await(emailQueue.removeAll())
  }

  "EmailQueue" should {

    "enqueue email request" should {

      "insert email job into collection" in {
        val emailRequest = EmailRequest(List.empty, "", Map.empty, force = false, None, None)
        val spyEmailQueue = spy(emailQueue)

        spyEmailQueue.enqueue(emailRequest)

        verify(spyEmailQueue).insert(ArgumentMatchers.any())(ArgumentMatchers.any())
      }

      "audit requests and insert result" in {
        pending
      }

      "audit queue length" in {
        pending
      }

      "get oldest, not processed, send email job" in {
        when(mockDateTimeService.getTimeStamp)
          .thenReturn(OffsetDateTime.of(2019,10,8,15,1,0,0,ZoneOffset.UTC))
          .thenReturn(OffsetDateTime.of(2019,10,8,15,2,0,0,ZoneOffset.UTC))
          .thenReturn(OffsetDateTime.of(2019,10,8,15,3,0,0,ZoneOffset.UTC))

        val emailRequests = Seq(
          EmailRequest(List.empty, "id_1", Map.empty, force = false, None, None),
          EmailRequest(List.empty, "id_2", Map.empty, force = false, None, None),
          EmailRequest(List.empty, "id_3", Map.empty, force = false, None, None)
        )
        emailRequests.foreach(emailQueue.enqueue)

        val expectedEmailJob = SendEmailJob(
          EmailRequest(List.empty, "id_1", Map.empty, force = false, None, None),
          OffsetDateTime.of(2019,10,8,15,1,0,0,ZoneOffset.UTC),
          processed = true
        )

        val job = await(emailQueue.getNextEmailJob())
        job mustBe Some(expectedEmailJob)

        val expectedEmailJob2 = SendEmailJob(
          EmailRequest(List.empty, "id_2", Map.empty, force = false, None, None),
          OffsetDateTime.of(2019,10,8,15,2,0,0,ZoneOffset.UTC),
          processed = true
        )

        val job2 = await(emailQueue.getNextEmailJob())
        job2 mustBe Some(expectedEmailJob2)
      }

    }

  }

}
