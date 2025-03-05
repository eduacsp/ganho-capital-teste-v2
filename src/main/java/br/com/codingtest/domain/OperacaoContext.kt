package br.com.codingtest.domain

import br.com.codingtest.config.Configuracao.RETORNO_ZERO
import java.math.BigDecimal

data class OperacaoContext(
    var quantidadeDeAcoesAtual: Int = 0,
    var mediaPonderada: BigDecimal = RETORNO_ZERO,
    var prejuizoAcumulado: BigDecimal = RETORNO_ZERO
)
