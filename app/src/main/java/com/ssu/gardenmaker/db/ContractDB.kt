package com.ssu.gardenmaker.db

/*Calendar_TB에 이미지와 관련된 field값을 아직 넣지 않았는데, 추후에 진행하기.*/
class ContractDB {
    companion object{
        const val DB_NAME:String="GardendMaker_DB"
        var DB_VERSION=1
        //Calendar 관련 DB명령어
        const val C_TBL="Calendar_TB"
        const val CREATE_Calendar_TB="CREATE TABLE IF NOT exists Calendar_TB(" +
                "NAME TEXT," +
                "CATEGORY TEXT,"+
                "FUCTION TEXT,"+
                "START_DAY INTEGER,"+
                "END_DAY INTEGER" +
                ")"
        fun INSERT_Calendar_TB(name:String,category:String,function:String,start_day:Int,end_day:Int):String{
            return "INSERT INTO $C_TBL VALUES('$name','$category','$function','$start_day','$end_day')"
        }
        const val SELECTAll_Calendar_TB="SELECT * FROM $C_TBL"
        fun SELECT_DATE_Calendar_TB(cal_day:Int):String{
            return "SELECT * FROM $C_TBL WHERE $cal_day>start_day AND $cal_day<end_day ORDER BY end_day"
        }


        //CheckList 관련 DB명령어
        const val Check_TBL="Checkbox_TB"
        const val CREATE_Checkbox_TB="CREATE TABLE IF NOT exists Checkbox_TB(" +
                "ID INTEGER,"+
                "TITLE TEXT," +
                "START_DAY INTEGER,"+
                "END_DAY INTEGER" +
                ")"
        fun INSERT_Checkbox_TB(id:Int,title:String,start_day:Int,end_day:Int):String{
            return "INSERT INTO $Check_TBL VALUES('$id','$title','$start_day','$end_day')"
        }
        const val SELECTAll_Checkbox_TB="SELECT * FROM $Check_TBL"
    }
}