package com.abc.photo.utils

import android.widget.ImageView
import com.abc.photo.app.MyApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.lcw.library.imagepicker.utils.ImageLoader

class GlideLoader:ImageLoader {
    private val mOptions = RequestOptions()
        .centerCrop()
        .format(DecodeFormat.PREFER_RGB_565)

    private val mPreOptions = RequestOptions()
        .skipMemoryCache(true)

    override fun loadImage(imageView: ImageView, imagePath: String?) {
        //小图加载
        Glide.with(imageView.context).load(imagePath).apply(mOptions).into(imageView)
    }

    override fun loadPreImage(imageView: ImageView, imagePath: String?) {
        //大图加载
        Glide.with(imageView.context).load(imagePath).apply(mPreOptions).into(imageView)
    }

    override fun clearMemoryCache() {
        //清理缓存
        Glide.get(MyApplication().applicationContext).clearMemory();
    }
}