package com.ssu.gardenmaker.ui.view.activity

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityMainBinding
import com.ssu.gardenmaker.ui.viewmodel.MainViewModel
import com.ssu.gardenmaker.category.CategoryAddDialog
import com.ssu.gardenmaker.category.CategoryListAdapter

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), NavigationView.OnNavigationItemSelectedListener {

    private val mViewModel: MainViewModel by viewModels()

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.mainViewModel = mViewModel
        binding.lifecycleOwner = this

        initNavigationMenu()
        binding.mainToolbar.findViewById<ImageButton>(R.id.Plusplan_btn_main).setOnClickListener{
            var CustomDialog= Dialog(this@MainActivity)
            CustomDialog.setContentView(R.layout.makeplan_dialog)
            CustomDialog.show()

            var checkbox_Check=CustomDialog.findViewById<CheckBox>(R.id.Checkbox_Check)
            var checkbox_Timer=CustomDialog.findViewById<CheckBox>(R.id.Checkbox_Timer)
            var checkbox_Pedometer=CustomDialog.findViewById<CheckBox>(R.id.Checkbox_Pedometer)
            var checkbox_Goalcounter=CustomDialog.findViewById<CheckBox>(R.id.Checkbox_Goalcounter)

            var goalparameter_tv=CustomDialog.findViewById<TextView>(R.id.GoalParmeter_tv_Dialog)
            var goalparameter_btn=CustomDialog.findViewById<Button>(R.id.GoalParameter_btn_Dialog)
            var countparameter_tv=CustomDialog.findViewById<TextView>(R.id.CountParameter_tv_Dialog)
            var countparameter_btn=CustomDialog.findViewById<Button>(R.id.CountParameter_btn_Dialog)

            var goaltimer_tv=CustomDialog.findViewById<TextView>(R.id.GoalTimer_tv_Dialog)
            var goaltimer_btn=CustomDialog.findViewById<Button>(R.id.GoalTimer_btn_Dialog)

            var goalcounter_tv=CustomDialog.findViewById<TextView>(R.id.CountGoalcounter_tv_Dialog)
            var goalcounter_btn=CustomDialog.findViewById<Button>(R.id.CountGoalcounter_btn_Dialog)

            //문제: 모든 checkbox에 대해서 해지가 가능하다.
            checkbox_Check.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    goalparameter_tv.visibility= View.GONE
                    goalparameter_btn.visibility= View.GONE
                    countparameter_tv.visibility= View.GONE
                    countparameter_btn.visibility= View.GONE
                    goaltimer_tv.visibility= View.GONE
                    goaltimer_btn.visibility= View.GONE
                    goalcounter_tv.visibility= View.GONE
                    goalcounter_btn.visibility= View.GONE
                }else{

                }
            }
            checkbox_Timer.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    goalparameter_tv.visibility= View.GONE
                    goalparameter_btn.visibility= View.GONE
                    countparameter_tv.visibility= View.GONE
                    countparameter_btn.visibility= View.GONE
                    goaltimer_tv.visibility= View.VISIBLE
                    goaltimer_btn.visibility= View.VISIBLE
                    goalcounter_tv.visibility= View.GONE
                    goalcounter_btn.visibility= View.GONE
                }else{

                }
            }
            checkbox_Pedometer.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    goalparameter_tv.visibility= View.VISIBLE
                    goalparameter_btn.visibility= View.VISIBLE
                    countparameter_tv.visibility= View.VISIBLE
                    countparameter_btn.visibility= View.VISIBLE
                    goaltimer_tv.visibility= View.GONE
                    goaltimer_btn.visibility= View.GONE
                    goalcounter_tv.visibility= View.GONE
                    goalcounter_btn.visibility= View.GONE
                }else{

                }
            }
            checkbox_Goalcounter.setOnCheckedChangeListener{ view, isChecked ->
                if(isChecked){
                    goalparameter_tv.visibility= View.GONE
                    goalparameter_btn.visibility= View.GONE
                    countparameter_tv.visibility= View.GONE
                    countparameter_btn.visibility= View.GONE
                    goaltimer_tv.visibility= View.GONE
                    goaltimer_btn.visibility= View.GONE
                    goalcounter_tv.visibility= View.VISIBLE
                    goalcounter_btn.visibility= View.VISIBLE
                }else{

                }
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
        val parentList = mutableListOf("전체 카테고리")
        val childList = mutableListOf(binding.mainViewModel!!.showCategory())

        val categoryList = binding.mainNaviListview.findViewById<ExpandableListView>(R.id.main_navi_listview)
        val categoryListAdapter = CategoryListAdapter(this, parentList, childList)  // List Adapter 초기화
        categoryList.setAdapter(categoryListAdapter)    // List에 Adapter 연결

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
            val categoryAddDialog = CategoryAddDialog(this)
            categoryAddDialog.showDialog()
            categoryAddDialog.setOnClickListener(object : CategoryAddDialog.OnDialogClickListener {
                override fun onClicked(name: String) {
                    binding.mainViewModel?.addCategory(name, categoryListAdapter)
                }
            })
        }

        // 카테고리 편집
        val btnEditCategory = binding.mainNaviHeader.findViewById<TextView>(R.id.btn_edit_category) // 카테고리 편집
        btnEditCategory.setOnClickListener {  }
    }
}