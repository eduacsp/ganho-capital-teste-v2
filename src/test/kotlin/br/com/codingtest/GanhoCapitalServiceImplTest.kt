package br.com.codingtest

import br.com.codingtest.config.AppConfig
import br.com.codingtest.domain.Operacao
import br.com.codingtest.enums.TipoOperacao
import br.com.codingtest.service.GanhoCapitalService
import br.com.codingtest.service.GanhoCapitalServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GanhoCapitalServiceImplTest {

    private val config = AppConfig(
        taxaImposto = BigDecimal("0.20"),
        valorIsencao = BigDecimal("20000.00"),
        escalaPadrao = 2,
        ganhoZerado = 0,
        retornoZero = BigDecimal("0.00")
    )

    private val service: GanhoCapitalService = GanhoCapitalServiceImpl(config)

    @Test
    fun `deve calcular imposto zero para operacoes de compra`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("10.00"), quantity = 100)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(1)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `deve calcular imposto para venda com lucro sem prejuizo acumulado`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("10.00"), quantity = 10000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 2000)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(2)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("2000.00"))
    }

    @Test
    fun `deve acumular prejuizo para venda com prejuizo`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("20.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 100)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(2)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `deve deduzir prejuizo acumulado do lucro`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("20.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 100),
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("10.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 100)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(4)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[2].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[3].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `nao deve calcular imposto se valor da operacao for menor ou igual a 20000`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("100.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("150.00"), quantity = 100)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(2)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `deve calcular imposto se valor da operacao for maior que 20000 mesmo com lucro apos deducao de prejuizo`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("100.00"), quantity = 200),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("50.00"), quantity = 200),
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("40.00"), quantity = 500),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("60.00"), quantity = 500)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(4)
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[3].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `deve calcular imposto corretamente em operacoes complexas`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("10.00"), quantity = 10000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("2.00"), quantity = 5000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("20.00"), quantity = 2000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("20.00"), quantity = 2000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("25.00"), quantity = 1000),
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("20.00"), quantity = 10000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 5000),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("30.00"), quantity = 4350),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("30.00"), quantity = 650)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(9)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[2].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[3].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[4].tax).isEqualTo(BigDecimal("3000.00"))
        assertThat(resultados[5].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[6].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[7].tax).isEqualTo(BigDecimal("3700.00"))
        assertThat(resultados[8].tax).isEqualTo(BigDecimal("0.00"))
    }

    @Test
    fun `deve lancar excecao ao tentar vender mais acoes do que possui`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("10.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("15.00"), quantity = 150)
        )

        assertThrows(IllegalArgumentException::class.java) {
            service.calcularGanhoDeCapital(operacoes)
        }
    }

    @Test
    fun `deve calcular corretamente varias operacoes de compra e venda`() {
        val operacoes = listOf(
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("20.00"), quantity = 100),
            Operacao(TipoOperacao.BUY, unitCost = BigDecimal("30.00"), quantity = 100),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("25.00"), quantity = 150),
            Operacao(TipoOperacao.SELL, unitCost = BigDecimal("35.00"), quantity = 50)
        )

        val resultados = service.calcularGanhoDeCapital(operacoes)

        assertThat(resultados).hasSize(4)
        assertThat(resultados[0].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[1].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[2].tax).isEqualTo(BigDecimal("0.00"))
        assertThat(resultados[3].tax).isEqualTo(BigDecimal("0.00"))
    }

}
