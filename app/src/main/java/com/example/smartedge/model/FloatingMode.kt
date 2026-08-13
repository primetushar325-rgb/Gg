package com.example.smartedge.model

enum class FloatingMode {
    PIP_SUPPORTED,
    MULTI_WINDOW_POSSIBLE,
    NORMAL_ONLY,
    UNKNOWN;

    fun getDisplayText(): String = when (this) {
        PIP_SUPPORTED -> "Picture-in-Picture Supported"
        MULTI_WINDOW_POSSIBLE -> "Multi-Window / Split-Screen Compatible"
        NORMAL_ONLY -> "Standard Windowing Only"
        UNKNOWN -> "Device Dependent (HiOS Windowing)"
    }
}
