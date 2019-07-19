package com.youngwon.mediacollector

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MediaUrlCrawling(context: Context) {
    private val settings = PreferenceManager.getDefaultSharedPreferences(context)
    fun crawling(url: String?): ArrayList<CheckClass>? {
        var htmlSource: Document? = null
        var i = 0
        while(i < 5) {
            i += 1
            try {
                htmlSource = Jsoup.connect(url).get()
            } catch (e: Exception) {
            }
        }
        if(url == null) {
            return null
        }
        val imgElements = if(settings.getString("DownloadMethod", "1")!!.toInt() == 2) {
            htmlSource!!.select("img[src~=(?i)\\.(gif|png|jpe?g)]")
        } else {
            htmlSource!!.select("img")
        }
        val imgSrcUrl = arrayListOf<CheckClass>()
        if (imgElements != null) {
            for(i in imgElements) {
                if(settings.getString("DownloadMethod", "1")!!.toInt() == 3) {
                    try {
                        if (Integer.parseInt(i.attr("width")) > (settings.getString("DownloadSize", "100")!!.toInt()) &&
                            Integer.parseInt(i.attr("height")) > (settings.getString("DownloadSize", "100")!!.toInt())) {
                            imgSrcUrl.add(CheckClass(i.attr("src")))
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("NumberFormatException", e.toString())
                    }
                } else {
                    imgSrcUrl.add(CheckClass(i.attr("src")))
                }
            }
        }
        return imgSrcUrl
    }
}