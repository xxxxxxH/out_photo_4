package com.abc.photo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.abc.photo.R
import com.sdsmdg.tastytoast.TastyToast
import jahirfiquitiva.libs.fabsmenu.FABsMenu
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener
import kotlinx.android.synthetic.main.layout_float.*

class PictureAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        to_remove.onClickListener = View.OnClickListener {
            TastyToast.makeText(this@PictureAct,"1",TastyToast.LENGTH_SHORT,TastyToast.INFO)
        }
    }
}