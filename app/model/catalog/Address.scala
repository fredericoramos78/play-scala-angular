package model.catalog

import play.api.libs.json._
import play.api.libs.functional.syntax._


object Address {
    implicit val JSON_FORMATTER: Format[Address] = (
            (__ \ "street").format[String] ~
            (__ \ "number").format[String] ~
            (__ \ "complement").format[String] ~
            (__ \ "city").format[String] ~
            (__ \ "state").format[String] ~
            (__ \ "country").format[String] ~
            (__ \ "toString").format[String]
         )(jsonApply _, unlift(jsonUnapply))
    
    def jsonApply(street: String, number: String, complement: String, city: String, state: String, country: String, toString: String): Address = 
        Address(street, number, complement, city, state, country)
        
    def jsonUnapply(x: Address): Option[(String, String, String, String, String, String, String)] =
        Some( (x.street, x.number, x.complement, x.city, x.state, x.country, x.toString()) )
        
        
    implicit val JSON_OPTION_FORMATTER: Format[Option[Address]] = Format.optionWithNull(JSON_FORMATTER)
}

case class Address(street: String, number: String, complement: String, city: String, state: String, country: String) {
    override def toString() = s"${number} ${street} - ${city}, ${country}"
}