package br.com.codingtest.handler

import br.com.codingtest.config.AppConfig
import br.com.codingtest.context.ImpostoCalculator
import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import java.math.BigDecimal

class VendaHandler(
    private val config: AppConfig,
    private val impostoCalculator: ImpostoCalculator
) : OperacaoHandler() {

    override fun handle(operacao: Operacao, contexto: OperacaoContext): Resultado {
        if (operacao.operation != br.com.codingtest.enums.TipoOperacao.SELL) {
            return nextHandler?.handle(operacao, contexto)
                ?: throw UnsupportedOperationException("Operação não suportada: ${operacao.operation}")
        }

        if (operacao.quantity > contexto.quantidadeDeAcoesAtual) {
            throw IllegalArgumentException("Tentativa de venda de mais ações do que disponível.")
        }

        contexto.quantidadeDeAcoesAtual -= operacao.quantity

        val ganhoDaOperacao = operacao.unitCost.subtract(contexto.mediaPonderada)
            .multiply(BigDecimal.valueOf(operacao.quantity.toLong()))

        val valorOperacao = operacao.unitCost.multiply(BigDecimal.valueOf(operacao.quantity.toLong()))

        return if (ganhoDaOperacao.signum() < config.ganhoZerado) {
            // Prejuízo
            contexto.prejuizoAcumulado = contexto.prejuizoAcumulado.add(ganhoDaOperacao.negate())
            Resultado(config.retornoZero)
        } else {
            // Lucro
            val ganhoLiquido = impostoCalculator.deduzirPrejuizoAcumulado(
                ganhoDaOperacao,
                contexto.prejuizoAcumulado
            )
            contexto.prejuizoAcumulado = contexto.prejuizoAcumulado.subtract(
                ganhoDaOperacao.subtract(ganhoLiquido)
            )
            val imposto = impostoCalculator.calcularImposto(ganhoLiquido, valorOperacao)
            Resultado(imposto)
        }
    }
}