package com.example.smartedge.floating

import android.os.Build

object TecnoFloatingWindowHelper {

    fun isTecnoDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return manufacturer.contains("tecno") || brand.contains("tecno") ||
                manufacturer.contains("infinix") || brand.contains("infinix")
    }

    fun getOEMDisclaimer(): String {
        return if (isTecnoDevice()) {
            "Running on HiOS / TECNO. Multi-window floating app capabilities rely on standard Android launcher intents and native OS multi-window options."
        } else {
            "Standard Android Device Detected."
        }
    }
}
