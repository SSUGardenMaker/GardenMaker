package com.ssu.gardenmaker.ui.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityMainBinding
import com.ssu.gardenmaker.ui.viewmodel.MainViewModel
import com.ssu.gardenmaker.category.CategoryAddDialog
import com.ssu.gardenmaker.category.CategoryEditDialog
import com.ssu.gardenmaker.category.CategoryExpandableListAdapter

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), NavigationView.OnNavigationItemSelectedListener {

    private val mViewModel: MainViewModel by viewModels()

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.mainViewModel = mViewModel
        binding.lifecycleOwner = this

        initNavigationMenu()
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

    // 네비게이션 메뉴를 초기화
    private fun initNavigationMenu() {
        drawerLayout = binding.mainDrawerLayout
        navigationView = binding.mainNavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val navigationMenu = binding.mainToolbar.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(navigationMenu) // 툴바 적용

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 드로어를 꺼낼 메뉴 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)   // 메뉴 버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 툴바에 타이틀 안 보이게 함

        initCategoryList()
    }

    // 네비게이션 카테고리 리스트를 초기화
    private fun initCategoryList() {
        val parentList = mutableListOf("전체 화단")
        val childList = mutableListOf(binding.mainViewModel!!.showCategory())

        val categoryList = binding.mainNaviListview.findViewById<ExpandableListView>(R.id.main_navi_listview)
        val categoryExpandableListAdapter = CategoryExpandableListAdapter(this, parentList, childList)  // List Adapter 초기화
        categoryList.setAdapter(categoryExpandableListAdapter)    // List에 Adapter 연결

        // 부모 아이템 클릭 이벤트
        categoryList.setOnGroupClickListener { parent, v, groupPosition, id ->
            false
        }

        // 자식 아이템 클릭 이벤트
        categoryList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            false
        }

        // 카테고리 추가
        val btnAddCategory = binding.mainNaviHeader.findViewById<TextView>(R.id.btn_add_category)
        btnAddCategory.setOnClickListener {
            if (categoryExpandableListAdapter.getChildrenCount(0) < 4) {
                val categoryAddDialog = CategoryAddDialog(this)
                categoryAddDialog.showDialog()
                categoryAddDialog.setOnClickListener(object : CategoryAddDialog.CategoryAddDialogClickListener {
                    override fun onClicked(name: String) {
                        binding.mainViewModel?.addCategory(name, categoryExpandableListAdapter)
                    }
                })
            }
            else {
                Toast.makeText(this@MainActivity, "화단은 최대 4개까지 만들 수 있습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 카테고리 편집
        val btnEditCategory = binding.mainNaviHeader.findViewById<TextView>(R.id.btn_edit_category)
        btnEditCategory.setOnClickListener {
            val categoryEditDialog = CategoryEditDialog(this, binding.mainViewModel!!.showCategory())
            categoryEditDialog.showDialog()
            categoryList.collapseGroup(0)
        }
    }
}