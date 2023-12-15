package com.util

import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectItems(
    owner: LifecycleOwner,
    emitData : ((T) -> Unit)?=null
) = owner.lifecycleScope.launch {
    flowWithLifecycle(owner.lifecycle,Lifecycle.State.STARTED).collect { emitData?.invoke(it) }
}

//    owner.lifecycleScope.launch {
//    owner.repeatOnLifecycle(lifecycleState){collect{ emitData?.invoke(it) } }
//}