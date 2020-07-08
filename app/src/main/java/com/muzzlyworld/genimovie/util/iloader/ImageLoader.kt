package com.muzzlyworld.genimovie.util.iloader

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.muzzlyworld.genimovie.util.iloader.target.ViewTarget
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

private val TAG = ImageLoader::class.java.name

class ImageLoader private constructor(private val context: Context){

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate +
            CoroutineExceptionHandler { _ , throwable -> Log.e(TAG, throwable.message.toString()) })

    fun process(request: Request){
        request.target.view.viewRequestKeeper.setCurrentRequestJob( scope.launch {
            attachRequestToLifecycle(request)

            try {

                request.target.onStart(
                    request.placeholder?.let { context.resources.getDrawable(request.placeholder, context.theme) })

                val bitmap = withContext(Dispatchers.IO){
                    BitmapFactory.decodeStream(java.net.URL(request.imageUri.toString()).openStream())
                }

                request.target.onSuccess(
                    BitmapDrawable(context.resources, bitmap))

            }catch (e: Exception){
                Log.e(TAG, e.message.toString())
                request.target.onError(
                    request.error?.let { context.resources.getDrawable(request.error, context.theme) }
                )
            }
        })
    }

    private suspend fun attachRequestToLifecycle(request: Request) = with(request.requestLifecycle) {
        TargetViewRequestDelegate(this@ImageLoader, request, this, coroutineContext[Job]!!).also {
            this.addObserver(it)
            request.target.view.viewRequestKeeper.setCurrentRequest(it)
        }

        (request.target as? LifecycleObserver)?.let(this::addObserver)
    }


    companion object{
        private var instance: ImageLoader? = null

        fun get(context: Context): ImageLoader = instance ?: newImageLoader(context.applicationContext)

        private fun newImageLoader(context: Context): ImageLoader =
            ImageLoader(context).apply {
                instance = this
            }
    }

    data class Request(
        val imageUri: Uri,
        val target: ViewTarget<View>,

        @DrawableRes val placeholder: Int? = null,
        @DrawableRes val error: Int? = null
    ){

        val requestLifecycle = target.view.findViewTreeLifecycleOwner()?.lifecycle ?: findLifecycle() ?: GlobalLifecycle

        private fun findLifecycle(): Lifecycle? {
            var context: Context? = target.view.context
            while (true) {
                when (context) {
                    is LifecycleOwner -> return context.lifecycle
                    !is ContextWrapper -> return null
                    else -> context = context.baseContext
                }
            }
        }
    }
}