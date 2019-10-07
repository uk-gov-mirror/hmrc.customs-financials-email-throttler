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

import org.mockito.ArgumentMatchers
import org.scalatest.WordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.EmailRequest
import uk.gov.hmrc.mongo.MongoConnector
import org.mockito.Mockito.{spy, verify, when}

//noinspection TypeAnnotation
class EmailQueueSpec extends WordSpec with MockitoSugar {

  import scala.concurrent.ExecutionContext.Implicits.global

  val mockAppconfig = mock[AppConfig]
  val mockDateTimeService = mock[DateTimeService]
  when(mockDateTimeService.getTimeStamp).thenCallRealMethod()

  val reactiveMongoComponent = new ReactiveMongoComponent {
    val mongoUri = "mongodb://127.0.0.1:27017/test-customs-email-throttler"

    override def mongoConnector: MongoConnector = MongoConnector(mongoUri)
  }

  val emailQueue = new EmailQueue(reactiveMongoComponent, mockAppconfig, mockDateTimeService)
  val spyEmailQueue = spy(emailQueue)

  "EmailQueue" should {

    "enqueue email request" should {

      "insert email job into collection" in {
        val emailRequest = EmailRequest(List.empty, "", Map.empty, force = false, None, None)

        spyEmailQueue.enqueue(emailRequest)

        verify(spyEmailQueue).insert(ArgumentMatchers.any())(ArgumentMatchers.any())
      }

      "audit requests and insert result" in {
        pending
      }

      "audit queue length" in {
        pending
      }

    }

  }

}
