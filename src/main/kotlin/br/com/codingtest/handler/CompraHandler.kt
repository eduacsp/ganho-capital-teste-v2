package br.com.codingtest.handler

import br.com.codingtest.config.AppConfig
import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import java.math.BigDecimal
import java.math.RoundingMode

class CompraHandler(private val config: AppConfig) : OperacaoHandler() {

    override fun handle(operacao: Operacao, contexto: OperacaoContext): Resultado {
        if (operacao.operation == br.com.codingtest.enums.TipoOperacao.BUY) {
            val quantidadeAtual = contexto.quantidadeDeAcoesAtual
            val mediaAtual = contexto.mediaPonderada

            val totalQuantidade = quantidadeAtual + operacao.quantity
            if (totalQuantidade == 0) {
                throw IllegalArgumentException("Quantidade total não pode ser zero.")
            }
            val totalValor = mediaAtual.multiply(BigDecimal(quantidadeAtual))
                .add(operacao.unitCost.multiply(BigDecimal(operacao.quantity)))

            contexto.mediaPonderada = totalValor.divide(
                BigDecimal.valueOf(totalQuantidade.toLong()),
                config.escalaPadrao,
                RoundingMode.HALF_UP
            )
            contexto.quantidadeDeAcoesAtual = totalQuantidade

            return Resultado(config.retornoZero)
        } else {
            return nextHandler?.handle(operacao, contexto)
                ?: throw UnsupportedOperationException("Operação não suportada: ${operacao.operation}")
        }
    }
}