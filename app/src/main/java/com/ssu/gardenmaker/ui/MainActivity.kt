package com.ssu.gardenmaker.ui

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.calendar.CalendarDialog
import com.ssu.gardenmaker.category.CategoryAddDialog
import com.ssu.gardenmaker.category.CategoryEditDialog
import com.ssu.gardenmaker.category.CategoryExpandableListAdapter
import com.ssu.gardenmaker.databinding.ActivityMainBinding
import com.ssu.gardenmaker.features.pedometer.PedometerService
import com.ssu.gardenmaker.features.recursiveTimer.recursiveTimerService
import com.ssu.gardenmaker.plant.PlantCreateDialog
import com.ssu.gardenmaker.plant.PlantPlacingDialog
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.callback.RetrofitGardenCallback
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.garden.GardenDataContent
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent
import com.ssu.gardenmaker.runnable.CalendarRunnable
import com.ssu.gardenmaker.util.SharedPreferenceManager
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var animation : Animation
    private val TAG = "MainActivity"
    private var backKeyPressedTime: Long = 0

    @RequiresApi(VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate)

        initPlantPlacing()
        initButtonFunction()
        initNavigationMenu()
        binding.mainLayout.scrollViewMain.post{
            binding.mainLayout.scrollViewMain.scrollTo(400, 0)
        }
       // startPedometer()

        // 만보기 권한요청
        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            var permissions = arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
            requestPermissions(arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),0)
        }

        // 뷰 중앙으로 이동
        binding.mainLayout.scrollViewMain.post{
            val mid = binding.mainLayout.scrollViewMain.width / ((this.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)
            binding.mainLayout.scrollViewMain.scrollTo(mid.toInt(), 0)
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
    @RequiresApi(VERSION_CODES.P)
    private fun initButtonFunction() {
        // 캘린더 기능
        binding.mainLayout.mainLayoutToolbar.findViewById<ImageButton>(R.id.ib_toolbar_calendar).setOnClickListener {
            var gardenitemList=ArrayList<CalendarDialog.Garden_item>()

            var handlerCount=0
            var handler:Handler=object : Handler(){
                override fun handleMessage(msg: Message) {
                   if(msg.arg1==100)
                       handlerCount++

                    if(handlerCount>=ApplicationClass.categoryLists.size)
                        CalendarDialog(this@MainActivity, applicationContext, layoutInflater,gardenitemList).showDialog()

                }
            }
            for(i in 0..ApplicationClass.categoryLists.size-1){
                var gardenData=ApplicationClass.categoryLists.get(i)
                gardenitemList.add(CalendarDialog.Garden_item(gardenData.id,gardenData.name,ArrayList<PlantDataContent>()))

                var Runnable=CalendarRunnable(gardenData.id,gardenitemList.get(i).plants,handler)
                var thread=Thread(Runnable)
                thread.start()
            }
        }

        // 식물 배치 ON/OFF
        binding.mainLayout.btnActivatePlacing.setOnClickListener {
            if (SharedPreferenceManager().getString("plantPlacing").equals("OFF")) {
                SharedPreferenceManager().setString("plantPlacing", "ON")
                val userEmail = SharedPreferenceManager().getString("email")
                if (SharedPreferenceManager().getInt(userEmail+"Place1") == -1) {
                    binding.mainLayout.plantPlacing1.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing1.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place2") == -1) {
                    binding.mainLayout.plantPlacing2.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing2.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place3") == -1) {
                    binding.mainLayout.plantPlacing3.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing3.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place4") == -1) {
                    binding.mainLayout.plantPlacing4.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing4.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place5") == -1) {
                    binding.mainLayout.plantPlacing5.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing5.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place6") == -1) {
                    binding.mainLayout.plantPlacing6.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing6.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place7") == -1) {
                    binding.mainLayout.plantPlacing7.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing7.startAnimation(animation)
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place8") == -1) {
                    binding.mainLayout.plantPlacing8.visibility = View.VISIBLE
                    binding.mainLayout.plantPlacing8.startAnimation(animation)

                }
            }
            else {
                SharedPreferenceManager().setString("plantPlacing", "OFF")
                val userEmail = SharedPreferenceManager().getString("email")
                if (SharedPreferenceManager().getInt(userEmail+"Place1") == -1) {
                    binding.mainLayout.plantPlacing1.clearAnimation()
                    binding.mainLayout.plantPlacing1.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place2") == -1) {
                    binding.mainLayout.plantPlacing2.clearAnimation()
                    binding.mainLayout.plantPlacing2.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place3") == -1) {
                    binding.mainLayout.plantPlacing3.clearAnimation()
                    binding.mainLayout.plantPlacing3.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place4") == -1) {
                    binding.mainLayout.plantPlacing4.clearAnimation()
                    binding.mainLayout.plantPlacing4.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place5") == -1) {
                    binding.mainLayout.plantPlacing5.clearAnimation()
                    binding.mainLayout.plantPlacing5.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place6") == -1) {
                    binding.mainLayout.plantPlacing6.clearAnimation()
                    binding.mainLayout.plantPlacing6.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place7") == -1) {
                    binding.mainLayout.plantPlacing7.clearAnimation()
                    binding.mainLayout.plantPlacing7.visibility = View.INVISIBLE
                }
                if (SharedPreferenceManager().getInt(userEmail+"Place8") == -1) {
                    binding.mainLayout.plantPlacing8.clearAnimation()
                    binding.mainLayout.plantPlacing8.visibility = View.INVISIBLE
                }
            }
        }

        // 식물 추가 다이얼로그
        binding.mainLayout.btnPlusPlan.setOnClickListener {
            val plantCreateDialog = PlantCreateDialog(this@MainActivity, layoutInflater)
            plantCreateDialog.showDialog()
        }

        // 꽃피운 식물들
        binding.categoryDoneNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlantDoneActivity::class.java))
        }

        // 식물 도감
        binding.plantBookNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlantBookActivity::class.java))
        }

        // 비밀번호 변경
        binding.navigationChangePasswordNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChangePasswordActivity::class.java))
        }

        // 로그아웃
        binding.navigationLogoutNext.setOnClickListener {
            SharedPreferenceManager().deleteData("email")
            SharedPreferenceManager().deleteData("password")
            SharedPreferenceManager().deleteData("accessToken")
            ApplicationClass.categoryLists.clear()
            ApplicationClass.plantDoneLists.clear()

            finish()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
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
            intent.putExtra("NAME", ApplicationClass.categoryLists[childPosition].name)
            intent.putExtra("ID", ApplicationClass.categoryLists[childPosition].id)
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
                        ApplicationClass.retrofitManager.gardenCreate(name, name, object : RetrofitCallback {
                            override fun onError(t: Throwable) {
                                Log.d(TAG, "onError : " + t.localizedMessage)
                            }

                            override fun onSuccess(message: String, data: String) {
                                Log.d(TAG, "onSuccess : message -> $message")
                                Log.d(TAG, "onSuccess : data -> $data")
                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()

                                val newGarden = GardenDataContent(category = name, id = Integer.parseInt(data), name = name)
                                ApplicationClass.categoryLists.add(newGarden)
                                categoryExpandableListAdapter.notifyDataSetChanged()
                            }

                            override fun onFailure(errorMessage: String, errorCode: Int) {
                                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        })
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
            categoryEditDialog.setOnClickListener(object: CategoryEditDialog.CategoryEditDialogClickListener {
                override fun onClicked() {
                    categoryList.collapseGroup(0)
                }
            })
        }
    }

    // 카테고리 리스트 가져오기
    private fun getCategoryList() {
        ApplicationClass.retrofitManager.gardenCheck(object : RetrofitGardenCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: List<GardenDataContent>) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")

                ApplicationClass.categoryLists.addAll(data)
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //만보기 실행시키기. 만보기 있는지 체크하는 API가 만들면 그때 추가하기.
    private fun startPedometer(){
            if(SharedPreferenceManager().getString("pedometerHave")=="YES"){
                val intent= Intent(applicationContext, PedometerService::class.java)
                startForegroundService(intent)
            }
    }

    // 식물 배치 기능 구현
    private fun initPlantPlacing() {
        // 기존에 식물 배치한 대로 초기 설정 + 없다면 식물 배치 공간에 애니메이션 설정
        SharedPreferenceManager().setString("plantPlacing", "OFF")
        val userEmail = SharedPreferenceManager().getString("email")
        if (SharedPreferenceManager().getInt(userEmail+"Place1") != -1) {
            binding.mainLayout.plantPlacing1.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing1.setImageResource(SharedPreferenceManager().getInt(userEmail + "Place1"))
        }
        else {
            binding.mainLayout.plantPlacing1.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place2") != -1) {
            binding.mainLayout.plantPlacing2.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing2.setImageResource(SharedPreferenceManager().getInt(userEmail + "Place2"))
        }
        else {
            binding.mainLayout.plantPlacing2.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place3") != -1) {
            binding.mainLayout.plantPlacing3.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing3.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place3"))
        }
        else {
            binding.mainLayout.plantPlacing3.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place4") != -1) {
            binding.mainLayout.plantPlacing4.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing4.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place4"))
        }
        else {
            binding.mainLayout.plantPlacing4.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place5") != -1) {
            binding.mainLayout.plantPlacing5.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing5.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place5"))
        }
        else {
            binding.mainLayout.plantPlacing5.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place6") != -1) {
            binding.mainLayout.plantPlacing6.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing6.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place6"))
        }
        else {
            binding.mainLayout.plantPlacing6.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place7") != -1) {
            binding.mainLayout.plantPlacing7.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing7.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place7"))
        }
        else {
            binding.mainLayout.plantPlacing7.visibility = View.INVISIBLE
        }

        if (SharedPreferenceManager().getInt(userEmail+"Place8") != -1) {
            binding.mainLayout.plantPlacing8.visibility = View.VISIBLE
            binding.mainLayout.plantPlacing8.setImageResource(SharedPreferenceManager().getInt(userEmail+"Place8"))
        }
        else {
            binding.mainLayout.plantPlacing8.visibility = View.INVISIBLE
        }

        if (ApplicationClass.categoryLists.size == 0)
            getPlantDoneList()

        // 식물 배치 공간에 클릭 이벤트 설정
        binding.mainLayout.plantPlacing1.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place1") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing1.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing1.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place1")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing1, "1")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing2.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place2") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing2.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing2.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place2")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing2, "2")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing3.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place3") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing3.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing3.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place3")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing3, "3")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing4.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place4") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing4.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing4.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place4")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing4, "4")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing5.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place5") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing5.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing5.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place5")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing5, "5")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing6.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place6") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing6.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing6.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place6")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing6, "6")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing7.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place7") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing7.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing7.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place7")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing7, "7")
                plantPlacingDialog.showDialog()
            }
        }

        binding.mainLayout.plantPlacing8.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place8") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("식물 배치 취소")
                        .setMessage("식물을 화단에서 제외하겠습니까?")
                        .setPositiveButton("확인") { dialog, id ->
                            binding.mainLayout.plantPlacing8.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing8.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place8")
                        }
                        .setNegativeButton("취소") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing8, "8")
                plantPlacingDialog.showDialog()
            }
        }
    }

    // 꽃피운 식물들 리스트 가져오기
    private fun getPlantDoneList() {
        ApplicationClass.retrofitManager.plantDoneCheck(object : RetrofitPlantCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: List<PlantDataContent>) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")

                ApplicationClass.plantDoneLists.addAll(data)
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
            }
        })
    }
}