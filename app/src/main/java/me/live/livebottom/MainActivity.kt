package me.live.livebottom

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import me.live.bottomnavigation.LiveBottomnavigation
import me.live.bottomnavigation.OnLiveNavigationItemChangeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LiveBottomNav.setOnNavigationItemChangedListener(object : OnLiveNavigationItemChangeListener {
            override fun onLiveNavigationItemChanged(liveNavigation: LiveBottomnavigation.LiveNavigation) {

            }
        })
    }
}


