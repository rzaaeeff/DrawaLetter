package com.rzaaeeff.drawaletter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rzaaeeff.drawaletter.fragment.DrawFragment
import com.rzaaeeff.drawaletter.fragment.MainFragment

class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainFragment())
                .commit()
    }

    fun openDrawFragment(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DrawFragment())
                .addToBackStack(null)
                .commit()
    }
}
