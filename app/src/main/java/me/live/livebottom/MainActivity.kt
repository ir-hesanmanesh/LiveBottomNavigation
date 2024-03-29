package me.live.livebottom

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.live.bottomnavigation.LiveBottomnavigation
import me.live.bottomnavigation.OnLiveNavigationItemChangeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // val arrayList = arrayListOf(0,1,2)
       // var i =1
        LiveBottomNav.setOnNavigationItemChangedListener(object : OnLiveNavigationItemChangeListener {
            override fun onLiveNavigationItemChanged(liveNavigation: LiveBottomnavigation.LiveNavigation) {
                Toast.makeText(this@MainActivity,
                    "Selected item at index ${liveNavigation.position}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }



}


