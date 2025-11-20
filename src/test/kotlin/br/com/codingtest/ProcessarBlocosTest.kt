package br.com.codingtest

import br.com.codingtest.config.AppConfig
import br.com.codingtest.service.GanhoCapitalService
import br.com.codingtest.service.GanhoCapitalServiceImpl
import br.com.codingtest.util.BigDecimalDeserializer
import br.com.codingtest.util.BigDecimalSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal


class ProcessarBlocosTest {

    private val config = AppConfig()
    private val service: GanhoCapitalService = GanhoCapitalServiceImpl(config)

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalDeserializer(config))
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer(config))
        .create()

    @Test
    fun `deve processar JSON valido com operacoes corretas`() {
        val inputJson = """
        [
            {"operation":"buy","unit-cost":10.0,"quantity":100},
            {"operation":"sell","unit-cost":15.0,"quantity":50}
        ]
    """.trimIndent()

        val output = captureOutput {
            processarBlocoDeOperacoes(inputJson, service, gson)
        }

        val jsonSaida = output.trim()

        // Parseia a saída em uma lista de mapas: [{tax=0.00}, {tax=0.00}]
        val listType = object : com.google.gson.reflect.TypeToken<List<Map<String, BigDecimal>>>() {}.type
        val resultados: List<Map<String, BigDecimal>> = gson.fromJson(jsonSaida, listType)

        assertThat(resultados).hasSize(2)
        assertThat(resultados[0]["tax"]).isEqualByComparingTo(BigDecimal("0.00"))
        assertThat(resultados[1]["tax"]).isEqualByComparingTo(BigDecimal("0.00"))
    }


    @Test
    fun `deve processar multiplos blocos de JSON com estado independente`() {
        val input = """
        [
            {"operation":"buy","unit-cost":10.0,"quantity":100},
            {"operation":"sell","unit-cost":15.0,"quantity":50}
        ]
    """.trimIndent()
        val output = captureOutput {
            processarBlocoDeOperacoes(input, service, gson)
        }
        val jsonSaida = output.trim()
        val listType = object : com.google.gson.reflect.TypeToken<List<Map<String, BigDecimal>>>() {}.type
        val resultados: List<Map<String, BigDecimal>> = gson.fromJson(jsonSaida, listType)

        assertThat(resultados).hasSize(2)
        assertThat(resultados[0]["tax"]).isEqualByComparingTo(BigDecimal("0.00"))
        assertThat(resultados[1]["tax"]).isEqualByComparingTo(BigDecimal("0.00"))
    }


    private fun captureOutput(block: () -> Unit): String {
        val baos = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(baos))
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return baos.toString().trim()
    }
}