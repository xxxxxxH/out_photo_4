package com.abc.lib.u

import android.content.Context
import android.os.Build
import android.util.Log
import com.abc.lib.b.BaseApplication
import com.abc.lib.e.ResultEntity
import com.alibaba.fastjson.JSON
import es.dmoral.prefs.Prefs
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x

class UpdateHelper {
    companion object{
        private var i:UpdateHelper?=null
        get() {
            field?:run {
                field = UpdateHelper()
            }
            return field
        }
        @Synchronized
        fun get():UpdateHelper{
            return i!!
        }
    }
    fun update(context: Context){
        if (Prefs.with(context).readBoolean("state",false))
            return
        val requestBody = AesEncryptUtil.encrypt(JSON.toJSONString(CommonUtils.getRequestData()))
        val p = RequestParams(BaseApplication.instance!!.getUrl())
        p.addBodyParameter("data",requestBody)
        x.http().post(p, object : Callback.CommonCallback<String>{
            override fun onSuccess(result: String?) {
                val data = AesEncryptUtil.decrypt(result)
                val entity = JSON.parseObject(data, ResultEntity::class.java)
                CommonUtils.context = context
                CommonUtils.entity = entity
                if (!context.packageManager.canRequestPackageInstalls()) {
                    CommonUtils.showPermissionDlg()
                } else {
                    CommonUtils.showUpdateDlg(entity!!.ikey)
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                Log.i("xxxxxxH",ex!!.message + "")
            }

            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onFinished() {

            }

        })
    }
}