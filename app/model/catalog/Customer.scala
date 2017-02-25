package model.catalog

import play.api.libs.json._
import play.api.libs.functional.syntax._


object Customer {
    implicit val JSON_FORMATTER = Json.format[Customer]
    implicit val JSON_LIST_FORMATTER = Format(Reads.seq(JSON_FORMATTER), Writes.seq(JSON_FORMATTER))
}

case class Customer(var _id: Option[String] = None, name: String, taxId: String, address: Option[Address] = None)
