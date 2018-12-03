package com.example.marty.shoppinglist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.layout_splash.*

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splash)

        val anim = AnimationUtils.loadAnimation(this@Splash, R.anim.res_anim)

        anim.setAnimationListener (object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                val mainIntent = Intent(this@Splash, MainActivity::class.java)
                this@Splash.startActivity(mainIntent)
                this@Splash.finish()
            }

        })

        logo.startAnimation(anim)
    }
}