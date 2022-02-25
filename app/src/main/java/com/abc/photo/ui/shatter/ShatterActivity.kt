package com.abc.photo.ui.shatter

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
import com.abc.photo.item.FrameItem
import com.abc.photo.item.StickerItem
import com.abc.photo.ui.PictureAct
import com.abc.photo.utils.CommonUtils
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.StickerModel
import com.abc.photo.widget.Filter
import com.abc.photo.widget.Shatter
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.sdsmdg.tastytoast.TastyToast
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.activity_bokeh.*
import kotlinx.android.synthetic.main.activity_shatter.*
import kotlinx.android.synthetic.main.activity_shatter.img_main
import kotlinx.android.synthetic.main.activity_shatter.main
import kotlinx.android.synthetic.main.activity_shatter.stickerView
import kotlinx.android.synthetic.main.layout_tools.*
import net.widget.DrawableSticker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import uk.co.ribot.easyadapter.EasyRecyclerAdapter
import kotlin.concurrent.thread

class ShatterActivity : AppCompatActivity() {
    private var url: String? = null
    private var progressDialog: AwesomeProgressDialog? = null
    var funRotate = true
    var funDRotate = true
    private var frameAdapter: EasyRecyclerAdapter<Bitmap>? = null
    private var stickerAdapter: EasyRecyclerAdapter<Bitmap>? = null
    val filter = Shatter()
    var completeBitmap: Bitmap? = null
    private var processOfCount = 0
    private var processOfX = 0
    var bitmap: Bitmap? = null

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    frameAdapter!!.items = data
                }
                2 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    stickerAdapter!!.items = data
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shatter)
        EventBus.getDefault().register(this)
        url = intent.getStringExtra("url") as String
        bitmap = BitmapFactory.decodeFile(url)
        img_main.displayImage(url)
        initData()
        initAdapter()
        initRadiobutton()
        initTools()
    }

    private fun initData() {
        thread {
            val data = ShatterUtils.getShatter(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)
        }

        thread {
            val data = ShatterUtils.getStickers(this)
            val msg = Message()
            msg.what = 2
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    private fun initAdapter() {
        frameAdapter = EasyRecyclerAdapter(this, FrameItem::class.java, ArrayList<Bitmap>())
        recyclerF.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerF.adapter = frameAdapter

        stickerAdapter = EasyRecyclerAdapter(this, StickerItem::class.java, ArrayList<Bitmap>())
        recyclerS.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerS.adapter = stickerAdapter
    }

    private fun initRadiobutton() {
        rgShatter.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbt -> {
                    recyclerF.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                    tools.visibility = View.VISIBLE
                }
                R.id.rbf -> {
                    recyclerF.visibility = View.VISIBLE
                    recyclerS.visibility = View.GONE
                    tools.visibility = View.GONE
                }
                R.id.rbs -> {
                    recyclerF.visibility = View.GONE
                    recyclerS.visibility = View.VISIBLE
                    tools.visibility = View.GONE
                }
                R.id.rbd -> {
                    progressDialog = CommonUtils.creteProgressDialog(this)
                    progressDialog!!.show()
                    thread {
                        CommonUtils.createBitmapFromView(main)
                    }
                    recyclerF.visibility = View.GONE
                    recyclerS.visibility = View.GONE
                    tools.visibility = View.GONE
                }
            }
        }
    }

    private fun initTools() {
        rotateLayout.setOnClickListener {
            if (funRotate) {
                img_rotate.setImageResource(R.mipmap.box)
                funRotate = false
                filter.boolPar[0] =
                    Filter.BoolParameter("Rotate Blocks", java.lang.Boolean.FALSE)
                changeStyleAsyncTask()
            } else {
                funRotate = true
                img_rotate.setImageResource(R.mipmap.rotate)
                filter.boolPar[0] =
                    Filter.BoolParameter("Rotate Blocks", java.lang.Boolean.TRUE)
                changeStyleAsyncTask()
            }
        }
        threed_rotateLayout.setOnClickListener {
            if (funDRotate) {
                img_threerotate.setImageResource(R.mipmap.box)
                funDRotate = false
                filter.boolPar[1] =
                    Filter.BoolParameter("Shattered Blocks", java.lang.Boolean.FALSE)
                changeStyleAsyncTask()
            } else {
                funDRotate = true
                img_threerotate.setImageResource(R.mipmap.rotate)
                filter.boolPar[1] = Filter.BoolParameter("Shattered Blocks", java.lang.Boolean.TRUE)
                changeStyleAsyncTask()
            }
        }
        findViewById<IndicatorSeekBar>(R.id.seekbarcount).onSeekChangeListener =
            object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    processOfCount = seekParams.progress
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                    filter.intPar[0] = Filter.IntParameter("Count", processOfCount, 2, 100)
                    changeStyleAsyncTask()
                }
            }
        findViewById<IndicatorSeekBar>(R.id.seekbarx).onSeekChangeListener =
            object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    processOfX = seekParams.progress
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                    filter.intPar[1] = Filter.IntParameter("X", "%", processOfX, 0, 100)
                    changeStyleAsyncTask()
                }
            }
    }

    private fun changeStyleAsyncTask() {
        Thread(Runnable {
            runOnUiThread {
                completeBitmap = filter.Apply(bitmap)

                img_main.setImageBitmap(completeBitmap)
            }

        }).start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "frameItem" -> {
                val bitmapDrawable = BitmapDrawable(msg[1] as Bitmap)
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