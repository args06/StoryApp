package com.example.storyapp.ui.insert

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Results
import com.example.storyapp.databinding.FragmentInsertBinding
import com.example.storyapp.utils.Helper
import com.example.storyapp.utils.Helper.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class InsertFragment : Fragment() {

    private var _binding: FragmentInsertBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InsertViewModel by viewModels()

    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLatitude: Float? = null
    private var currentLongitude: Float? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { getSessionData() }
        binding.switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked)
                getMyLocation()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireActivity())
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                Helper.rotateFile(file, true)
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun getSessionData() {
        viewModel.getUserSessionData().observe(viewLifecycleOwner) { userData ->
            if (userData != null) uploadImage(userData.token)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        Helper.createTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(), "com.example.storyapp", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage(token: String) {
        if (getFile != null) {
            val reducedFile = Helper.reduceFileImage(getFile as File)
            val caption = binding.etCaption.text.toString().trimEnd()
                .toRequestBody("text/plain".toMediaType())

            val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", reducedFile.name, requestImageFile
            )

            viewModel.uploadImage(token, imageMultipart, caption, currentLatitude, currentLongitude)
                .observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Results.Loading -> {
                                showLoading(true)
                            }

                            is Results.Success -> {
                                showLoading(false)
                                val uploadStatus = result.data
                                if (!uploadStatus) {
                                    findNavController().popBackStack()
                                }
                            }

                            is Results.Error -> {
                                showLoading(false)
                                showSnackBar(result.error)
                            }
                        }
                    }
                }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.ivLoading.visibility = View.VISIBLE
        } else {
            binding.ivLoading.visibility = View.GONE
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.scrollLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun checkEachPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkPermission() {
        if(!checkEachPermission(REQUIRED_PERMISSIONS)){
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun getMyLocation() {
        checkPermission()
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLatitude = location.latitude.toFloat()
                currentLongitude = location.longitude.toFloat()
            } else {
                showSnackBar(getString(R.string.location_not_found))
                binding.switchLocation.isChecked = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}