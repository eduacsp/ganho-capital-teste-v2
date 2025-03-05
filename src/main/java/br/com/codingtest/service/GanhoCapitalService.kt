package br.com.codingtest.service

import br.com.codingtest.domain.Operacao
import br.com.codingtest.domain.Resultado

interface GanhoCapitalService {
    fun calcularGanhoDeCapital(operacoes: List<Operacao>): List<Resultado>
}