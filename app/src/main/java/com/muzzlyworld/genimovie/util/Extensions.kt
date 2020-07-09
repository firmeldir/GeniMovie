package com.muzzlyworld.genimovie.util

import com.muzzlyworld.genimovie.util.model.Result
import retrofit2.Response

internal inline fun <T, R> saveApiCall(
    call: () -> Response<T>,
    transform: (T) -> R
): Result<R> = try {
    val response = call()
    if (response.isSuccessful) {
        if (response.body() == null) Result.Error(IllegalArgumentException())
        else Result.Success(transform(response.body()!!))
    } else Result.Error(IllegalArgumentException())
} catch (exception: Exception) {
    Result.Error(exception)
}