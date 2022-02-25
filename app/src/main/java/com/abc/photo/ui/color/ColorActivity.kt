package com.abc.photo.ui.color

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abc.photo.R
import com.abc.photo.item.EffectItem
import com.abc.photo.item.FilterItem
import com.abc.photo.item.StickerItem
import com.abc.photo.ui.PictureAct
import com.abc.photo.utils.CommonUtils
import com.abc.photo.utils.GUPUtil
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.ScreenUtils
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.sdsmdg.tastytoast.TastyToast
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.android.synthetic.main.activity_color.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter
import kotlin.concurrent.thread

class ColorActivity : AppCompatActivity() {

    var gpuImage: GPUImage? = null
    private var url: String? = null
    private var effectAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var filterAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var stickerAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var progressDialog: AwesomeProgressDialog? = null
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
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    filterAdapter!!.items = data
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
        setContentView(R.layout.activity_color)
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
            val data = ColorUtils.getEffect(this)
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
            val data = ColorUtils.getStickers(this)
            val msg = Message()
            msg.what = 3
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    private fun initAdapter() {
        effectAdapter =
            EasyRecyclerAdapter<Bitmap>(this, EffectItem::class.java, ArrayList<Bitmap>())
        recyclerE.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerE.adapter = effectAdapter

        filterAdapter = EasyRecyclerAdapter(this, FilterItem::class.java, ArrayList<Bitmap>())
        recyclerF.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerF.adapter = filterAdapter

        stickerAdapter = EasyRecyclerAdapter(this, StickerItem::class.java, ArrayList<Bitmap>())
        recyclerS.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerS.adapter = stickerAdapter
    }

    private fun initRadiobutton() {
        rgColor.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbe -> {
                    recyclerE.visibility = View.VISIBLE
                    recyclerF.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbf -> {
                    recyclerE.visibility = View.GONE
                    recyclerF.visibility = View.VISIBLE
                    recyclerS.visibility = View.GONE
                }
                R.id.rbs -> {
                    recyclerE.visibility = View.GONE
                    recyclerF.visibility = View.GONE
                    recyclerS.visibility = View.VISIBLE
                }
                R.id.rbd -> {
                    progressDialog = CommonUtils.creteProgressDialog(this)
                    progressDialog!!.show()
                    thread {
                        CommonUtils.createBitmapFromView(main)
                    }
                    recyclerE.visibility = View.GONE
                    recyclerF.visibility = View.GONE
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
                val iv = ImageView(this)
                val p = RelativeLayout.LayoutParams(
                    ScreenUtils.get().getScreenSize(this)[1] / 2,
                    ScreenUtils.get().getScreenSize(this)[1] / 2
                )
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                iv.layoutParams = p
                iv.setImageBitmap(msg[1] as Bitmap)
                main.addView(iv)
                main.invalidate()
            }
            "filterItem" -> {
                img_main.invalidate()
                img_main.setImageBitmap(msg[1] as Bitmap)
            }
            "stickerItem" -> {
                val iv = ImageView(this)
                val p = RelativeLayout.LayoutParams(
                    ScreenUtils.get().getScreenSize(this)[1] / 2,
                    ScreenUtils.get().getScreenSize(this)[1] / 2
                )
                p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                iv.layoutParams = p
                iv.setImageBitmap(msg[1] as Bitmap)
                main.addView(iv)
                main.invalidate()
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