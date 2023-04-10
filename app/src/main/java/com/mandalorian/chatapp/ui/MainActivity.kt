package com.mandalorian.chatapp.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.mandalorian.chatapp.R
import com.mandalorian.chatapp.data.receiver.MyBroadcastReceiver
import com.mandalorian.chatapp.data.receiver.OtpReceiver
import com.mandalorian.chatapp.service.AuthService
import com.mandalorian.chatapp.service.MyService
import com.mandalorian.chatapp.utils.Constants
import com.mandalorian.chatapp.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val NOTIFICATION_REQ_CODE = 0
    private val FOREGROUND_REQ_CODE = 1
    lateinit var myBroadcastReceiver: MyBroadcastReceiver
    lateinit var myOtpReceiver: OtpReceiver
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var myService: MyService
    lateinit var myServiceIntent: Intent

    @Inject
    lateinit var authRepo: AuthService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = intent.data

        Log.d(Constants.DEBUG, data.toString())

        registerActivityResult()

        NotificationUtils.createNotificationChannel(this)
        checkPermission("android.permission.POST_NOTIFICATIONS", NOTIFICATION_REQ_CODE)
        checkPermission("android.permission.FOREGROUND_SERVICE", FOREGROUND_REQ_CODE)

        navController = findNavController(R.id.navHostFragment)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        NavigationUI.setupWithNavController(navigationView, navController)
//        navigationView.setupWithNavController(navController)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            .build()
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            authRepo.signOut()
            findNavController(R.id.navHostFragment).navigate(R.id.loginFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        if (!authRepo.isLoggedIn()) {
            findNavController(R.id.navHostFragment).navigate(R.id.loginFragment)
        } else {
            findNavController(R.id.navHostFragment).navigate(R.id.homeFragment)
        }

        registerBroadcastReceiver()

        registerOtpReceiver()
    }

    fun startService() {
        myService = MyService()
        myServiceIntent = Intent(this, MyService::class.java).also {
            intent.putExtra("EXTRA_DATA", "Hello from main")
            startService(it)
        }
    }

    fun stopService() {
        Intent(this, MyService::class.java).also {
            stopService(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
        unregisterReceiver(myOtpReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onNavigateUp()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Log.d(Constants.DEBUG, "Permission is granted already")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_REQ_CODE -> {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            }
            FOREGROUND_REQ_CODE -> {
                Toast.makeText(this, "Foreground service permission is granted", Toast.LENGTH_LONG)
                    .show()
            }
            else -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerActivityResult() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                data?.let {
                    val msg = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).toString()
                    val otp = Regex("\\d{4,6}").find(msg)?.value ?: ""
                }
            }
        }
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.mandalorian.MyBroadcast")
        myBroadcastReceiver = MyBroadcastReceiver()
        registerReceiver(myBroadcastReceiver, filter)
    }

    private fun registerOtpReceiver() {
        myOtpReceiver = OtpReceiver()
        OtpReceiver.bind(object : OtpReceiver.Companion.Listener {
            override fun onSuccess(messageIntent: Intent) {
                resultLauncher.launch(messageIntent)
            }

            override fun onFailure() {
                TODO("Not yet implemented")
            }
        })
        val otpFilter = IntentFilter()
        otpFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(myOtpReceiver, otpFilter)
    }
}
