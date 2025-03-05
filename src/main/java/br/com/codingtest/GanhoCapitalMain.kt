package br.com.codingtest

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.Resultado
import br.com.codingtest.service.GanhoCapitalService
import br.com.codingtest.service.GanhoCapitalServiceImpl
import br.com.codingtest.util.BigDecimalDeserializer
import br.com.codingtest.util.BigDecimalSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.StringReader
import java.math.BigDecimal
import java.util.*

val service: GanhoCapitalService = GanhoCapitalServiceImpl()
val gson: Gson = GsonBuilder()
    .registerTypeAdapter(BigDecimal::class.java, BigDecimalDeserializer())
    .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer())
    .create()

fun main() {
    val scanner = Scanner(System.`in`)
    val inputBuffer = StringBuilder()

    while (scanner.hasNextLine()) {
        val input = scanner.nextLine()
        if (input.trim().isEmpty()) {
            break
        } else {
            inputBuffer.append(input).append('\n')
        }
    }

    if (inputBuffer.isNotEmpty()) {
        processarBlocos(inputBuffer.toString())
    }
}

fun processarBlocos(input: String) {
    val pattern = Regex("\\[.*?\\]", RegexOption.DOT_MATCHES_ALL)
    val matches = pattern.findAll(input)

    for (match in matches) {
        val bloco = match.value
        processarBlocoDeOperacoes(bloco)
    }
}

fun processarBlocoDeOperacoes(input: String) {
    try {
        val reader = JsonReader(StringReader(input.trim()))
        reader.isLenient = true
        val tipo = object : TypeToken<List<Operacao>>() {}.type
        val operacoes: List<Operacao> = gson.fromJson(reader, tipo)
        val resultado = service.calcularGanhoDeCapital(operacoes)
        println(toJson(resultado))
    } catch (e: JsonSyntaxException) {
        println("Erro ao processar o bloco: JSON inválido. ${e.message}")
    } catch (e: Exception) {
        println("Erro ao processar o bloco: ${e.message}")
    }
}

fun toJson(resultados: List<Resultado>): String {
    return gson.toJson(resultados)
}
