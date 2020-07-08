package com.muzzlyworld.genimovie.util.iloader

import android.view.View
import com.muzzlyworld.genimovie.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

internal val View.viewRequestKeeper: TargetViewRequestKeeper
    get() {
        var manager = getTag(R.id.view_request_keeper) as? TargetViewRequestKeeper
        if (manager == null) {
            manager = synchronized(this) {

                (getTag(R.id.view_request_keeper) as? TargetViewRequestKeeper)
                    ?.let { return@synchronized it }

                TargetViewRequestKeeper().apply {
                    addOnAttachStateChangeListener(this)
                    setTag(R.id.view_request_keeper, this)
                }
            }
        }
        return manager
    }

internal class TargetViewRequestKeeper : View.OnAttachStateChangeListener {

    private var currentViewRequest: TargetViewRequestDelegate? = null

    var currentRequestId: UUID? = null
        private set
    var currentRequestJob: Job? = null
        private set

    private var pendingClear: Job? = null

    private var isRestart = false
    private var skipAttach = true

    fun setCurrentRequest(viewRequest: TargetViewRequestDelegate?) {
        // Don't cancel the pending clear if this is a restarted request.
        if (isRestart) {
            isRestart = false
        } else {
            pendingClear?.cancel()
            pendingClear = null
        }

        currentViewRequest?.dispose()
        currentViewRequest = viewRequest
        skipAttach = true
    }

    fun setCurrentRequestJob(job: Job): UUID {
        val requestId = newRequestId()
        currentRequestId = requestId
        currentRequestJob = job
        return requestId
    }

    fun clearCurrentRequest() {
        currentRequestId = null
        currentRequestJob = null

        pendingClear?.cancel()
        pendingClear = GlobalScope.launch(Dispatchers.Main.immediate) { setCurrentRequest(null) }
    }

    override fun onViewAttachedToWindow(v: View) {
        if (skipAttach) {
            skipAttach = false
            return
        }

        currentViewRequest?.let { request ->
            isRestart = true
            request.restart()
        }
    }

    override fun onViewDetachedFromWindow(v: View) {
        skipAttach = false
        currentViewRequest?.dispose()
    }

    private fun newRequestId(): UUID {
        val requestId = currentRequestId
        if (requestId != null && isRestart) {
            return requestId
        }

        return UUID.randomUUID()
    }
}
