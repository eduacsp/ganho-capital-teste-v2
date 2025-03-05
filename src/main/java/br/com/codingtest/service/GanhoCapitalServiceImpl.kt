package br.com.codingtest.service

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado
import br.com.codingtest.handler.CompraHandler
import br.com.codingtest.handler.VendaHandler

class GanhoCapitalServiceImpl : GanhoCapitalService {

    override fun calcularGanhoDeCapital(operacoes: List<Operacao>): List<Resultado> {
        val resultados = mutableListOf<Resultado>()
        val contexto = OperacaoContext()

        val compraHandler = CompraHandler()
        val vendaHandler = VendaHandler()
        compraHandler.setNext(vendaHandler)

        operacoes.forEach { operacao ->
            val resultado = compraHandler.handle(operacao, contexto)
            resultados.add(resultado)
        }
        return resultados
    }
}
