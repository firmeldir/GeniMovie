package com.muzzlyworld.genimovie.util.iloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt

private const val BITMAP_SCALE = 0.4f
private const val BLUR_RADIUS = 7.5f

private val TAG = ImageLoader::class.java.name

class ImageLoader private constructor(private val context: Context){

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate +
            CoroutineExceptionHandler { _ , throwable -> Log.e(TAG, throwable.message.toString()) })

    fun process(imageRequest: ImageRequest){
        imageRequest.target.view.viewRequestKeeper.setCurrentRequestJob( scope.launch {
            attachRequestToLifecycle(imageRequest)

            try {

                imageRequest.target.onStart(
                    imageRequest.placeholder?.let { context.resources.getDrawable(imageRequest.placeholder, context.theme) })

                var bitmap = withContext(Dispatchers.IO){
                    BitmapFactory.decodeStream(java.net.URL(imageRequest.imageUri.toString()).openStream())
                }

                bitmap = if (imageRequest.extraOptions.contains(ExtraLoaderOptions.Blur)) bitmap.blur() else bitmap

                imageRequest.target.onSuccess(
                    BitmapDrawable(context.resources, bitmap)
                )

            }catch (e: Exception){
                Log.e(TAG, e.message.toString())
                imageRequest.target.onError(
                    imageRequest.error?.let { context.resources.getDrawable(imageRequest.error, context.theme) }
                )
            }
        })
    }

    private suspend fun attachRequestToLifecycle(imageRequest: ImageRequest) = with(imageRequest.requestLifecycle) {
        TargetViewRequestDelegate(this@ImageLoader, imageRequest, this, coroutineContext[Job]!!).also {
            this.addObserver(it)
            imageRequest.target.view.viewRequestKeeper.setCurrentRequest(it)
        }

        (imageRequest.target as? LifecycleObserver)?.let(this::addObserver)
    }

    private fun Bitmap.blur(): Bitmap {
        val width = (width * BITMAP_SCALE).roundToInt()
        val height = (height * BITMAP_SCALE).roundToInt()
        val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }

    companion object{
        private var instance: ImageLoader? = null

        fun get(context: Context): ImageLoader = instance ?: newImageLoader(context.applicationContext)

        private fun newImageLoader(context: Context): ImageLoader =
            ImageLoader(context).apply {
                instance = this
            }
    }
}