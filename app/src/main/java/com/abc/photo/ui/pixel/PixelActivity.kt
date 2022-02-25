package com.abc.photo.ui.pixel

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abc.photo.R
import com.abc.photo.item.ColorItem
import com.abc.photo.item.EffectItem
import com.abc.photo.item.StickerItem
import com.abc.photo.ui.PictureAct
import com.abc.photo.utils.CommonUtils
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.ScreenUtils
import com.abc.photo.utils.StickerModel
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.sdsmdg.tastytoast.TastyToast
import kotlinx.android.synthetic.main.activity_pixel.*
import net.widget.DrawableSticker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter
import kotlin.concurrent.thread

class PixelActivity : AppCompatActivity() {
    private var url: String? = null
    private var progressDialog: AwesomeProgressDialog? = null
    private var effectAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var colorAdapter: EasyRecyclerAdapter<String>? = null
    private var stickerAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    effectAdapter!!.items = data
                }
                2 -> {
                    val data: ArrayList<String> = msg.obj as ArrayList<String>
                    colorAdapter!!.items = data
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
        setContentView(R.layout.activity_pixel)
        EventBus.getDefault().register(this)
        url = intent.getStringExtra("url") as String
        img_main.displayImage(url)
        initData()
        initAdapter()
        initRadiobutton()
    }

    private fun initData() {
        thread {
            val data = PixelUtils.getPixel(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)
        }
        thread {
            val data = this.resources.getStringArray(R.array.color).toList() as ArrayList<String>
            val msg = Message()
            msg.what = 2
            msg.obj = data
            handler.sendMessage(msg)
        }
        thread {
            val data = PixelUtils.getStickers(this)
            val msg = Message()
            msg.what = 3
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    private fun initAdapter() {
        effectAdapter = EasyRecyclerAdapter(this, EffectItem::class.java, ArrayList<Bitmap>())
        recyclerE.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerE.adapter = effectAdapter

        colorAdapter = EasyRecyclerAdapter(this, ColorItem::class.java, ArrayList<String>())
        recyclerC.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerC.adapter = colorAdapter

        stickerAdapter = EasyRecyclerAdapter(this, StickerItem::class.java, ArrayList<Bitmap>())
        recyclerS.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerS.adapter = stickerAdapter
    }

    private fun initRadiobutton() {
        rgPix.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbe -> {
                    recyclerE.visibility = View.VISIBLE
                    recyclerC.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbc -> {
                    recyclerE.visibility = View.GONE
                    recyclerC.visibility = View.VISIBLE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbs -> {
                    recyclerE.visibility = View.GONE
                    recyclerC.visibility = View.GONE
                    recyclerS.visibility = View.VISIBLE
                }
                R.id.rbd -> {
                    progressDialog = CommonUtils.creteProgressDialog(this)
                    progressDialog!!.show()
                    thread {
                        CommonUtils.createBitmapFromView(main)
                    }
                    recyclerE.visibility = View.GONE
                    recyclerC.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "effectItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
            }
            "colorItem" -> {
                val bitmap = Bitmap.createBitmap(
                    ScreenUtils.get().getScreenSize(this)[1] / 2,
                    ScreenUtils.get().getScreenSize(this)[1] / 2,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.parseColor(msg[1] as String))
                val bitmapDrawable = BitmapDrawable(bitmap)
                val stickerModel = StickerModel(bitmapDrawable)
                val sticker = DrawableSticker(stickerModel.drawable)
                stickerView.addSticker(sticker)
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