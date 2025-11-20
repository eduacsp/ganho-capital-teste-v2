package br.com.codingtest

import br.com.codingtest.config.loadConfig
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

fun main() {
    val config = try {
        loadConfig()
    } catch (e: Exception) {
        System.err.println("Erro ao carregar application.properties: ${e.message}")
        return
    }
    val service: GanhoCapitalService = GanhoCapitalServiceImpl(config)

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalDeserializer(config))
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer(config))
        .create()

    val inputBuffer = StringBuilder()
    Scanner(System.`in`).use { scanner ->
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.trim().isEmpty()) break
            inputBuffer.append(line).append('\n')
        }
    }

    if (inputBuffer.isNotEmpty()) {
        processarBlocos(inputBuffer.toString(), service, gson)
    }
}

private fun processarBlocos(input: String, service: GanhoCapitalService, gson: Gson) {
    val pattern = Regex("\\[.*?\\]", RegexOption.DOT_MATCHES_ALL)
    pattern.findAll(input).forEach { match ->
        processarBlocoDeOperacoes(match.value, service, gson)
    }
}

internal fun processarBlocoDeOperacoes(
    blocoJson: String,
    service: GanhoCapitalService,
    gson: Gson
) {
    val reader = JsonReader(StringReader(blocoJson.trim())).apply { isLenient = true }
    val tipo = object : TypeToken<List<Operacao>>() {}.type
    val operacoes: List<Operacao> = gson.fromJson(reader, tipo)
    val resultado = service.calcularGanhoDeCapital(operacoes)
    println(toJson(resultado, gson))
}

private fun toJson(resultados: List<Resultado>, gson: Gson): String {
    return gson.toJson(resultados)
}