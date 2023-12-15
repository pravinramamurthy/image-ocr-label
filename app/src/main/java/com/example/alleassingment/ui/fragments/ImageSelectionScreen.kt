package com.example.alleassingment.ui.fragments

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alleassingment.R
import com.example.alleassingment.databinding.FragmentImageSelectionScreenBinding
import com.example.alleassingment.model.ImageModel
import com.example.alleassingment.ui.ImageDetailsViewModel
import com.example.alleassingment.ui.adapter.ImageAdapter
import com.example.alleassingment.util.ValueFragment
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class ImageSelectionScreen :
    ValueFragment<FragmentImageSelectionScreenBinding>(R.layout.fragment_image_selection_screen) {
    lateinit var _binding: FragmentImageSelectionScreenBinding
    private val binding get() = _binding!!
    private val permissionsToRequest = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES

    )
    private val viewModel: ImageDetailsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageSelectionScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchImages()
        onBackPressed {
            viewModel.screenData = null
            requireActivity().finish()
        }
    }

    private fun setupRecyclerView(imageList: List<ImageModel>) {
        val recyclerView: RecyclerView = binding.imagesRecyclerview
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ImageAdapter(requireContext(), imageList)
        recyclerView.adapter = adapter
        if (imageList.isNotEmpty()) {

            imageList.let {
                val imageUri = Uri.parse(it[0].imagePath)
                val bitmap = uriToBitmap(imageUri)
                if (bitmap != null) {
                    viewModel.updateBitmap(bitmap)
                }
                Glide.with(requireContext())
                    .load(it[0].imagePath)
                    .into(binding.selectedImageView)
            }
        }
        adapter.onItemClickListener = { clickedImage ->
            val imageUri = Uri.parse(clickedImage.imagePath)
            val bitmap = uriToBitmap(imageUri)
            if (bitmap != null) {
                viewModel.updateBitmap(bitmap)
            }
            Glide.with(requireContext())
                .load(clickedImage.imagePath)
                .into(binding.selectedImageView)
        }
    }

    fun navigateToNextFrag(bundle: Bundle) {
        val nextFragment = ImageDetailsFrag()
        nextFragment.arguments = bundle
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, nextFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        try {
            val contentResolver: ContentResolver = context?.contentResolver!!
            inputStream = contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun fetchImages() {
        if (checkPermission()) {
            setupRecyclerView(getAllImages())
            Log.d(TAG, "fetchImages: ${getAllImages()}")
        } else {
            requestPermission()
        }
    }

    private fun getAllImages(): List<ImageModel> {
        val imageList = mutableListOf<ImageModel>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )
        val selection = "${MediaStore.Images.Media.MIME_TYPE}=?"
        val selectionArgs = arrayOf("image/jpeg", "image/png")
        val cursor = context?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val data = it.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageList.add(ImageModel(contentUri.toString()))
            }
        }

        return imageList
    }

    private fun requestPermission() {
        requestMultiplePermissions.launch(permissionsToRequest)

    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permission = it.key
                val isGranted = it.value
                if (isGranted) {
                    Log.e(TAG, "$permission is granted")
                    setupRecyclerView(getAllImages())

                } else {
                    Log.e(TAG, "$permission is denied")
                }

            }
        }

    private fun checkPermission(): Boolean {
        val permissionsToGrant = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        return permissionsToGrant.isEmpty()
    }

    companion object {
        const val TAG = "ImageSelectionScreen"
    }
}

