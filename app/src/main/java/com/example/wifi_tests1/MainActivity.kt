package com.example.wifi_tests1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.OutputStream
import java.net.InetSocketAddress

import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import java.lang.Thread.sleep as sleep1



class MainActivity() : AppCompatActivity(), Parcelable {


    var allowtransmission:Byte=1

    lateinit var wifiButton: Switch
    lateinit var textView: TextView
    lateinit var button: Button
    lateinit var button2: Button
    lateinit var listv: ListView
    lateinit var wifiManager: WifiManager
    lateinit var resultList : ArrayList<ScanResult>
    //lateinit var client_:Socket

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager




        title = "KotlinApp"
        textView = findViewById(R.id.textView)
        wifiButton = findViewById(R.id.switchWifi)
        button = findViewById(R.id.button)
        button2 = findViewById(R.id.button2)
        listv=findViewById(R.id.wifiList)

        //var sa:SocketAddress= InetSocketAddress("192.168.1.4", 10002)
        //client_=Socket()


        wifiButton.setOnCheckedChangeListener { _, isChecked -> // TODO Auto-generated method stub
            if (isChecked)
            {
                textView.text = "WIFI ON"
                enableWiFi()
            }
            else
            {
                textView.text = "WIFI OFF"
                disableWiFi()
            }
        }

        button.setOnClickListener {

            var t:Int = 0
            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED )
            {
                t += 1
            }
            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED )
            {
                t=t+2
            }


            var REQUEST_CODE: Int =0
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE)

                    // REQUEST_CODE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }

            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                t=t+4
            }

            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                t=t+8
            }
            if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED )
            {
                t=t+16
            }

            startScanning()
            Toast.makeText(this, t.toString(), Toast.LENGTH_SHORT).show()
        }

         button2.setOnClickListener {

            // client_.connect(sa,10000)
            // client_.outputStream.write("Hello from the client!".toByteArray())
             //client_.close()
             if (button2.text!="Stop Msg")
             {
                 allowtransmission=1
                 val thread = thread { runthread() }
                 button2.text="Stop Msg"

             }
             else
             {
                 button2.text="Start Msg"
                 allowtransmission=0
             }


         }


    }

    private fun runthread(){

        val client = Client("192.168.1.4", 10002)
        val thread =  client.run()

    }


    private fun disableWiFi() {
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = false
        Toast.makeText(this, "Wifi Disabled", Toast.LENGTH_SHORT).show()
    }
    private fun enableWiFi() {
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        Toast.makeText(this, "Wifi Enabled", Toast.LENGTH_SHORT).show()
    }



    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            resultList = wifiManager.scanResults as ArrayList<ScanResult>
            //Toast.makeText(this, "onReceive Called", Toast.LENGTH_SHORT).show()
        }
    }


    fun startScanning() {
        wifiManager.startScan()
        registerReceiver(broadcastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        Toast.makeText(this, "onReceive Called", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            stopScanning()
        }, 10000)
    }

    fun stopScanning() {
        unregisterReceiver(broadcastReceiver)
      /*  val axisList = ArrayList<Axis>()*/
        var ll: String
        var list: ArrayList<String>
        ll=""
        list = ArrayList<String>()

        for (result in resultList) {
            ll= result.SSID.toString()
            if ("Voda" in ll) {
                //+ " " + result.BSSID.toString() + " " + result.level.toString()
                list.add(ll) //result.BSSID + " " + result.level)
            }
        }

        var mHistory: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listv.setAdapter(mHistory);
        Toast.makeText(this, ll, Toast.LENGTH_SHORT).show()

    }





    inner class Client(address: String, port: Int) {


        public fun colortoJason(red:UByte, green: UByte, blue:UByte,alpha:UByte):String
        {
            var mystring:String=""
            mystring="{" + "\""+ "red" + "\"" + ":" + red.toString() + "," +
                    "\""+ "green" + "\"" + ":" + green.toString() + "," +
                    "\""+ "blue" + "\"" + ":" + blue.toString() + "," +
                    "\""+ "alpha" + "\"" + ":" + alpha.toString()  +
                    "}"
            return mystring
        }

        private val connection: Socket = Socket(address, port)
        private var connected: Boolean = true

        init {
            println("Connected to server at $address on port $port")
        }

        private val reader: Scanner = Scanner(connection.getInputStream())
        private val writer: OutputStream = connection.getOutputStream()
        private var writestuff:String =""



         fun run() {
            writestuff= colortoJason(12u, 34u, 34u,58u)
            var cc=0
             var ss=1
            thread { read() }
            while (connected) {
                val input = readLine() ?: ""
                if (("exit" in input) || (allowtransmission<1)) {
                    connected = false
                    reader.close()
                    connection.close()
                } else {
                    write(writestuff)
                    if (cc==255)
                    {
                        ss = -1
                    }
                    if (cc==0)
                    {
                        ss = 1
                    }
                    cc=cc+ss
                    writestuff= colortoJason(cc.toUByte(), 34u, 34u,58u)
                    Thread.sleep(1_0)

                }
            }

        }

        private fun write(message: String) {
            writer.write((message).toByteArray(Charset.defaultCharset()))
        }

        private fun read() {
            while (connected)
                println(reader.nextLine())
        }
    }




    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}