package com.example.contactapp.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contactapp.R
import com.example.contactapp.databinding.ActivityMainBinding
import com.example.contactapp.function.FragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewInit()
    }

    private fun viewInit(){
        val tabLayout = binding.tlTabs
        val viewPager = binding.vpViewpager
        viewPager.adapter = FragmentViewPagerAdapter(this)

        // tab layout 과 view pager2 연동하는 코드입니다.
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setText(R.string.tab_left)
                1 -> tab.setText(R.string.tab_right)
            }
        }.attach()

    }
}