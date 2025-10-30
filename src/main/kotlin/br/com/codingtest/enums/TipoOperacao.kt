package br.com.codingtest.enums

import com.google.gson.annotations.SerializedName

enum class TipoOperacao {
    @SerializedName("buy") BUY,
    @SerializedName("sell") SELL
}