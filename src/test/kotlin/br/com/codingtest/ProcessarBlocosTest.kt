package br.com.codingtest

import br.com.codingtest.config.AppConfig
import br.com.codingtest.service.GanhoCapitalService
import br.com.codingtest.service.GanhoCapitalServiceImpl
import br.com.codingtest.util.BigDecimalDeserializer
import br.com.codingtest.util.BigDecimalSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
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

        val expected = """[{"tax":"0.00"},{"tax":"0.00"}]"""
        assertEquals(expected, output.trim())
    }

    @Test
    fun `deve exibir erro ao processar JSON invalido`() {
        val inputJson = """
            [
                {"operation":"buy","unit-cost":10.0,"quantity":100},
                {"operation":"sell","unit-cost":15.0,"quantity":50}
        """.trimIndent()

        val output = captureOutput {
            processarBlocoDeOperacoes(inputJson, service, gson)
        }

        assertThat(output).contains("Erro ao processar o bloco: JSON inválido")
    }

    @Test
    fun `deve exibir erro ao processar operacoes invalidas`() {
        val inputJson = """
            [
                {"operation":"buy","unit-cost":10.0,"quantity":100},
                {"operation":"sell","unit-cost":"invalid","quantity":50}
            ]
        """.trimIndent()

        val output = captureOutput {
            processarBlocoDeOperacoes(inputJson, service, gson)
        }

        assertThat(output).contains("Erro ao processar o bloco")
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

        val expected = """[{"tax":"0.00"},{"tax":"0.00"}]"""
        assertEquals(expected, output.trim())
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