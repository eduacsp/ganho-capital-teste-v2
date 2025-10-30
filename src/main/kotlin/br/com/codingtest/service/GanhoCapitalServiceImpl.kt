package br.com.codingtest.service

import br.com.codingtest.config.AppConfig
import br.com.codingtest.context.ImpostoCalculator
import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import br.com.codingtest.handler.CompraHandler
import br.com.codingtest.handler.VendaHandler

class GanhoCapitalServiceImpl(private val config: AppConfig) : GanhoCapitalService {

    override fun calcularGanhoDeCapital(operacoes: List<Operacao>): List<Resultado> {
        val resultados = mutableListOf<Resultado>()
        val contexto = OperacaoContext()

        val impostoCalculator = ImpostoCalculator(config)
        val compraHandler = CompraHandler(config)
        val vendaHandler = VendaHandler(config, impostoCalculator)

        compraHandler.setNext(vendaHandler)

        operacoes.forEach { operacao ->
            resultados.add(compraHandler.handle(operacao, contexto))
        }
        return resultados
    }
}