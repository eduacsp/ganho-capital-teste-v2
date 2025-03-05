package br.com.codingtest.handler

import br.com.codingtest.config.Configuracao.GANHO_ZERADO
import br.com.codingtest.config.Configuracao.RETORNO_ZERO
import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import br.com.codingtest.context.ImpostoCalculator
import java.math.BigDecimal

class VendaHandler : OperacaoHandler() {

    override fun handle(operacao: Operacao, contexto: OperacaoContext): Resultado {
        if (operacao.operation == br.com.codingtest.enums.TipoOperacao.SELL) {
            if (operacao.quantity > contexto.quantidadeDeAcoesAtual) {
                throw IllegalArgumentException("Tentativa de venda de mais ações do que disponível.")
            }

            contexto.quantidadeDeAcoesAtual -= operacao.quantity

            val ganhoDaOperacao = operacao.unitCost.subtract(contexto.mediaPonderada)
                .multiply(BigDecimal(operacao.quantity))

            val valorOperacao = operacao.unitCost.multiply(BigDecimal(operacao.quantity))

            val resultado: Resultado

            if (ganhoDaOperacao.signum() < GANHO_ZERADO) {
                // Prejuízo
                contexto.prejuizoAcumulado = contexto.prejuizoAcumulado.add(ganhoDaOperacao.negate())
                resultado = Resultado(RETORNO_ZERO)
            } else {
                // Lucro
                val ganhoLiquido = ImpostoCalculator.deduzirPrejuizoAcumulado(
                    ganhoDaOperacao,
                    contexto.prejuizoAcumulado
                )
                contexto.prejuizoAcumulado = contexto.prejuizoAcumulado.subtract(
                    ganhoDaOperacao.subtract(ganhoLiquido)
                )

                val imposto = ImpostoCalculator.calcularImposto(ganhoLiquido, valorOperacao)
                resultado = Resultado(imposto)
            }

            return resultado
        } else {
            return nextHandler?.handle(operacao, contexto)
                ?: throw UnsupportedOperationException("Operação não suportada: ${operacao.operation}")
        }
    }
}
