package com.bonhams.expensemanagement.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private lateinit var prefs: SharedPreferences
    private const val PREFS_NAME = "expense_management_prefs"
    // list of app specific preferences
    private val IS_LOGGED_IN = Pair("is_logged_in", false)
    private val IS_TOKEN_AVAILABLE = Pair("is_token_avail", false)
    private val FIREBASE_TOKEN = Pair("firebase_token", "")
    private val USER_ID = Pair("user_id", "")
    private val PASSWORD = Pair("password", "")
    private val EMPLOYEE_ID = Pair("employee_id", "")
    private val USER_TOKEN = Pair("user_token", "")
    private val REFRESH_TOKEN = Pair("refresh_token", "")
    private val FULL_NAME = Pair("full_name", "")
    private val FIRST_NAME = Pair("first_name", "")
    private val LAST_NAME = Pair("last_name", "")
    private val EMAIL = Pair("email", "")
    private val PHONE_NUMBER = Pair("phone_number", "")
    private val PROFILE_PIC = Pair("profile_pic", "")

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit()
    and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isLoggedIn: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = prefs.getBoolean(IS_LOGGED_IN.first, IS_LOGGED_IN.second)
        // custom setter to save a preference back to preferences file
        set(value) = prefs.edit {
            it.putBoolean(IS_LOGGED_IN.first, value)
        }

    var isTokenAvailable: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = prefs.getBoolean(IS_TOKEN_AVAILABLE.first, IS_TOKEN_AVAILABLE.second)
        // custom setter to save a preference back to preferences file
        set(value) = prefs.edit {
            it.putBoolean(IS_TOKEN_AVAILABLE.first, value)
        }

    var password: String
        get() = prefs.getString(PASSWORD.first, PASSWORD.second)!!
        set(value) = prefs.edit {
            it.putString(PASSWORD.first, value)
        }

    var fireBaseToken: String
        get() = prefs.getString(FIREBASE_TOKEN.first, FIREBASE_TOKEN.second)!!
        set(value) = prefs.edit {
            it.putString(FIREBASE_TOKEN.first, value)
        }

    var userId: String
        get() = prefs.getString(USER_ID.first, USER_ID.second)!!
        set(value) = prefs.edit {
            it.putString(USER_ID.first, value)
        }

    var employeeId: String
        get() = prefs.getString(EMPLOYEE_ID.first, EMPLOYEE_ID.second)!!
        set(value) = prefs.edit {
            it.putString(EMPLOYEE_ID.first, value)
        }

    var userToken: String
        get() = prefs.getString(USER_TOKEN.first, USER_TOKEN.second)!!
        set(value) = prefs.edit {
            it.putString(USER_TOKEN.first, value)
        }

    var refreshToken: String
        get() = prefs.getString(REFRESH_TOKEN.first, REFRESH_TOKEN.second)!!
        set(value) = prefs.edit {
            it.putString(REFRESH_TOKEN.first, value)
        }

    var fullName: String
        get() = prefs.getString(FULL_NAME.first, FULL_NAME.second)!!
        set(value) = prefs.edit {
            it.putString(FULL_NAME.first, value)
        }

    var firstName: String
        get() = prefs.getString(FIRST_NAME.first, FIRST_NAME.second)!!
        set(value) = prefs.edit {
            it.putString(FIRST_NAME.first, value)
        }

    var lastName: String
        get() = prefs.getString(LAST_NAME.first, LAST_NAME.second)!!
        set(value) = prefs.edit {
            it.putString(LAST_NAME.first, value)
        }

    var email: String
        get() = prefs.getString(EMAIL.first, EMAIL.second)!!
        set(value) = prefs.edit {
            it.putString(EMAIL.first, value)
        }

    var phoneNumber: String
        get() = prefs.getString(PHONE_NUMBER.first, PHONE_NUMBER.second)!!
        set(value) = prefs.edit {
            it.putString(PHONE_NUMBER.first, value)
        }

    var profilePic: String
        get() = prefs.getString(PROFILE_PIC.first, PROFILE_PIC.second)!!
        set(value) = prefs.edit {
            it.putString(PROFILE_PIC.first, value)
        }


    fun clearPrefs(){
        // User selected remember me. Store email and password
        if(isLoggedIn && isTokenAvailable){
            prefs.edit {
                it.remove(IS_LOGGED_IN.first)
                it.remove(IS_TOKEN_AVAILABLE.first)
                it.remove(FIREBASE_TOKEN.first)
//                it.remove(PASSWORD.first)
                it.remove(USER_ID.first)
                it.remove(EMPLOYEE_ID.first)
                it.remove(USER_TOKEN.first)
                it.remove(REFRESH_TOKEN.first)
                it.remove(FIRST_NAME.first)
                it.remove(LAST_NAME.first)
//                it.remove(EMAIL.first)
                it.remove(PROFILE_PIC.first)
            }
        }
        else { // User did not select remember me. Clear all details
            isLoggedIn = false
            prefs.edit {
                it.remove(IS_LOGGED_IN.first)
                it.remove(IS_TOKEN_AVAILABLE.first)
                it.remove(FIREBASE_TOKEN.first)
                it.remove(PASSWORD.first)
                it.remove(USER_ID.first)
                it.remove(EMPLOYEE_ID.first)
                it.remove(USER_TOKEN.first)
                it.remove(REFRESH_TOKEN.first)
                it.remove(FIRST_NAME.first)
                it.remove(LAST_NAME.first)
                it.remove(EMAIL.first)
                it.remove(PROFILE_PIC.first)
            }
        }
    }
}
