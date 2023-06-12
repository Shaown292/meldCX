package com.example.kotlin
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var text: TextView
    var time: Long = 0
    var interval: Long = 0
    var componentList: MutableList<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initialise layout
        listView = findViewById(R.id.all_list_v)
        text = findViewById(R.id.total_txt)
        var btn = findViewById<Button>(R.id.btn_show)
        var etTime = findViewById<EditText>(R.id.txt_time)
        var btn_set_timmer = findViewById<Button>(R.id.btn_timmer)

        // button for get all installed apps
        btn.setOnClickListener {
            getAllApps()
        }

        btn_set_timmer.setOnClickListener {
            time = (etTime.text.toString().toLong())*1000
            interval = 2
        }


        // list item click listener for open list items at specific possition
        listView.setOnItemClickListener { parent, view, position, id ->

            var packages = componentList[position]
            var launchApp = getPackageManager().getLaunchIntentForPackage(packages)

            val timer = object : CountDownTimer(time, interval){
                override fun onTick(millisUntilFinished: Long) {
                }
                override fun onFinish() {
                    if (launchApp != null) {
                        startActivity(launchApp)
                    } else {
                        Toast.makeText(applicationContext, "Package not found", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            timer.start()

        }
    }


    fun getAllApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        // get list
        val ril = packageManager.queryIntentActivities(mainIntent, 0)
        lateinit var name: String
        var i = 0
        // get size
        val apps = arrayOfNulls<String>(ril.size)
        for (ri in ril) {
            if (ri.activityInfo != null) {
                // get package
                val res = packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
                // if activity label res is found
                name = if (ri.activityInfo.labelRes != 0) {
                    res.getString(ri.activityInfo.labelRes)
                } else {
                    ri.activityInfo.applicationInfo.loadLabel(packageManager).toString()
                }
                componentList.add(ri.activityInfo.applicationInfo.packageName)
                apps[i] = name
                i++
            }
        }
        // set all the apps name in list view
        listView.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, apps)
        // write total count of apps available.
        text.text = ril.size.toString() + " Apps are installed"
    }

    override fun onStart() {
        super.onStart()
    }
}
