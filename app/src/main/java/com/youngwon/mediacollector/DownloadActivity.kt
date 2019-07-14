package com.youngwon.mediacollector

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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


class DownloadActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val filename = "log.txt"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val url = textInputEditText.text.toString()
            downloadasync().execute(url)
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
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
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

    inner class downloadasync() : AsyncTask<String, String, ArrayList<String>?>() {

        val dialogView = LayoutInflater.from(this@DownloadActivity).inflate(R.layout.progressbar, null)
        val alert = AlertDialog.Builder(this@DownloadActivity).setView(dialogView).setCancelable(false)
        val dialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): ArrayList<String>? {
            if(MediaDownload().MediaDownload(url[0]) != null) {
                url[0]?.let { saveToInnerStorage(it) }
            }
            return MediaDownload().MediaDownload(url[0])
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
                lm.orientation = LinearLayoutManager.HORIZONTAL
                recycler.layoutManager = lm
                recycler.setHasFixedSize(true)
            }
        }
    }
}
