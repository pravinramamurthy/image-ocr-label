package com.example.alleassingment.ui.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.fragment.app.activityViewModels
import com.example.alleassingment.R
import com.example.alleassingment.databinding.EditLableBottomSheetBinding
import com.example.alleassingment.ui.ImageDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip


/**
 * Created by Praveen on 04,August,2023
 */
class EditCollectionsBottomSheet : BottomSheetDialogFragment() {
    private var labels: List<String> = emptyList<String>()
    private lateinit var binding: EditLableBottomSheetBinding
    private val viewModel: ImageDetailsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditLableBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        labels = ArrayList<String>(arguments?.getStringArrayList("labels"))
        for (label in labels) {
            addChip(label)
        }
        binding.recipientInputET.requestFocus()
        textWatch()
        setUpOnCLick()

    }

    fun setUpOnCLick() {
        binding.doneBT.setOnClickListener {
            val chipTextList = mutableListOf<String>()
            for (i in 0 until binding.recipientGroupFL.childCount) {
                val child = binding.recipientGroupFL.getChildAt(i)
                if (child is Chip) {
                    chipTextList.add(child.text.toString())
                }
            }
            viewModel.sendData(chipTextList)
            dismiss()
        }
    }

    private var currentChip: Chip? = null

    fun textWatch() {
        binding.recipientInputET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s?.toString()?.trim()

                // Check if the entered text is not empty
                if (enteredText.isNullOrBlank()) {
                    // Reset current chip if text is empty
                    currentChip = null
                    binding.chipGroup.removeAllViews()
                } else {
                    addOrUpdateChip(enteredText)
                }
            }
        })
    }

    private fun addOrUpdateChip(person: String) {
        if (currentChip == null) {
            // If there is no current chip, create a new one
            val chip = generateNewChip(person)
            currentChip = chip
        } else {
            // If there is a current chip, update its text
            currentChip?.text = person
        }
    }

    // Function is to generate chip while typing
    fun generateNewChip(label: String): Chip {
        val chip = Chip(context)

        chip.text = label
        chip.isClickable = true
        chip.setChipBackgroundColorResource(R.color.yellow)
        binding.chipGroup.addView(chip)

        chip.setOnClickListener {
            // Handle chip click here
            binding.recipientInputET.setText("")
            addChip(chip.text.toString())
        }
        return chip

    }

    fun addChip(label: String) {
        val yellowChip = Chip(requireContext())
        val marginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        yellowChip.setOnCloseIconClickListener {
            binding.recipientGroupFL.removeView(yellowChip)
        }
        marginLayoutParams.setMargins(4, 4, 4, 4) // left, top, right, bottom
        yellowChip.layoutParams = marginLayoutParams
        yellowChip.text = label
        yellowChip.isClickable = true
        yellowChip.isCloseIconVisible = true
        yellowChip.marginStart
        yellowChip.setChipBackgroundColorResource(R.color.yellow)
        if (binding.recipientGroupFL.childCount > 0) {
            binding.recipientGroupFL.addView(
                yellowChip,
                binding.recipientGroupFL.childCount - 1
            )

        } else {
            binding.recipientGroupFL.addView(yellowChip, 0)

        }
    }


    companion object {
        const val TAG = "EditCollectionsBottomSheet"

        fun newInstance(
            key: String,
            value: List<String>
        ): EditCollectionsBottomSheet {
            return EditCollectionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putStringArrayList(key, ArrayList(value))

                }
            }
        }
    }
}