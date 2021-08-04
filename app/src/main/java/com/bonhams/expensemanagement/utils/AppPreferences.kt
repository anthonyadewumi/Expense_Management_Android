package com.bonhams.expensemanagement.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private lateinit var prefs: SharedPreferences
    private const val PREFS_NAME = "expense_management_prefs"
    // list of app specific preferences
    private val IS_LOGGED_IN = Pair("is_logged_in", false)

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
}