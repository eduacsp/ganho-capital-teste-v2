package br.com.codingtest.util

import br.com.codingtest.config.AppConfig
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalSerializer(private val config: AppConfig) : JsonSerializer<BigDecimal> {
    override fun serialize(
        src: BigDecimal,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement =
        JsonPrimitive(src.setScale(config.escalaPadrao, RoundingMode.HALF_UP).toPlainString())
}