package com.hpinc.voter.RequestBodyDataClass

import java.util.*

/**
 * Created by ashwin-4529 on 18/02/18.
 */


data class Requests(var features: Array<Features>, var image: Image) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Requests

        if (!Arrays.equals(features, other.features)) return false
        if (image != other.image) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(features)
        result = 31 * result + image.hashCode()
        return result
    }
}