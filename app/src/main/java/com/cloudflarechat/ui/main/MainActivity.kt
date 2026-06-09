package com.cloudflarechat.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.cloudflarechat.R
import com.cloudflarechat.data.api.ApiClient
import com.cloudflarechat.databinding.ActivityMainBinding
import com.cloudflarechat.ui.login.LoginActivity
import com.cloudflarechat.ui.settings.SettingsActivity
import com.cloudflarechat.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: PreferencesManager

    private val tabTitles = listOf("对话", "好友", "群聊")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)

        // 恢复 token
        lifecycleScope.launch {
            val token = prefs.token.first()
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return@launch
            }
            ApiClient.token = token
        }

        supportActionBar?.setTitle(R.string.app_name)

        // 左上角齿轮图标 → 设置面板
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_preferences)

        binding.viewPager.adapter = MainPagerAdapter(this)
        binding.viewPager.offscreenPageLimit = 3
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, android.R.color.white),
            ContextCompat.getColor(this, R.color.white)
        )
        binding.tabLayout.setSelectedTabIndicatorColor(
            ContextCompat.getColor(this, R.color.white)
        )
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.menu_logout -> {
                CoroutineScope(Dispatchers.IO).launch {
                    prefs.clearAll()
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MainPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int) = when (position) {
            0 -> ChatsFragment()
            1 -> FriendsFragment()
            2 -> GroupsFragment()
            else -> ChatsFragment()
        }
    }
}