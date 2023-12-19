package com.android.alist.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.android.alist.utils.constant.AppConstant
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 *
 */
@SuppressLint(
    "ModifierFactoryUnreferencedReceiver",
    "ReturnFromAwaitPointerEventScope",
    "MultipleAwaitPointerEventScopes"
)
fun Modifier.dragTheActionTool(
    onDrag: () -> Unit
): Modifier = composed {
    val offsetY = remember { Animatable(0f) }

    pointerInput(Unit) {
        //衰减动画通常在投掷姿势后使用，用于计算投掷动画最后的固定位置
        val decay = splineBasedDecay<Float>(this)

        coroutineScope {
            while (true) {
                //触摸按下点
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                //速度跟踪器
                val velocityTracker = VelocityTracker()

                //等待拖动事件
                awaitPointerEventScope {
                    //监控垂直滑动
                    verticalDrag(pointerId) { change ->
                        if (change.position.y > 0) {
                            val verticalDropOffset = offsetY.value + change.position.y
                            //启动协程，执行动画
                            launch { offsetY.snapTo(verticalDropOffset) }
                            //记录滑动位置
                            velocityTracker.addPosition(change.uptimeMillis, change.position)

                            //消费掉手势，不传递到外面
                            change.consume()
                        }
                    }
                }
                //拖动完成，计算投掷速度
                val velocity = velocityTracker.calculateVelocity().x
                //计算投掷的最终位置，以决定是将元素滑回原始位置，还是将其滑开并调用回调
                val targetOffsetX = decay.calculateTargetValue(offsetY.value, velocity)

                launch {
                    if (offsetY.value.toDp() < 300.dp) {
                        //划回来
                        offsetY.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        onDrag()
                    }
                }
            }
        }
    }
}