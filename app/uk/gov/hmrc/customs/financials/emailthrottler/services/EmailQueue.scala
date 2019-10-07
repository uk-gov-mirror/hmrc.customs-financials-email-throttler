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
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.customs.financials.emailthrottler.config.AppConfig
import uk.gov.hmrc.customs.financials.emailthrottler.domain.{EmailRequest, SendEmailJob}
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@Singleton
class EmailQueue @Inject()(mongoComponent: ReactiveMongoComponent, appConfig: AppConfig, dateTimeService: DateTimeService)(implicit ec: ExecutionContext)
  extends ReactiveRepository[SendEmailJob, BSONObjectID](
    collectionName = "emailQueue",
    mongo = mongoComponent.mongoConnector.db,
    domainFormat = SendEmailJob.format,
    idFormat = ReactiveMongoFormats.objectIdFormats) {

  def enqueue(emailRequest: EmailRequest): Unit = {

    val timeStamp = dateTimeService.getTimeStamp
    val result = insert(SendEmailJob(emailRequest, timeStamp, processed = false))

    result.onComplete {
      // audit request and insert result
      case Failure(e) => e.printStackTrace()
      case Success(writeResult) =>
        println(s"successfully inserted document with result: $writeResult")
    }

    ()
  }
}
