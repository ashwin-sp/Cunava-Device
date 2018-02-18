package com.hpinc.voter.RequestBodyDataClass

import java.util.*

/**
 * Created by ashwin-4529 on 18/02/18.
 */


data class MainDataClass(private var requests: Array<Requests>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MainDataClass

        if (!Arrays.equals(requests, other.requests)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(requests)
    }
}