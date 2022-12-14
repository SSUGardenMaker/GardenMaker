package com.ssu.gardenmaker.ui

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

        // ????????? ????????????
        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
            requestPermissions(permissions,0)
        }

        // ??? ???????????? ??????
        binding.mainLayout.scrollViewMain.post{
            val mid = binding.mainLayout.scrollViewMain.width / ((this.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)
            binding.mainLayout.scrollViewMain.scrollTo(mid.toInt(), 0)
        }
    }

    // ?????? ?????? ??? ????????? ?????????
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

    // 2??? ????????? ???????????? ????????? ?????? ??? ????????? ??? ?????? / ???????????? ?????? ?????? ???????????? ???????????? ????????? ????????? ???????????? ??????
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
        else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            } else {
                moveTaskToBack(true) // ???????????? ?????????????????? ??????
                finishAndRemoveTask() // ???????????? ?????? + ????????? ??????????????? ?????????
                exitProcess(0)
            }
        }
    }

    // ?????? ?????? ????????? ?????? ?????? ??? ???????????? ??????
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // ????????? ?????? ?????? ????????? id ?????? ????????? ??????????????? ??????
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // ????????? ??? ????????? ?????? ????????? ???????????? ??????
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    // ?????? ?????? ??????
    @RequiresApi(VERSION_CODES.P)
    private fun initButtonFunction() {
        // ????????? ??????
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

        // ?????? ?????? ON/OFF
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

        // ?????? ?????? ???????????????
        binding.mainLayout.btnPlusPlan.setOnClickListener {
            val plantCreateDialog = PlantCreateDialog(this@MainActivity, layoutInflater)
            plantCreateDialog.showDialog()
        }

        // ????????? ?????????
        binding.categoryDoneNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlantDoneActivity::class.java))
        }

        // ?????? ??????
        binding.plantBookNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlantBookActivity::class.java))
        }

        // ???????????? ??????
        binding.navigationChangePasswordNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChangePasswordActivity::class.java))
        }

        // ????????????
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

    // ??????????????? ????????? ?????????
    private fun initNavigationMenu() {
        drawerLayout = binding.mainDrawerLayout
        navigationView = binding.mainNavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val navigationMenu = binding.mainLayout.mainLayoutToolbar.findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(navigationMenu) // ?????? ??????

        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // ???????????? ?????? ?????? ?????? ?????????
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)   // ?????? ?????? ????????? ??????
        supportActionBar?.setDisplayShowTitleEnabled(false)   // ????????? ????????? ??? ????????? ???

        initCategoryList()
    }

    // ??????????????? ?????? ???????????? ?????????
    private fun initCategoryList() {
        getCategoryList()

        val parentList = mutableListOf("?????? ??????")
        val childList = mutableListOf(ApplicationClass.categoryLists)

        val categoryList = binding.mainNaviListview.findViewById<ExpandableListView>(R.id.main_navi_listview)
        val categoryExpandableListAdapter = CategoryExpandableListAdapter(this, parentList, childList)  // List Adapter ?????????
        categoryList.setAdapter(categoryExpandableListAdapter)    // List??? Adapter ??????

        // ?????? ????????? ?????? ?????????
        categoryList.setOnGroupClickListener { parent, v, groupPosition, id ->
            false
        }

        // ?????? ????????? ?????? ?????????
        categoryList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val intent = Intent(this, GardenActivity::class.java)
            intent.putExtra("NAME", ApplicationClass.categoryLists[childPosition].name)
            intent.putExtra("ID", ApplicationClass.categoryLists[childPosition].id)
            startActivity(intent)
            false
        }

        // ?????? ??????
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
                Toast.makeText(this@MainActivity, "????????? ?????? 4????????? ?????? ??? ????????????", Toast.LENGTH_SHORT).show()
            }
        }

        // ?????? ??????
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

    // ???????????? ????????? ????????????
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

    // ?????? ?????? ?????? ??????
    private fun initPlantPlacing() {
        // ????????? ?????? ????????? ?????? ?????? ?????? + ????????? ?????? ?????? ????????? ??????????????? ??????
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

        // ?????? ?????? ????????? ?????? ????????? ??????
        binding.mainLayout.plantPlacing1.setOnClickListener {
            if (SharedPreferenceManager().getInt(userEmail+"Place1") != -1) {
                if (SharedPreferenceManager().getString("plantPlacing").equals("ON")) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing1.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing1.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place1")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing2.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing2.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place2")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing3.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing3.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place3")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing4.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing4.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place4")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing5.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing5.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place5")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing6.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing6.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place6")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing7.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing7.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place7")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
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
                    builder.setTitle("?????? ?????? ??????")
                        .setMessage("????????? ???????????? ??????????????????????")
                        .setPositiveButton("??????") { dialog, id ->
                            binding.mainLayout.plantPlacing8.setImageResource(R.drawable.plant_empty)
                            binding.mainLayout.plantPlacing8.startAnimation(animation)
                            SharedPreferenceManager().deleteData(userEmail+"Place8")
                        }
                        .setNegativeButton("??????") { dialog, id -> }
                    builder.show()
                }
            }
            else {
                val plantPlacingDialog = PlantPlacingDialog(this@MainActivity, binding.mainLayout.plantPlacing8, "8")
                plantPlacingDialog.showDialog()
            }
        }
    }

    // ????????? ????????? ????????? ????????????
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