package com.abc.photo.item

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import com.abc.photo.R
import com.abc.photo.utils.MessageEvent
import com.bumptech.glide.Glide
import com.itheima.roundedimageview.RoundedImageView
import org.greenrobot.eventbus.EventBus
import uk.co.ribot.easyadapter.ItemViewHolder
import uk.co.ribot.easyadapter.PositionInfo
import uk.co.ribot.easyadapter.annotations.LayoutId
import uk.co.ribot.easyadapter.annotations.ViewId

@SuppressLint("NonConstantResourceId")
@LayoutId(R.layout.layout_item_bokeh)
class FilterItem(view: View):ItemViewHolder<Bitmap>(view) {
    @ViewId(R.id.itemImage)
    lateinit var itemImage: RoundedImageView
    override fun onSetValues(item: Bitmap?, positionInfo: PositionInfo?) {
        Glide.with(context).load(item).into(itemImage)
        itemImage.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("filterItem",item))
        }
    }
}