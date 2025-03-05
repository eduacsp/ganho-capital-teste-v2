package br.com.codingtest.config

import java.math.BigDecimal
import java.util.Properties

object Configuracao {
    private val properties = Properties()

    init {
        val inputStream = Configuracao::class.java.classLoader.getResourceAsStream("application.properties")
        inputStream?.use { properties.load(it) }
    }

    val TAXA_IMPOSTO: BigDecimal = properties.getProperty("taxa.imposto")?.toBigDecimal() ?: BigDecimal("0.20")
    val VALOR_ISENCAO: BigDecimal = properties.getProperty("valor.isencao")?.toBigDecimal() ?: BigDecimal("20000.00")
    val ESCALA_PADRAO: Int = properties.getProperty("escala.padrao")?.toInt() ?: 2
    val GANHO_ZERADO: Int = properties.getProperty("ganho.zerado")?.toInt() ?: 0
    val RETORNO_ZERO: BigDecimal = properties.getProperty("retorno.zero")?.toBigDecimal() ?: BigDecimal("0.00")

}
