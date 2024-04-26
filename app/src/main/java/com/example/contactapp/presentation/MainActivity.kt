package com.example.contactapp.presentation

import android.os.Bundle
import android.view.View
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.contactapp.R
import com.example.contactapp.data.DataSource
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

        val tabLayout = binding.tlTabs
        val viewPager = binding.vpViewpager
        viewPager.adapter = FragmentViewPagerAdapter(this)

        // tab layout 과 view pager2 연동하는 코드입니다.
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
//                0 -> tab.text = R.string.tab_left.toString() // 전부 대문자로 하는거 맞나요?
//                1 -> tab.text = R.string.tab_right.toString() // string 데이터 이렇게 가져오는게 맞나요?
                0 -> tab.text = "CONTACT LIST" // 값이 제대로 안가져와서 일단 이렇게 만들었어요.
                1 -> tab.text = "MY PAGE"
            }
        }.attach()


    }

    fun hideTabLayout() {
        binding.tlTabs.visibility = View.GONE
    }

    fun showTabLayout() {
        binding.tlTabs.visibility = View.VISIBLE

        val dataSource = DataSource.getInstance()
        var myContact = dataSource.myContact

        binding.fab.setOnClickListener {
            val addDialog = AddContact(-5)
            addDialog.setOnDialogDismissListener(object : AddContact.OnDialogDismissListener {
                override fun onDialogDismissed() {
                    Log.d("contact", "$myContact")
                    val fragment = FragmentViewPagerAdapter(this@MainActivity)
                    fragment.refreshListFragment()
                }
            })
            addDialog.show(supportFragmentManager, AddContact.TAG)

        }
    }
}