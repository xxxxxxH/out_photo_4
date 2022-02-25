package com.abc.photo.ui.bokeh

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
import com.abc.photo.item.BokehItem
import com.abc.photo.item.EffectItem
import com.abc.photo.item.StickerItem
import com.abc.photo.ui.PictureAct
import com.abc.photo.utils.CommonUtils
import com.abc.photo.utils.GUPUtil
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.StickerModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.sdsmdg.tastytoast.TastyToast
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.android.synthetic.main.activity_bokeh.*
import net.widget.DrawableSticker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter
import kotlin.concurrent.thread


class BokehActivity : AppCompatActivity() {

    var gpuImage: GPUImage? = null
    private var url: String? = null
    private var bokehAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var stickerAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var effectAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var progressDialog: AwesomeProgressDialog? = null

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    bokehAdapter!!.items = data
                }
                2 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    stickerAdapter!!.items = data
                }
                3 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    effectAdapter!!.items = data
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bokeh)
        EventBus.getDefault().register(this)
        url = intent.getStringExtra("url") as String
        img_main.displayImage(url)
        gpuImage = GPUImage(this)
        initData()
        initAdapter()
        initRadioButton()
    }

    private fun initData() {
        thread {
            val data = BokehUtils.getBokehRes(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)

        }
        thread {
            val data2 = BokehUtils.getStickers(this)
            val msg2 = Message()
            msg2.what = 2
            msg2.obj = data2
            handler.sendMessage(msg2)
        }
        thread {
            val data3: ArrayList<Bitmap> = ArrayList()
            for (index in 1 until 19) {
                gpuImage!!.setImage(BitmapFactory.decodeFile(url!!))
                gpuImage!!.setFilter(GUPUtil.createFilterForType(GUPUtil.getFilters().filters[index]))
                data3.add(gpuImage!!.bitmapWithFilterApplied)
            }
            val msg3 = Message()
            msg3.what = 3
            msg3.obj = data3
            handler.sendMessage(msg3)
        }
    }

    private fun initAdapter() {
        bokehAdapter = EasyRecyclerAdapter<Bitmap>(this, BokehItem::class.java, ArrayList<Bitmap>())
        recyclerBokeh.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerBokeh.adapter = bokehAdapter

        stickerAdapter =
            EasyRecyclerAdapter<Bitmap>(this, StickerItem::class.java, ArrayList<Bitmap>())
        recyclerSticker.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerSticker.adapter = stickerAdapter

        effectAdapter =
            EasyRecyclerAdapter<Bitmap>(this, EffectItem::class.java, ArrayList<Bitmap>())
        recyclerEffect.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerEffect.adapter = effectAdapter
    }


    private fun initRadioButton() {
        rgBokeh.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbBokeh -> {
                    recyclerBokeh.visibility = View.VISIBLE
                    recyclerEffect.visibility = View.GONE
                    recyclerSticker.visibility = View.GONE
                }
                R.id.rbEffect -> {
                    recyclerBokeh.visibility = View.GONE
                    recyclerEffect.visibility = View.VISIBLE
                    recyclerSticker.visibility = View.GONE
                }
                R.id.rbSticker -> {
                    recyclerBokeh.visibility = View.GONE
                    recyclerEffect.visibility = View.GONE
                    recyclerSticker.visibility = View.VISIBLE
                }
                R.id.rbDone -> {
                    progressDialog = CommonUtils.creteProgressDialog(this)
                    progressDialog!!.show()
                    thread {
                        CommonUtils.createBitmapFromView(main)
                    }
                    recyclerBokeh.visibility = View.GONE
                    recyclerEffect.visibility = View.GONE
                    recyclerSticker.visibility = View.GONE
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "bokehItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
            }
            "effectItem" -> {
                img_main.setImageBitmap(msg[1] as Bitmap)
                img_main.invalidate()
            }
            "stickerItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
            }
            "saveSuccess" -> {
                progressDialog!!.hide()
                TastyToast.makeText(this, "save success", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS)
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