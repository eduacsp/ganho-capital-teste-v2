package br.com.codingtest.util

import br.com.codingtest.config.AppConfig
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalDeserializer(private val config: AppConfig) : JsonDeserializer<BigDecimal> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BigDecimal = try {
        when {
            json.isJsonNull -> config.retornoZero
            json.isJsonPrimitive -> {
                val p = json.asJsonPrimitive
                when {
                    p.isNumber -> BigDecimal(p.asNumber.toString())
                        .setScale(config.escalaPadrao, RoundingMode.HALF_UP)
                    p.isString -> BigDecimal(p.asString)
                        .setScale(config.escalaPadrao, RoundingMode.HALF_UP)
                    else -> throw IllegalArgumentException("Formato inválido: $json")
                }
            }
            else -> throw IllegalArgumentException("Formato inválido: $json")
        }
    } catch (e: Exception) {
        throw IllegalArgumentException("Erro ao desserializar BigDecimal: ${json}", e)
    }
}