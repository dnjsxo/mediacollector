package com.youngwon.mediacollector

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log


class DownloadService : ClipboardManager.OnPrimaryClipChangedListener,Service() {

    var mManager: ClipboardManager? = null
    var i = true
    override fun onPrimaryClipChanged() {
        if (mManager != null && mManager!!.getPrimaryClip() != null) {
            if(i) {
                val data = mManager?.getPrimaryClip()?.getItemAt(0)?.getText()
                Log.e("te출력","$data")
                sendMsgToActivity(data as String)
                i = false
            } else {
                i = true
            }
        }
    }

    private val mIBinder = MyBinder()

    internal inner class MyBinder : Binder() {
        fun getService(): DownloadService {
            return this@DownloadService
        }
    }
    override fun onBind(intent: Intent): IBinder {
        return mMessenger.binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        mManager?.addPrimaryClipChangedListener(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mManager?.removePrimaryClipChangedListener(this);
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    private val mMessenger = Messenger(Handler(Handler.Callback { msg ->
        Log.i("test", "act : what " + msg.what)
        when (msg.what) {
            DownloadService().MSG_REGISTER_CLIENT -> {
                mClient = msg.replyTo
            }
        }
        false
    }))

    val MSG_SEND_TO_ACTIVITY = 4
    val MSG_REGISTER_CLIENT = 1
    private var mClient: Messenger? = null
    private fun sendMsgToActivity(sendValue: String) {
        try {
            val bundle = Bundle()
            Log.e("service 메세지 함수","$sendValue")
            bundle.putString("fromService", sendValue)
            val msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY)
            msg.setData(bundle)
            if (mClient != null) {
                Log.e("service 메세지 전송직전","$sendValue")
                mClient!!.send(msg)
            }      // msg 보내기
        } catch (e: RemoteException) {
        }
    }
}
