package me.live.bottomnavigation

import android.support.annotation.ColorRes
import android.view.Menu

class LiveParams {
    var menu: Menu? = null
    @ColorRes
    var activeColor: Int = R.color.LiveBottomNavigationActiveColor
    @ColorRes
    var passiveColor: Int = R.color.LiveBottomNavigationPassiveColor
    @ColorRes
    var pressedColor: Int = R.color.LiveBottomNavigationPressedColor
    var itemPadding: Float = 16f
    var itemTextSize: Float = 40f
    var animationDuraction: Int = 300
    var endScale: Float = 0.95f
    var startScale: Float = 1f

}