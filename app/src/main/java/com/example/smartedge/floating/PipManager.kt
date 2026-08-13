package com.example.smartedge.floating

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational

object PipManager {

    fun enterPipMode(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
            activity.enterPictureInPictureMode(builder.build())
        }
    }
}
