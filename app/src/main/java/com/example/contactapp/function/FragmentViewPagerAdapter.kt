package com.example.contactapp.function

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contactapp.presentation.ContactListFragment
import com.example.contactapp.presentation.MyPageFragment

class FragmentViewPagerAdapter(activity:FragmentActivity):FragmentStateAdapter(activity) {

    private val fragments: List<Fragment> =listOf(ContactListFragment(), MyPageFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}