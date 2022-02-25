package com.abc.photo.ui.pip

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abc.photo.R
import com.abc.photo.item.BgItem
import com.abc.photo.item.PipItem
import com.abc.photo.item.StickerItem
import com.abc.photo.ui.PictureAct
import com.abc.photo.utils.CommonUtils
import com.abc.photo.utils.GUPUtil
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.StickerModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.sdsmdg.tastytoast.TastyToast
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.android.synthetic.main.activity_color.*
import kotlinx.android.synthetic.main.activity_pip.*
import kotlinx.android.synthetic.main.activity_pip.img_main
import kotlinx.android.synthetic.main.activity_pip.main
import kotlinx.android.synthetic.main.activity_pip.recyclerS
import kotlinx.android.synthetic.main.activity_pip.stickerView
import net.widget.DrawableSticker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter
import kotlin.concurrent.thread

class PipActivity : AppCompatActivity() {
    var gpuImage: GPUImage? = null
    private var url: String? = null
    private var progressDialog: AwesomeProgressDialog? = null

    private var pipAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var bgAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var stickerAdapter: EasyRecyclerAdapter<Bitmap>? = null

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    pipAdapter!!.items = data
                }
                2 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    bgAdapter!!.items = data
                }
                3 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    stickerAdapter!!.items = data
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pip)
        EventBus.getDefault().register(this)
        url = intent.getStringExtra("url") as String
        img_main.displayImage(url)
        gpuImage = GPUImage(this)
        initData()
        initAdapter()
        initRadiobutton()
    }

    private fun initData() {
        thread {
            val data = PipUtils.getPip(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)
        }

        thread {
            val data: ArrayList<Bitmap> = ArrayList()
            for (index in 1 until 19) {
                gpuImage!!.setImage(BitmapFactory.decodeFile(url!!))
                gpuImage!!.setFilter(GUPUtil.createFilterForType(GUPUtil.getFilters().filters[index]))
                data.add(gpuImage!!.bitmapWithFilterApplied)
            }
            val msg = Message()
            msg.what = 2
            msg.obj = data
            handler.sendMessage(msg)
        }

        thread {
            val data = PipUtils.getStickers(this)
            val msg = Message()
            msg.what = 3
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    private fun initAdapter() {
        pipAdapter = EasyRecyclerAdapter(this, PipItem::class.java, ArrayList<Bitmap>())
        recyclerP.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerP.adapter = pipAdapter

        bgAdapter = EasyRecyclerAdapter(this, BgItem::class.java, ArrayList<Bitmap>())
        recyclerB.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerB.adapter = bgAdapter

        stickerAdapter = EasyRecyclerAdapter(this, StickerItem::class.java, ArrayList<Bitmap>())
        recyclerS.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerS.adapter = stickerAdapter
    }

    private fun initRadiobutton() {
        rgPip.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbp -> {
                    recyclerP.visibility = View.VISIBLE
                    recyclerB.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbb -> {
                    recyclerP.visibility = View.GONE
                    recyclerB.visibility = View.VISIBLE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbs -> {
                    recyclerP.visibility = View.GONE
                    recyclerB.visibility = View.GONE
                    recyclerS.visibility = View.VISIBLE
                }
                R.id.rbd -> {
                    progressDialog = CommonUtils.creteProgressDialog(this)
                    progressDialog!!.show()
                    thread {
                        CommonUtils.createBitmapFromView(main)
                    }
                    recyclerP.visibility = View.GONE
                    recyclerB.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "pipItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
            }
            "bgItem" -> {
                img_main.setImageBitmap(msg[1] as Bitmap)
            }
            "stickerItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
            }
            "saveSuccess" -> {
                progressDialog!!.hide()
                TastyToast.makeText(
                    this,
                    "save success",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.SUCCESS
                )
                startActivity(Intent(this, PictureAct::class.java))
                finish()
            }
            "saveError" -> {
                progressDialog!!.hide()
                TastyToast.makeText(this, "save failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}