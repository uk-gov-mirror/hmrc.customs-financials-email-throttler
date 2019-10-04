package uk.gov.hmrc.customsfinancialsemailthrottler.domain

import play.api.libs.json.Json

case class EmailRequest(to: List[EmailAddress],
                        templateId: String,
                        parameters: Map[String, String],
                        force: Boolean,
                        eventUrl: Option[String],
                        onSendUrl: Option[String])

object EmailRequest {
  implicit val format = Json.format[EmailRequest]
}
