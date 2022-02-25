package com.abc.photo.item

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.RelativeLayout
import com.abc.photo.R
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.ScreenUtils
import me.panpf.sketch.SketchImageView
import org.greenrobot.eventbus.EventBus
import uk.co.ribot.easyadapter.ItemViewHolder
import uk.co.ribot.easyadapter.PositionInfo
import uk.co.ribot.easyadapter.annotations.LayoutId
import uk.co.ribot.easyadapter.annotations.ViewId

@SuppressLint("NonConstantResourceId")
@LayoutId(R.layout.layout_pic_item)
class PictureItem(view: View) : ItemViewHolder<String>(view) {
    @ViewId(R.id.item)
    lateinit var iv: SketchImageView
    @ViewId(R.id.itemRoot)
    lateinit var root:RelativeLayout
    override fun onSetValues(item: String?, positionInfo: PositionInfo?) {
        item?.let {
            iv.displayImage(it)
            iv.layoutParams.apply {
                this!!.width = ScreenUtils.get().getScreenSize(context as Activity)[1] / 3
                height = ScreenUtils.get().getScreenSize(context as Activity)[1] / 3
            }
            root.layoutParams.apply {
                width = ScreenUtils.get().getScreenSize(context as Activity)[1] / 3
                height = ScreenUtils.get().getScreenSize(context as Activity)[1] / 3
            }
            iv.apply {
                this.setOnClickListener {
                    EventBus.getDefault().post(MessageEvent("picItem",item))
                }
            }
        }

    }
}