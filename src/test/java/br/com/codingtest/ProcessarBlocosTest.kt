package br.com.codingtest

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.Resultado
import br.com.codingtest.util.BigDecimalDeserializer
import br.com.codingtest.util.BigDecimalSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal

class ProcessarBlocosTest {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalDeserializer())
        .registerTypeAdapter(BigDecimal::class.java, BigDecimalSerializer())
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
            processarBlocoDeOperacoes(inputJson)
        }

        val resultadosEsperados = listOf(Resultado(BigDecimal("0.00")), Resultado(BigDecimal("0.00")))
        val jsonEsperado = gson.toJson(resultadosEsperados)

        println("JSON Esperado: $jsonEsperado")
        println("JSON Obtido: $output")

        assertEquals(jsonEsperado.trim(), output.trim())
    }


    @Test
    fun `deve exibir erro ao processar JSON invalido`() {
        val inputJson = """
            [
                {"operation":"buy","unit-cost":10.0,"quantity":100},
                {"operation":"sell","unit-cost":15.0,"quantity":50}
            """.trimIndent()

        val output = captureOutput {
            processarBlocoDeOperacoes(inputJson)
        }

        assertTrue(output.contains("Erro ao processar o bloco: JSON inválido."))
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
            processarBlocoDeOperacoes(inputJson)
        }

        assertTrue(output.contains("Erro ao processar o bloco:"))
    }

    @Test
    fun `deve exibir erro ao processar entrada vazia`() {
        val inputJson = ""
        val output = captureOutput {
            processarBlocoDeOperacoes(inputJson)
        }
        assertTrue(output.contains("Erro ao processar o bloco: gson.fromJson(reader, tipo) must not be null"))
    }

    @Test
    fun `deve processar multiplos blocos de JSON com estado independente`() {
        val input = """
        [
            {"operation":"buy","unit-cost":10.0,"quantity":100},
            {"operation":"sell","unit-cost":15.0,"quantity":50}
        ]
        """.trimIndent()

        val tipo = object : TypeToken<List<Operacao>>() {}.type
        val operacoes: List<Operacao> = gson.fromJson(input, tipo)

        operacoes.forEach { op ->
            println("Operacao: ${op.operation}, unitCost: ${op.unitCost}, Quantity: ${op.quantity}")
        }

        val output = captureOutput {
            processarBlocoDeOperacoes(gson.toJson(operacoes))
        }

        val resultadosEsperados = listOf(Resultado(BigDecimal("0.00")), Resultado(BigDecimal("0.00")))
        val jsonEsperado = gson.toJson(resultadosEsperados)

        assertEquals(jsonEsperado.trim(), output.trim())
    }

    private fun captureOutput(block: () -> Unit): String {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        val originalOut = System.out
        System.setOut(printStream)
        try {
            block.invoke()
        } finally {
            System.setOut(originalOut)
        }
        return outputStream.toString()
    }
}
