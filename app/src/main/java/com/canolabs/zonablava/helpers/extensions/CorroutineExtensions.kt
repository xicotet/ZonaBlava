package com.canolabs.zonablava.helpers.extensions

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi


/*
Be aware that this declaration is opt-in '@kotlinx.coroutines.ExperimentalCoroutinesApi'
Besides that, LatLng should always be possible to be casted to T
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CancellableContinuation<T>.resumeWithLatLng(value: LatLng) {
    resume(value as T) { /* Empty callback Since we're not using cancellation in this scenario */ }
}
