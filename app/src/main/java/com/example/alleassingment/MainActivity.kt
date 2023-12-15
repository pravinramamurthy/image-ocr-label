package com.example.alleassingment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.alleassingment.databinding.ActivityMainBinding
import com.example.alleassingment.ui.ImageDetailsViewModel
import com.example.alleassingment.ui.fragments.ImageDetailsFrag
import com.example.alleassingment.ui.fragments.ImageSelectionScreen

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentFrag: ActivityMainBinding
    private val viewModel: ImageDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
      //  backPressed()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.share -> {
                    loadFragment(ImageSelectionScreen())
                    viewModel.screenData = null
                    return@setOnItemSelectedListener true

                }

                R.id.info -> {
                    loadFragment(ImageDetailsFrag())
                    return@setOnItemSelectedListener true

                }

                else -> {
                    return@setOnItemSelectedListener false

                }
            }
            true
        }
        if (savedInstanceState == null) {
            loadFragment(ImageSelectionScreen())
        }
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    viewModel.screenData = null
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
                /*if (binding.bottomNavigation.selectedItemId == R.id.info) {
                    binding.bottomNavigation.selectedItemId = R.id.share
                } else {

                }*/

            }
        })
    }

}