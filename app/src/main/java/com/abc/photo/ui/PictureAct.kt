package com.abc.photo.ui

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.abc.photo.R
import com.abc.photo.item.PictureItem
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.ScreenUtils
import com.lcw.library.imagepicker.utils.Utils
import com.sdsmdg.tastytoast.TastyToast
import com.tencent.mmkv.MMKV
import jahirfiquitiva.libs.fabsmenu.FABsMenu
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener
import kotlinx.android.synthetic.main.activity_color.*
import kotlinx.android.synthetic.main.activity_picture.*
import kotlinx.android.synthetic.main.layout_float.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter

class PictureAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        val keySet = MMKV.defaultMMKV()!!.decodeStringSet("keys") as HashSet?
        val data = ArrayList<String>()
        if (keySet != null) {
            for (item in keySet) {
                MMKV.defaultMMKV()!!.decodeString(item)?.let {
                    data.add(it)
                }
            }
        }
        if (data.size > 0){
            val adapter = EasyRecyclerAdapter(this,PictureItem::class.java,data)
            recycler.layoutManager = GridLayoutManager(this , 3)
            recycler.adapter = adapter
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        if (msg[0] == "picItem"){
            val i = Intent(this,PreviewAct::class.java)
            i.putExtra("url",msg[1] as String)
            startActivity(i)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}