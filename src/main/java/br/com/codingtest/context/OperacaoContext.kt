package br.com.codingtest.context

import br.com.codingtest.config.Configuracao
import br.com.codingtest.config.Configuracao.GANHO_ZERADO
import br.com.codingtest.config.Configuracao.RETORNO_ZERO
import br.com.codingtest.config.Configuracao.TAXA_IMPOSTO
import br.com.codingtest.config.Configuracao.VALOR_ISENCAO
import java.math.BigDecimal
import java.math.RoundingMode

object ImpostoCalculator {

    fun calcularImposto(ganhoLiquido: BigDecimal, valorOperacao: BigDecimal): BigDecimal {
        return if (ganhoLiquido.signum() > GANHO_ZERADO && valorOperacao > VALOR_ISENCAO) {
            ganhoLiquido.multiply(TAXA_IMPOSTO).setScale(Configuracao.ESCALA_PADRAO, RoundingMode.HALF_UP)
        } else {
            RETORNO_ZERO
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
