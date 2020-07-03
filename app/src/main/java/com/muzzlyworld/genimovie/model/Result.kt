package com.muzzlyworld.genimovie.model

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

    val succeeded
        get() = this is Result.Success && data != null

    fun onSuccess(block: (R) -> Unit) : Success<R>? =
        (this as? Success)?.apply{ data?.let { block(it) } }
}


