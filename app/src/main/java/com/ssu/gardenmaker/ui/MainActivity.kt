package com.ssu.gardenmaker.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.CalendarAndCheckList.CalendarDialog
import com.ssu.gardenmaker.CalendarAndCheckList.CheckboxDialog
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityMainBinding
import com.ssu.gardenmaker.category.CategoryAddDialog
import com.ssu.gardenmaker.category.CategoryEditDialog
import com.ssu.gardenmaker.category.CategoryExpandableListAdapter
import com.ssu.gardenmaker.plant.PlantCreateDialog
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var backKeyPressedTime: Long = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView: NavigationView

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtonFunction()
        initNavigationMenu()

        binding.mainLayout.btnPlusPlan.setOnClickListener {
            PlantCreateDialog(this@MainActivity, layoutInflater).showDialog()
        }
    }

    // 화면 터치 시 키보드 내려감
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 2초 이내에 뒤로가기 버튼을 한번 더 클릭시 앱 종료 / 드로어가 열려 있는 상태에서 뒤로가기 버튼을 누르면 드로어가 닫힘
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
        else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                moveTaskToBack(true) // 태스크를 백그라운드로 이동
                finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
                exitProcess(0)
            }
        }
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

    // 버튼 기능 구현
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initButtonFunction() {
        binding.mainLayout.mainLayoutToolbar.findViewById<ImageButton>(R.id.ib_toolbar_checklist).setOnClickListener {
            CheckboxDialog(this@MainActivity,applicationContext,layoutInflater).showDialog()
        }

        binding.mainLayout.mainLayoutToolbar.findViewById<ImageButton>(R.id.ib_toolbar_calendar).setOnClickListener {
            CalendarDialog(this@MainActivity,applicationContext,layoutInflater).showDialog()
        }
    }

    // 네비게이션 메뉴를 초기화
    private fun initNavigationMenu() {
        drawerLayout = binding.mainDrawerLayout
        navigationView = binding.mainNavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val navigationMenu = binding.mainLayout.mainLayoutToolbar.findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(navigationMenu) // 툴바 적용

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // 드로어를 꺼낼 메뉴 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)   // 메뉴 버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false)   // 툴바에 타이틀 안 보이게 함

        initCategoryList()
    }

    // 네비게이션 화단 리스트를 초기화
    private fun initCategoryList() {
        getCategoryList()

        val parentList = mutableListOf("전체 화단")
        val childList = mutableListOf(ApplicationClass.categoryLists)

        val categoryList = binding.mainNaviListview.findViewById<ExpandableListView>(R.id.main_navi_listview)
        val categoryExpandableListAdapter = CategoryExpandableListAdapter(this, parentList, childList)  // List Adapter 초기화
        categoryList.setAdapter(categoryExpandableListAdapter)    // List에 Adapter 연결

        // 부모 아이템 클릭 이벤트
        categoryList.setOnGroupClickListener { parent, v, groupPosition, id ->
            false
        }

        // 자식 아이템 클릭 이벤트
        categoryList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val intent = Intent(this, GardenActivity::class.java)
            intent.putExtra("NAME", ApplicationClass.categoryLists[childPosition])
            startActivity(intent)
            false
        }

        // 화단 추가
        binding.mainNaviHeader.btnAddCategory.setOnClickListener {
            if (categoryExpandableListAdapter.getChildrenCount(0) < 4) {
                val categoryAddDialog = CategoryAddDialog(this)
                categoryAddDialog.showDialog()
                categoryAddDialog.setOnClickListener(object : CategoryAddDialog.CategoryAddDialogClickListener {
                    override fun onClicked(name: String) {
                        ApplicationClass.categoryLists.add(name)
                        categoryExpandableListAdapter.notifyDataSetChanged()
                    }
                })
            }
            else {
                Toast.makeText(this@MainActivity, "화단은 최대 4개까지 만들 수 있습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 화단 편집
        binding.mainNaviHeader.btnEditCategory.setOnClickListener {
            val categoryEditDialog = CategoryEditDialog(this, ApplicationClass.categoryLists)
            categoryEditDialog.showDialog()
            categoryList.collapseGroup(0)
        }
    }

    // 사용자가 설정해놓은 카테고리 리스트 가져오기
    private fun getCategoryList() {
        ApplicationClass.categoryLists.add("건강")
        ApplicationClass.categoryLists.add("학업")
        ApplicationClass.categoryLists.add("저축")
    }
}