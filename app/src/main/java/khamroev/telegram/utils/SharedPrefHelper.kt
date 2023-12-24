package khamroev.telegram.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import khamroev.telegram.UserData

class SharedPrefHelper private constructor(context: Context){
    private val shared: SharedPreferences = context.getSharedPreferences("data", 0)
    private val edit: SharedPreferences.Editor = shared.edit()
    private val gson  = Gson()
    var user_string="user"


    companion object{
        private var instance :SharedPrefHelper? = null
        fun getInstance(context: Context):SharedPrefHelper{
            if (instance == null) instance = SharedPrefHelper(context)
            return instance!!
        }
    }

    fun setUser(user: UserData?){
        val data  = gson.toJson(user)
        edit.putString(user_string, data).apply()
        Log.d("SET","I am being used ${user}")
    }

    fun logOut(){
        edit.putString(user_string, "").apply()
    }
    fun getUser():UserData?{
        val data = shared.getString(user_string,"")
        if (data == "") return null
        val typeToken = object : TypeToken<UserData>() {}.type

        Log.d("GET","I am being used")
        return gson.fromJson(data, typeToken)

    }

}