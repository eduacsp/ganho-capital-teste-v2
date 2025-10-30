package br.com.codingtest.handler

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.OperacaoContext
import br.com.codingtest.domain.Resultado

abstract class OperacaoHandler {
    protected var nextHandler: OperacaoHandler? = null

    fun setNext(handler: OperacaoHandler): OperacaoHandler {
        this.nextHandler = handler
        return handler
    }

    abstract fun handle(operacao: Operacao, contexto: OperacaoContext): Resultado
}
