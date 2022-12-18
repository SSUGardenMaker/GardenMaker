package com.ssu.gardenmaker.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.garden.GardenDataContent

class CategoryListAdapter(
    private val context: Context,
    private val categoryList : MutableList<GardenDataContent>
    ) : BaseAdapter() {

    private val TAG = "CategoryListAdapter"

    private class ViewHolder {
        var categoryName : TextView? = null
        var ButtonModify : ImageButton? = null
        var ButtonDelete : ImageButton? = null
    }

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(position : Int): Any {
        return categoryList[position].name
    }

    override fun getItemId(position : Int): Long {
        return position.toLong()
    }

    override fun getView(position : Int, convertView : View?, parent : ViewGroup?): View {
        val viewHolder : ViewHolder
        val view : View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_category, parent, false)
            viewHolder = ViewHolder()
            viewHolder.categoryName = view.findViewById(R.id.tv_listview)
            viewHolder.ButtonModify = view.findViewById(R.id.btn_modify)
            viewHolder.ButtonDelete = view.findViewById(R.id.btn_delete)
            view.tag = viewHolder
        }
        else {
            viewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        viewHolder.categoryName?.text = categoryList[position].name

        viewHolder.ButtonModify?.setOnClickListener {
            val categoryChangeNameDialog = CategoryChangeNameDialog(context)
            categoryChangeNameDialog.showDialog()
            categoryChangeNameDialog.setOnClickListener(object : CategoryChangeNameDialog.CategoryChangeNameDialogClickListener {
                override fun onClicked(name: String) {
                    ApplicationClass.retrofitManager.gardenEdit(categoryList[position].id, name, name, object : RetrofitCallback {
                        override fun onError(t: Throwable) {
                            Log.d(TAG, "onError : " + t.localizedMessage)
                        }

                        override fun onSuccess(message: String, data: String) {
                            Log.d(TAG, "onSuccess : message -> $message")
                            Log.d(TAG, "onSuccess : data -> $data")
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            categoryList[position].category = name
                            categoryList[position].name = name
                            notifyDataSetChanged()
                        }

                        override fun onFailure(errorMessage: String, errorCode: Int) {
                            Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                            Log.d(TAG, "onFailure : errorCode -> $errorCode")
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            })
        }

        viewHolder.ButtonDelete?.setOnClickListener {
            // 해당 화단 안에 아무것도 없을 경우에만 삭제 가능
            val categoryDeleteDialog = CategoryDeleteDialog(context, categoryList[position].name)
            categoryDeleteDialog.showDialog()
            categoryDeleteDialog.setOnClickListener(object : CategoryDeleteDialog.CategoryDeleteDialogClickListener {
                override fun onClicked() {
                    ApplicationClass.retrofitManager.gardenDelete(categoryList[position].id, object : RetrofitCallback {
                        override fun onError(t: Throwable) {
                            Log.d(TAG, "onError : " + t.localizedMessage)
                        }

                        override fun onSuccess(message: String, data: String) {
                            Log.d(TAG, "onSuccess : message -> $message")
                            Log.d(TAG, "onSuccess : data -> $data")
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            categoryList.removeAt(position)
                            notifyDataSetChanged()
                        }

                        override fun onFailure(errorMessage: String, errorCode: Int) {
                            Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                            Log.d(TAG, "onFailure : errorCode -> $errorCode")
                            Toast.makeText(context, "화단에 식물이 있어 삭제가 불가능합니다", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            })
        }

        return view
    }
}