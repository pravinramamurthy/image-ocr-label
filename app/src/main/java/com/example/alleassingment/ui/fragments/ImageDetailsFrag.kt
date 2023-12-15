package com.example.alleassingment.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.alleassingment.R
import com.example.alleassingment.databinding.FragmentImageDetailsBinding
import com.example.alleassingment.ui.ImageDetails
import com.example.alleassingment.ui.ImageDetailsViewModel
import com.example.alleassingment.ui.MainActivity
import com.example.alleassingment.util.ImageLabelingHelper
import com.example.alleassingment.util.RecognizeText
import com.example.alleassingment.util.TextRecognitionListener
import com.example.alleassingment.util.ValueFragment
import com.example.alleassingment.util.collectItems
import com.google.android.material.chip.Chip


class ImageDetailsFrag :
    ValueFragment<FragmentImageDetailsBinding>(R.layout.fragment_image_details),
    TextRecognitionListener {
    private lateinit var binding: FragmentImageDetailsBinding
    private lateinit var bitmap: Bitmap
    private val imageLabelingHelper = ImageLabelingHelper()
    private lateinit var labelList: List<String>
    private val model: ImageDetailsViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageDetailsBinding.inflate(inflater, container, false)
        bitmap = model.selectedBitmap ?: getDefaultBitmap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setOnClick()

        if (model.screenData == null) {
            fetchText()
        } else {
            setUpObserver()

        }

        onBackPressed {
            model.screenData = null
            requireActivity().finish()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNavigation()

    }

    fun setUpObserver() {
        model.screenDataState.collectItems(viewLifecycleOwner) {
            Log.d(TAG, "setUpObserver: $it")
            it?.let {
                labelList = it.labels
                binding.descriptionText.text = it.description
                binding.notes.setText(it.note ?: "")
                printTags(it.labels)
            }
        }
    }

    fun setOnClick() {
        binding.edit.setOnClickListener {
            model.updateScreenData(
                ImageDetails(
                    note = binding.notes.text.toString(),
                    labels = labelList,
                    description = binding.descriptionText.text.toString()
                )
            )
            navigateToEditFrag()
        }
    }

    fun navigateToEditFrag() {
        val nextFragment = EditDetailsFrag()
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, nextFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun setUi() {
        Glide.with(requireContext())
            .load(bitmap)
            .into(binding.imageView)

        binding.descriptionText.post {
            if (binding.descriptionText.lineHeight > 2) {
                binding.readMore.visibility = View.VISIBLE
            }
        }
        binding.readMore.setOnClickListener {
            if (binding.descriptionText.maxLines == Integer.MAX_VALUE) {
                binding.descriptionText.maxLines = 2
                binding.readMore.text = "Read More"
            } else {
                binding.descriptionText.maxLines = Integer.MAX_VALUE
                binding.readMore.text = "Read Less"
            }
        }
    }

    fun printTags(labels: List<String>) {
        Log.d(TAG, "printTags: $labels")
        binding.chipGroup.removeAllViews()
        for (label in labels) {
            val yellowChip = Chip(requireContext())
            yellowChip.text = label
            yellowChip.isClickable = true
            yellowChip.setChipBackgroundColorResource(R.color.yellow)
            binding.chipGroup.addView(yellowChip)
        }
    }

    fun fetchText() {
        //Read text from image using ML-Kit
        RecognizeText.getTextFromImage(bitmap, this)

        //Generate labels from image using ML-Kit
        imageLabelingHelper.labelImage(bitmap) { labels ->
            labelList = labels
            printTags(labels)

        }
    }

    companion object {
        const val TAG = "ImageDetailsFrag"

    }

    private fun getDefaultBitmap(): Bitmap {
        return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
    }

    override fun onTextRecognized(text: String) {
        binding.descriptionText.text = text
    }

    override fun onTextRecognitionFailed(error: String) {
        binding.descriptionText.text = "No description found"

    }
}