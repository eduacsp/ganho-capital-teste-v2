package br.com.codingtest.context

import br.com.codingtest.config.AppConfig
import java.math.BigDecimal
import java.math.RoundingMode

class ImpostoCalculator(private val config: AppConfig) {

    fun calcularImposto(ganhoLiquido: BigDecimal, valorOperacao: BigDecimal): BigDecimal {
        return if (ganhoLiquido.signum() > config.ganhoZerado && valorOperacao > config.valorIsencao) {
            ganhoLiquido.multiply(config.taxaImposto).setScale(config.escalaPadrao, RoundingMode.HALF_UP)
        } else {
            config.retornoZero
        }
    }

    fun deduzirPrejuizoAcumulado(
        ganhoBruto: BigDecimal,
        prejuizoAcumulado: BigDecimal
    ): BigDecimal {
        val prejuizoADeduzir = ganhoBruto.min(prejuizoAcumulado)
        return ganhoBruto.subtract(prejuizoADeduzir)
    }
}