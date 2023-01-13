package com.perennial.receivernotifier

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.perennial.receivernotifier.databinding.ActivityMainBinding
import com.perennial.receivernotifier.receiver.EventReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var eventReceiver: EventReceiver
    private lateinit var wifiManager: WifiManager
    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(eventReceiver, intentFilter)
        val intentFilterWifi = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(eventReceiver, intentFilterWifi)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        eventReceiver = EventReceiver()
        binding.swAirplaneMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.swAirplaneMode.text = "Airplane Mode On"
            } else {
                binding.swAirplaneMode.text = "Airplane Mode Off"
            }
        }
        binding.swWifiManager.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.swWifiManager.text = "WiFi is On"
            } else {
                binding.swWifiManager.text = "Wifi is off"
            }
        }

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(eventReceiver)
    }
}