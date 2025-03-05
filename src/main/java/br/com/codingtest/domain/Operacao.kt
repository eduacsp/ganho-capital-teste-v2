package br.com.codingtest.domain

import br.com.codingtest.enums.TipoOperacao
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Operacao(
        val operation: TipoOperacao,
        @SerializedName("unit-cost")
        val unitCost: BigDecimal,
        val quantity: Int
    )