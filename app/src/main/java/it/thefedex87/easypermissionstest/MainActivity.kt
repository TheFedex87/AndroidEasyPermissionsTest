package it.thefedex87.easypermissionstest

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.thefedex87.easypermissionstest.databinding.ActivityMainBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding
    private var requestWithStd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            buttonCameraStandardPermissions.setOnClickListener {
                requestWithStd = true
                checkCameraPermissionStandard()
            }

            buttonCameraEasyPermissions.setOnClickListener {
                requestWithStd = false
                checkCameraPermissionEasy()
            }
        }
    }

    private fun checkCameraPermissionEasy() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            EasyPermissions.requestPermissions(this, "App need to access camera to user this amazing functionality", CAMERA_REQUEST_CODE, Manifest.permission.CAMERA)
        }
    }

    private fun checkCameraPermissionStandard() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.apply {
                        setMessage("App need to access camera to user thia amazing functionality")
                        setTitle("Permission required")
                        setPositiveButton("OK") { dialog, which ->
                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                        }
                    }
                    dialogBuilder.create().show()
                }
                else -> {
                    // You can directly ask for the permission.
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!requestWithStd) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        } else {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this@MainActivity, "Permission granted", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        const val CAMERA_REQUEST_CODE: Int = 100
    }
}