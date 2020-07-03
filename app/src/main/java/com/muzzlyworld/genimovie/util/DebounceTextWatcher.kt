package com.muzzlyworld.genimovie.util

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEBOUNCE_DELAY = 500L

class DebounceTextWatcher(
    private val coroutineScope: CoroutineScope,
    private val onDebouncingTextChange: (String) -> Unit): TextWatcher {

    private var searchJob: Job? = null

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.isNullOrEmpty())return

        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            s.toString().let {
                delay(DEBOUNCE_DELAY)
                onDebouncingTextChange(it)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}