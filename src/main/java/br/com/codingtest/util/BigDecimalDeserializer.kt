package br.com.codingtest.util

import br.com.codingtest.config.Configuracao.ESCALA_PADRAO
import br.com.codingtest.config.Configuracao.RETORNO_ZERO
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalDeserializer : JsonDeserializer<BigDecimal> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BigDecimal {
        return try {
            val valor = when {
                json.isJsonNull -> RETORNO_ZERO
                json.isJsonPrimitive -> {
                    val primitivo = json.asJsonPrimitive
                    when {
                        primitivo.isNumber -> BigDecimal(primitivo.asNumber.toString()).setScale(ESCALA_PADRAO, RoundingMode.HALF_UP)
                        primitivo.isString -> BigDecimal(primitivo.asString).setScale(ESCALA_PADRAO, RoundingMode.HALF_UP)
                        else -> throw IllegalArgumentException("Formato inválido para BigDecimal: $json")
                    }
                }
                else -> throw IllegalArgumentException("Formato inválido para BigDecimal: $json")
            }
            valor
        } catch (e: Exception) {
            throw IllegalArgumentException("Erro ao desserializar BigDecimal: ${json.toString()}", e)
        }
    }
}