package com.example.alleassingment.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.alleassingment.ui.MainActivity
import com.example.alleassingment.R
import com.example.alleassingment.databinding.FragmentEditDetailsBinding
import com.example.alleassingment.ui.ImageDetails
import com.example.alleassingment.ui.ImageDetailsViewModel
import com.example.alleassingment.ui.bottomsheet.EditCollectionsBottomSheet
import com.google.android.material.chip.Chip
import com.example.alleassingment.util.ValueFragment
import com.example.alleassingment.util.collectItems


class EditDetailsFrag : ValueFragment<FragmentEditDetailsBinding>(R.layout.fragment_edit_details) {
    private lateinit var binding: FragmentEditDetailsBinding
    private val model: ImageDetailsViewModel by activityViewModels()
    private var editCollectionBottomSheet = EditCollectionsBottomSheet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.hideBottomNavigation()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setOnClick()
        setUpObserver()
        onBackPressed {
            model.sendScreenData(
                ImageDetails(
                    binding.adNoteEt.text.toString(),
                    labels = model.screenData?.labels ?: emptyList(),
                    description = binding.descriptionText.text.toString()
                )
            )
            parentFragmentManager.popBackStack()
        }
    }



    fun setUpObserver() {
        model.collectionData.collectItems(viewLifecycleOwner) {
            model.updateCollectionsList(it)
            addLabel(it)
        }
    }

    fun setOnClick() {
        binding.edit.setOnClickListener {
            EditCollectionsBottomSheet.newInstance(
                "labels",
                model.screenData?.labels ?: emptyList()
            ).show(
                childFragmentManager,
                EditCollectionsBottomSheet.TAG
            )
        }
        binding.back.setOnClickListener{
            model.sendScreenData(
                ImageDetails(
                    binding.adNoteEt.text.toString(),
                    labels = model.screenData?.labels ?: emptyList(),
                    description = binding.descriptionText.text.toString()
                )
            )
            parentFragmentManager.popBackStack()
        }
    }

    private fun setUi() {
        editCollectionBottomSheet = EditCollectionsBottomSheet()
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
        model?.screenData?.let {
            binding.descriptionText.text = it.description
            binding.adNoteEt.setText(it.note ?: "")
            addLabel(it.labels)

        }
    }

    fun addLabel(labels: List<String>) {
        binding.chipGroup.removeAllViews()
        for (label in labels) {
            val yellowChip = Chip(requireContext())
            yellowChip.text = label
            yellowChip.isClickable = true
            yellowChip.setChipBackgroundColorResource(R.color.yellow)
            binding.chipGroup.addView(yellowChip as View, 0)
        }
    }

    companion object {
    }

}