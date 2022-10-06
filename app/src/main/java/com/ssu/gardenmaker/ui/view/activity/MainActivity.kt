package com.ssu.gardenmaker.ui.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityMainBinding
import com.ssu.gardenmaker.ui.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), NavigationView.OnNavigationItemSelectedListener {

    private val mViewModel: MainViewModel by viewModels()
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainViewModel = mViewModel

        val toolbar: Toolbar = findViewById(R.id.toolbar) // 툴바를 통해 App Bar 생성
        setSupportActionBar(toolbar) // 툴바 적용

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 드로어를 꺼낼 메뉴 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)   // 메뉴 버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 툴바에 타이틀 안 보이게 함

        drawerLayout = binding.mainDrawerLayout
        navigationView = binding.mainNavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.item_account -> Toast.makeText(this, "내 계정", Toast.LENGTH_SHORT).show()
            R.id.item_add -> Toast.makeText(this, "화단 추가", Toast.LENGTH_SHORT).show()
            R.id.item_edit -> Toast.makeText(this, "화단 편집", Toast.LENGTH_SHORT).show()
            R.id.item_doing -> Toast.makeText(this, "전체 화단", Toast.LENGTH_SHORT).show()
            R.id.item_done -> Toast.makeText(this, "꽃피운 식물들", Toast.LENGTH_SHORT).show()
            R.id.item_setting -> Toast.makeText(this, "환경설정", Toast.LENGTH_SHORT).show()
        }

        return false
    }

    // 뒤로 가기 (드로어 닫음)
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
        else {
            super.onBackPressed()
        }
    }
}