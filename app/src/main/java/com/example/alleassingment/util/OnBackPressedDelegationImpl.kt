package com.example.alleassingment.util

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

class OnBackPressedDelegationImpl : OnBackPressedDelegation, DefaultLifecycleObserver {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed.invoke()
        }
    }

    private var fragmentActivity: FragmentActivity? = null

    private lateinit var onBackPressed: () -> Unit

    override fun registerOnBackPressedDelegation(
        fragmentActivity: FragmentActivity?,
        lifecycle: Lifecycle,
        onBackPressed: () -> Unit
    ) {
        this.fragmentActivity = fragmentActivity
        this.onBackPressed = onBackPressed
        lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        fragmentActivity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        onBackPressedCallback.remove()
    }
}

interface OnBackPressedDelegation {

    fun registerOnBackPressedDelegation(
        fragmentActivity: FragmentActivity?,
        lifecycle: Lifecycle,
        onBackPressed: () -> Unit
    )
}

interface OnBackPressed {
    fun onBackPressed(block: () -> Unit)
}

abstract class ValueFragment<T : ViewBinding>(@LayoutRes fragxml: Int) : Fragment(fragxml),
    OnBackPressedDelegation by OnBackPressedDelegationImpl(), OnBackPressed {

//    protected val binding by viewBinding(::attachBinding)
    //abstract fun attachBinding(view: View): T
    override fun onBackPressed(block: () -> Unit) {
        registerOnBackPressedDelegation(requireActivity(), viewLifecycleOwner.lifecycle) {
            block()
        }
    }
}