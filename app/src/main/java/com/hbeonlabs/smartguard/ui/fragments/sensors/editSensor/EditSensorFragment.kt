package com.hbeonlabs.smartguard.ui.fragments.sensors.editSensor

import android.app.Activity
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hbeonlabs.smartguard.R
import com.hbeonlabs.smartguard.base.BaseFragment
import com.hbeonlabs.smartguard.data.local.models.Sensor
import com.hbeonlabs.smartguard.databinding.FragmentAddASensorBinding
import com.hbeonlabs.smartguard.databinding.FragmentEditASensorBinding
import com.hbeonlabs.smartguard.ui.activities.MainActivity
import com.hbeonlabs.smartguard.ui.fragments.sensors.SensorViewModel

import org.koin.android.ext.android.inject


class EditSensorFragment:BaseFragment<SensorViewModel,FragmentEditASensorBinding>() {

    var imageUri = "".toUri()

    private val args:EditSensorFragmentArgs by navArgs()


    private val startImagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!

                    imageUri = fileUri
                    binding.imgEditHubImage.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    snackBar(ImagePicker.getError(data))
                }
                else -> {
                    snackBar("Task Cancelled")
                }
            }
        }

    private  val sensorViewModel: SensorViewModel by inject()
    override fun getViewModel(): SensorViewModel {
            return sensorViewModel
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_add_a_sensor
    }

    override fun initView() {
        super.initView()

        (requireActivity() as MainActivity).binding.toolbarIconEnd.apply {
            visibility = View.INVISIBLE
        }
        (requireActivity() as MainActivity).binding.toolbarIconEnd.apply {
            visibility = View.INVISIBLE
        }


        binding.btnEditSensor.setOnClickListener {
            val sensorName = binding.edtAddHubSerial.text.toString()
            val customMessage = binding.edtEditSensorCustomMessage.text.toString()
            val sensor = Sensor(args.sensor.sensor_id,
                                sensorName,
            imageUri.toString(),
                args.sensor.sensor_type,
                args.sensor.sensor_arm_state,
                customMessage,
                args.sensor.sensor_registered_on,
                args.sensor.hub_serial_number
            )

            sensorViewModel.editSensor(sensor)
        }

        binding.btnUploadFromGallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .saveDir(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                .createIntent {
                    startImagePickerResult.launch(it)
                }
        }

        binding.btnUploadFromCamera.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .saveDir(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                .createIntent {
                    startImagePickerResult.launch(it)
                }
        }

    }



}