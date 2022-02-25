package com.abc.photo.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.abc.photo.BuildConfig
import com.abc.photo.R
import com.sdsmdg.tastytoast.TastyToast
import java.io.File

class ShareUtils {
    companion object {
        private var i: ShareUtils? = null
            get() {
                field ?: run {
                    field = ShareUtils()
                }
                return field
            }

        @Synchronized
        fun get(): ShareUtils {
            return i!!
        }
    }

    fun shareWithFb(context: Context, picPath: String) {
        try {
            if (isInstalledApp(context, "com.facebook.katana")) {
                share(context, "com.facebook.katana", picPath)
            } else {
                val appPackageName = "com.facebook.katana"
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=$appPackageName"
                            )
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareWithIns(context: Context, picPath: String) {
        try {
            if (isInstalledApp(context, "com.instagram.android")) {
                share(context, "com.instagram.android", picPath)
            } else {
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.instagram.android")
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.instagram.android")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareWithEmail(context: Context, picPath: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name))
            intent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.fromFile(File(picPath))
            )

            intent.putExtra(
                Intent.EXTRA_TEXT, """Make more pics with app link 
     https://play.google.com/store/apps/details?id=${context.packageName}"""
            )
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Share Picture"))
            } else {
                TastyToast.makeText(
                    context,
                    "Mail app have not been installed",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.WARNING
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareWithNative(context: Context){
        var shareMessage = "Photo Editor" + "" + "\n\nLet me recommend you this application\n\n"
        shareMessage =
            "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name))
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        context.startActivity(Intent.createChooser(shareIntent, "choose one"))
    }

    private fun isInstalledApp(context: Context, uri: String): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun share(context: Context, packageNames: String, path: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.setPackage(packageNames)
        val uri =
            Uri.fromFile(File(path))
        share.putExtra(
            Intent.EXTRA_STREAM,
            uri
        )
        share.putExtra(
            Intent.EXTRA_TEXT, """Make more pics with app link 
                                         https://play.google.com/store/apps/details?id=${context.packageName}"""
        )
        share.type = "image/jpeg"
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(share, "Share Picture"))
    }
}