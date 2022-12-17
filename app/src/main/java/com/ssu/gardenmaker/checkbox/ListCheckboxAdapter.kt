package com.ssu.gardenmaker.checkbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.db.ContractDB

class ListCheckboxAdapter(context: Context, itemList: ArrayList<ListCheckboxDB>) : BaseAdapter() {

    data class ListCheckboxDB(var id: Int, var title: String, var today: Int, var flag: String)

    private var mContext = context
    private var mItemList = itemList

    override fun getCount(): Int {
        return mItemList.size
    }

    override fun getItem(p0: Int): Any {
        return mItemList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var view: View? =p1
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view=inflater.inflate(R.layout.dialog_checkbox_list, p2, false)
        }

        val checkBox = view?.findViewById<CheckBox>(R.id.dialog_checkbox_list_checkbox)
        checkBox?.isChecked = mItemList[p0].flag == "Y"

        view?.findViewById<TextView>(R.id.dialog_checkbox_list_title_text)?.text = mItemList[p0].title

        val checkDate = mItemList[p0].today.toString().substring(0, 4) + "-" + mItemList[p0].today.toString().substring(4, 6) + "-" + mItemList[p0].today.toString().substring(6)
        view?.findViewById<TextView>(R.id.dialog_checkbox_list_sub_text)?.text = checkDate

        // 체크박스 클릭 이벤트
        checkBox?.setOnClickListener {
            if (checkBox.isChecked) {
                ApplicationClass.db.execSQL(
                    ContractDB.updateCheckboxTB(
                        mItemList[p0].id,
                        "Y"
                    )
                )
            }
            else {
                ApplicationClass.db.execSQL(
                    ContractDB.updateCheckboxTB(
                        mItemList[p0].id,
                        "N"
                    )
                )
            }
        }

        // 체크리스트 원소 삭제
        view?.findViewById<ImageButton>(R.id.btn_checkbox_delete)?.setOnClickListener {
            Toast.makeText(mContext, mItemList[p0].title + " 삭제", Toast.LENGTH_SHORT).show()
            ApplicationClass.db.execSQL(
                ContractDB.deleteCheckboxTB(
                    mItemList[p0].id
                )
            )
            mItemList.removeAt(p0)
            notifyDataSetChanged()
        }

        return view
    }
}