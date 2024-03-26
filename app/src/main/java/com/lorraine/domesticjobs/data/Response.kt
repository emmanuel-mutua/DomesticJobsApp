package com.lorraine.domesticjobs.data

sealed class Response<out T> {
    data object Idle : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Failure(val message: String) : Response<Nothing>()
}
