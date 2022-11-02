package com.hbeonlabs.smartguard.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

fun Fragment.makeToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}


fun Fragment.makeToast(@StringRes res: Int) {
    Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show()
}

fun Fragment.snackBar(text: String) {
    Snackbar.make(
        requireView(),
        text,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Fragment.snackBar(@StringRes res: Int) {
    Snackbar.make(
        requireView(),
        res,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun <T> Fragment.collectLatestLifeCycleFlow(
    flow: Flow<T>,
    collect: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun Context.getBitmap(uri: Uri): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
    else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

fun Context.hideKeyboard(view:View)
{
    val inputMethodManager =
        this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


private fun Fragment.sendSMS2(phoneNumber:String,message:String,SENT:PendingIntent?,DELIVERY:PendingIntent?)
{
    try {
        val smsManager: SmsManager
        if (Build.VERSION.SDK_INT>=23) {
            smsManager = this.requireActivity().getSystemService(SmsManager::class.java)
        }
        else{
            smsManager = SmsManager.getDefault()
        }

        smsManager.sendTextMessage(phoneNumber, null, message, SENT, DELIVERY)

        Toast.makeText(this.requireContext(), "Message Sent", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(this.requireContext(), e.message.toString(), Toast.LENGTH_LONG)
            .show()
        Log.d("TAG", "sendSMS: "+e.localizedMessage)
    }
}

 fun Fragment.sendSMS(phoneNumber:String,  message:String, deliveredListener:()->Unit ) {
    val SENT = "SMS_SENT"
    val  DELIVERED = "SMS_DELIVERED"

    val sentPI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(requireContext(), 0, Intent(SENT),PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getBroadcast(requireContext(), 0, Intent(SENT),0)
    }
    val deliveredPI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(requireContext(), 0,  Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getBroadcast(requireContext(), 0,  Intent(DELIVERED), 0)
    }

    requireActivity().registerReceiver(object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            when(resultCode)
            {
                Activity.RESULT_OK->
                    Toast.makeText(requireContext(), "SMS sent",
                        Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_GENERIC_FAILURE->

                    Toast.makeText(requireContext(), "Generic failure",
                        Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NO_SERVICE->

                    Toast.makeText(requireContext(), "No service",
                        Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NULL_PDU->

                    Toast.makeText(requireContext(), "Null PDU",
                        Toast.LENGTH_SHORT).show()


                SmsManager.RESULT_ERROR_RADIO_OFF->

                    Toast.makeText(requireContext(), "Radio off",
                        Toast.LENGTH_SHORT).show()

            }
        }

    }, IntentFilter(SENT))


    requireActivity().registerReceiver(object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            when(resultCode)
            {
                Activity.RESULT_OK-> deliveredListener()
                Activity.RESULT_CANCELED-> makeToast("SMS not delivered")


            }
        }

    }, IntentFilter(SENT))

    sendSMS2(phoneNumber, message,sentPI,deliveredPI)

}