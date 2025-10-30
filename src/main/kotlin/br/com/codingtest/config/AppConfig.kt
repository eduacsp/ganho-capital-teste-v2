package br.com.codingtest.config

import java.math.BigDecimal
import java.util.Properties

data class AppConfig(
    val taxaImposto: BigDecimal = BigDecimal("0.20"),
    val valorIsencao: BigDecimal = BigDecimal("20000.00"),
    val escalaPadrao: Int = 2,
    val ganhoZerado: Int = 0,
    val retornoZero: BigDecimal = BigDecimal("0.00")
)

fun loadConfig(): AppConfig {
    val properties = Properties()
    val inputStream = AppConfig::class.java.classLoader.getResourceAsStream("application.properties")
    inputStream?.use { properties.load(it) }

    val config = AppConfig(
        taxaImposto = properties.getProperty("taxa.imposto")?.toBigDecimal() ?: BigDecimal("0.20"),
        valorIsencao = properties.getProperty("valor.isencao")?.toBigDecimal() ?: BigDecimal("20000.00"),
        escalaPadrao = properties.getProperty("escala.padrao")?.toInt() ?: 2,
        ganhoZerado = properties.getProperty("ganho.zerado")?.toInt() ?: 0,
        retornoZero = properties.getProperty("retorno.zero")?.toBigDecimal() ?: BigDecimal("0.00")
    )

    require(config.taxaImposto >= BigDecimal.ZERO) { "Taxa de imposto não pode ser negativa" }
    require(config.valorIsencao >= BigDecimal.ZERO) { "Valor de isenção não pode ser negativo" }
    require(config.escalaPadrao >= 0) { "Escala padrão não pode ser negativa" }
    return config
}