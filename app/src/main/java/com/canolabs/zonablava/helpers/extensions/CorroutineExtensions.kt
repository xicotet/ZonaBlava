package com.canolabs.zonablava.helpers.extensions

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CancellableContinuation


/*
It says that this declaration needs opt-in '@kotlinx.coroutines.ExperimentalCoroutinesApi'
@OptIn(ExperimentalCoroutinesApi::class)
Besides that, LatLng should always be possible to be casted to T
 */
fun <T> CancellableContinuation<T>.resumeWithLatLng(value: LatLng) {
    resume(value as T) { /* Empty callback Since we're not using cancellation in this scenario */ }
}
