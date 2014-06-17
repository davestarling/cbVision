package me.d10g.vision

import play.api.libs.json.Json

trait JsonUtils {
	implicit val faceFmt  = Json.format[Face]
	implicit val photoFmt = Json.format[Photo]
}