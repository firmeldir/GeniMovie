package com.muzzlyworld.genimovie.util

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

//Todo : Rework fun

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