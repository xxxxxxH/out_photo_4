package com.abc.lib.b

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.abc.lib.u.CommonUtils
import com.abc.lib.u.MyReceiver
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x

abstract class BaseActivity : AppCompatActivity() {

    protected var appLink: String? = null
    protected var installReferrer: String? = null
    var msgCount = 0

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                msgCount++
                if (msgCount == 10) {
                    closeLoading()
                    startMainActivity()
                } else {
                    if (!TextUtils.isEmpty(appLink) && !TextUtils.isEmpty(installReferrer)) {
                        Log.i("xxxxxxH", "$appLink-----$installReferrer")
                        closeLoading()
                        startMainActivity()
                    } else {
                        sendEmptyMessageDelayed(1, 1000)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        val intentFilter = IntentFilter()
        intentFilter.addAction("action_download")
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addDataScheme("package")
        registerReceiver(MyReceiver(), intentFilter)
        PermissionsUtil.requestPermission(this, object : PermissionListener {
            override fun permissionGranted(permission: Array<out String>) {
                showLoading()
                val request = RequestParams("https://sichuanlucking.xyz/navigation489/fb.php")
                x.http().get(request, object : Callback.CommonCallback<String> {
                    override fun onSuccess(result: String?) {
                        if (TextUtils.isEmpty(result)) {
                            CommonUtils.initFbSdk(this@BaseActivity, CommonUtils.default_id)
                        } else {
                            CommonUtils.initFbSdk(this@BaseActivity, result!!)
                        }
                        appLink = CommonUtils.getFbInfo(this@BaseActivity)
                        installReferrer = CommonUtils.getGooInfo(this@BaseActivity)
                        handler.sendEmptyMessage(1)
                    }

                    override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                        CommonUtils.initFbSdk(this@BaseActivity, CommonUtils.default_id)
                        appLink = CommonUtils.getFbInfo(this@BaseActivity)
                        installReferrer = CommonUtils.getGooInfo(this@BaseActivity)
                        handler.sendEmptyMessage(1)
                    }

                    override fun onCancelled(cex: Callback.CancelledException?) {

                    }

                    override fun onFinished() {

                    }

                })

            }

            override fun permissionDenied(permission: Array<out String>) {
                finish()
            }

        }, *BaseApplication.instance!!.getPermissions())
    }

    abstract fun getLayoutId(): Int

    abstract fun startMainActivity()

    abstract fun showLoading()

    abstract fun closeLoading()
}