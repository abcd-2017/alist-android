package com.android.alist.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity

class VibratorHelper {
    private var vibrator: Vibrator? = null

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VibratorHelper()
        }
    }

    @SuppressLint("ServiceCast")
    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            this.vibrator = vibratorManager.defaultVibrator
        } else {
            this.vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun init(vibrator: Vibrator) {
        this.vibrator = vibrator
    }

    fun cancel() {
        vibrator?.cancel()
    }

    fun hasVibrator(): Boolean {
        return vibrator?.hasVibrator() == true
    }

    fun hasAmplitudeControl(): Boolean {
        return vibrator?.hasAmplitudeControl() == true
    }

    fun vibrate(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat)
        vibrator?.vibrate(vibrationEffect)
    }

    fun vibrateOneShot(milliseconds: Long, amplitude: Int) {
        val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
        vibrator?.vibrate(vibrationEffect)
    }

    fun vibratePredefined(predefined: Int) {
        val vibrationEffect = VibrationEffect.createPredefined(predefined)
        vibrator?.vibrate(vibrationEffect)
    }

    /**
     * 计算错误时的振动效果
     * */
    fun vibrateOnError() {
        val timings = longArrayOf(
            10,
            180,
            10,
            90,
            4,
            90,
            7,
            80,
            2,
            120,
            4,
            50,
            2,
            40,
            1,
            40,
            4,
            50,
            2,
            40,
            1,
            40,
            4,
            50,
            2,
            40,
            1,
            40
        )
        val amplitudes = intArrayOf(
            255,
            0,
            255,
            0,
            240,
            0,
            240,
            0,
            240,
            0,
            230,
            0,
            230,
            0,
            230,
            0,
            220,
            0,
            220,
            0,
            220,
            0,
            210,
            0,
            210,
            0,
            210,
            0
        )
        instance.vibrate(timings, amplitudes, -1)
    }

    /**
     * 清除时的振动效果
     * */
    fun vibrateOnClear() {
        val timings = longArrayOf(10, 180, 10, 90, 4, 90, 7, 80, 2, 120)
        val amplitudes = intArrayOf(255, 0, 255, 0, 240, 0, 240, 0, 240, 0)
        instance.vibrate(timings, amplitudes, -1)
    }

    /**
     * 长按震动
     * */
    fun vibrateOnLongPress() {
        instance.vibrateOneShot(60, 255)
    }

    /**
     * 按下按键时的振动效果
     * */
    fun vibrateOnClick() {
        instance.vibrateOneShot(20, 255)
    }
}