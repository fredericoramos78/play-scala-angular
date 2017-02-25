package controllers.rest.utils


import play.api.libs.json._

case class SearchFilter(value: Option[String] = None)
case class DatatablesPost(offset: Int = 0, length: Int = 25, order: Option[Seq[Sorting]] = None, search: Option[SearchFilter] = None)

object DatatablesPost {
    implicit val SEARCH_READER = Json.reads[SearchFilter]
    implicit val DATATABLES_READER = Json.reads[DatatablesPost]
    
    
    def prepareResponse(total: Long, selected: Long, data: JsValue): JsValue =
        Json.obj("recordsTotal" -> total,
                 "recordsFiltered" -> selected,
                 "data" -> data)
}
