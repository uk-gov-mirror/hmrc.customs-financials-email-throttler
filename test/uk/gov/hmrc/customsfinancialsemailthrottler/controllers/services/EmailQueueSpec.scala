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

package uk.gov.hmrc.customsfinancialsemailthrottler.controllers.services

import org.scalatest.{Assertion, BeforeAndAfterEach, MustMatchers, WordSpec}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Configuration, Environment}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.customsfinancialsemailthrottler.config.AppConfig
import uk.gov.hmrc.customsfinancialsemailthrottler.services.EmailQueue
import uk.gov.hmrc.mongo.MongoConnector
import uk.gov.hmrc.play.config.ServicesConfig


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmailQueueSpec extends WordSpec with MustMatchers with MongoSpecSupport with DefaultAwaitTimeout with FutureAwaits with BeforeAndAfterEach {
// class MandateRepositorySpec extends PlaySpec with MongoSpecSupport with GuiceOneServerPerSuite with BeforeAndAfterEach with MockitoSugar {

  override def beforeEach: Unit = {
    await(emailQueue.removeAll())
  }

  val reactiveMongo = new ReactiveMongoComponent {
    override def mongoConnector: MongoConnector = mongoConnectorForTest
  }
  val env = Environment.simple()
  val configuration: Configuration = Configuration.load(env)
  val servicesConfiguration = ServicesConfig()
  val appConfig = new AppConfig(configuration, env)
  val emailQueue = new EmailQueue(reactiveMongo,appConfig)

  def toFuture(condition: Assertion) = Future.successful(condition)

  "EmailQueue" should {

    "do stuff" in {
    }

  }

}
