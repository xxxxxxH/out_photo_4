package com.abc.photo.item

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import com.abc.photo.R
import com.abc.photo.utils.MessageEvent
import com.abc.photo.utils.ScreenUtils
import com.itheima.roundedimageview.RoundedImageView
import org.greenrobot.eventbus.EventBus
import uk.co.ribot.easyadapter.ItemViewHolder
import uk.co.ribot.easyadapter.PositionInfo
import uk.co.ribot.easyadapter.annotations.LayoutId
import uk.co.ribot.easyadapter.annotations.ViewId

@SuppressLint("NonConstantResourceId")
@LayoutId(R.layout.layout_item_bokeh)
class ColorItem(view: View) : ItemViewHolder<String>(view) {
    @ViewId(R.id.itemImage)
    lateinit var itemImage: RoundedImageView
    override fun onSetValues(item: String?, positionInfo: PositionInfo?) {
        itemImage.layoutParams.apply {
            width = ScreenUtils.get().getScreenSize(context as Activity)[1] / 6
            height = ScreenUtils.get().getScreenSize(context as Activity)[1] / 6
        }
        itemImage.setBackgroundColor(Color.parseColor(item))
        itemImage.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("colorItem", item))
        }
    }
}