package br.com.codingtest.domain

import java.math.BigDecimal

data class OperacaoContext(
    var quantidadeDeAcoesAtual: Int = 0,
    var mediaPonderada: BigDecimal = BigDecimal.ZERO,
    var prejuizoAcumulado: BigDecimal = BigDecimal.ZERO
)