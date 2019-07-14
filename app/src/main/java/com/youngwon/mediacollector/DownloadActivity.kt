package com.youngwon.mediacollector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_download.*
import java.io.BufferedWriter
import java.io.FileWriter

class DownloadActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (intent.hasExtra("url")) {
            textInputEditText.setText(intent.getStringExtra("url"))
            DownloadAsync().execute(intent.getStringExtra("url"))
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val url = textInputEditText.text.toString()
            DownloadAsync().execute(url)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun saveToInnerStorage(text:String) {
        val bw = BufferedWriter(FileWriter(filesDir.toString() + "history.txt", true))
        bw.write(text+"\n")
        bw.close()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                startActivity(Intent(this@DownloadActivity, MainActivity::class.java))
            }
            R.id.nav_history -> {
                // Handle the camera action
            }
            R.id.nav_download -> {
                startActivity(Intent(this@DownloadActivity, DownloadActivity::class.java))
            }
            R.id.nav_setting -> {
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadAsync : AsyncTask<String, String, ArrayList<String>?>() {

        @SuppressLint("InflateParams")
        private val dialogView: View = LayoutInflater.from(this@DownloadActivity).inflate(R.layout.progressbar, null)
        private val alert: AlertDialog.Builder = AlertDialog.Builder(this@DownloadActivity).setView(dialogView).setCancelable(false)
        private val dialog: AlertDialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): ArrayList<String>? {
            if(MediaDownload().mediadownload(url[0]) != null) {
                url[0]?.let { saveToInnerStorage(it) }
            }
            return MediaDownload().mediadownload(url[0])
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            dialog.dismiss()
            if(result == null) {
                Toast.makeText(this@DownloadActivity,"잘못된 url", Toast.LENGTH_LONG).show()
            }
            else{
                val mAdapter = DownloadRvAdapter(this@DownloadActivity,result)
                recycler.adapter = mAdapter
                val lm = LinearLayoutManager(this@DownloadActivity)
                recycler.layoutManager = lm
                recycler.setHasFixedSize(true)
                selectbutton.visibility = View.VISIBLE
                totalbutton.visibility = View.VISIBLE
            }
        }
    }
}
