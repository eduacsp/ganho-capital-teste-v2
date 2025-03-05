package br.com.codingtest.util

import br.com.codingtest.config.Configuracao.ESCALA_PADRAO
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalSerializer : JsonSerializer<BigDecimal> {
    override fun serialize(
        src: BigDecimal,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.setScale(ESCALA_PADRAO, RoundingMode.HALF_UP).toPlainString())
    }
}
