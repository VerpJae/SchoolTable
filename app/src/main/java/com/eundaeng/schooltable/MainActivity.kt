package com.eundaeng.schooltable

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.util.*

class MainActivity : AppCompatActivity() {
    init{
        instance = this
    }

    companion object {
        lateinit var instance: MainActivity
        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
        lateinit var prefs: PreferenceUtil
    }
    private var fragmentManager: FragmentManager = supportFragmentManager
    //private var fragmentA: WVFragment = WVFragment()
    private var fragmentA: AFragment = AFragment()
    private var fragmentB: BFragment = BFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = PagerAdapter(fragmentManager)
        adapter.addFragment(fragmentA, "시간표")
        adapter.addFragment(fragmentB, "급식표")
        viewpager.adapter = adapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                println(tab?.position)
            }
        })
    }
}