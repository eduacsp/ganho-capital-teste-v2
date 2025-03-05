package br.com.codingtest.handler

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import br.com.codingtest.config.Configuracao.ESCALA_PADRAO
import br.com.codingtest.config.Configuracao.RETORNO_ZERO
import java.math.BigDecimal
import java.math.RoundingMode

class CompraHandler : OperacaoHandler() {

    override fun handle(operacao: Operacao, contexto: OperacaoContext): Resultado {
        if (operacao.operation == br.com.codingtest.enums.TipoOperacao.BUY) {
            val quantidadeAtual = contexto.quantidadeDeAcoesAtual
            val mediaAtual = contexto.mediaPonderada

            val totalQuantidade = quantidadeAtual + operacao.quantity
            val totalValor = mediaAtual.multiply(BigDecimal(quantidadeAtual))
                .add(operacao.unitCost.multiply(BigDecimal(operacao.quantity)))

            contexto.mediaPonderada = totalValor.divide(
                BigDecimal(totalQuantidade),
                ESCALA_PADRAO,
                RoundingMode.HALF_UP
            )
            contexto.quantidadeDeAcoesAtual = totalQuantidade

            return Resultado(RETORNO_ZERO)
        } else {
            return nextHandler?.handle(operacao, contexto)
                ?: throw UnsupportedOperationException("Operação não suportada: ${operacao.operation}")
        }
    }
}
