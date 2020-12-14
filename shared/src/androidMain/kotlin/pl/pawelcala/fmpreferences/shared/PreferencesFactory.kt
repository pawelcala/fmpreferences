package pl.pawelcala.fmpreferences.shared

import android.content.Context
import android.content.SharedPreferences

actual class PreferencesFactory(private val context: Context) {

    actual fun createPreferences(): Preferences = AndroidPreferences(context, "prefs")

}

class AndroidPreferences(context: Context, name: String?) : Preferences {

    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private fun SharedPreferences.apply(block: SharedPreferences.Editor.() -> Unit) =
        edit().apply { block() }.apply()

    override fun setInt(key: String, value: Int) = prefs.apply { putInt(key, value) }

    override fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)

    override fun getInt(key: String): Int? =
        if (prefs.contains(key)) prefs.getInt(key, 0) else null

    override fun setFloat(key: String, value: Float) = prefs.apply { putFloat(key, value) }

    override fun getFloat(key: String, defaultValue: Float): Float =
        prefs.getFloat(key, defaultValue)

    override fun getFloat(key: String): Float? =
        if (prefs.contains(key)) prefs.getFloat(key, 0f) else null

    override fun setLong(key: String, value: Long) = prefs.apply { putLong(key, value) }

    override fun getLong(key: String, defaultValue: Long): Long = prefs.getLong(key, defaultValue)

    override fun getLong(key: String): Long? =
        if (prefs.contains(key)) prefs.getLong(key, 0L) else null

    override fun setString(key: String, value: String) = prefs.apply { putString(key, value) }

    override fun getString(key: String, defaultValue: String): String =
        prefs.getString(key, defaultValue)!!

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun setBoolean(key: String, value: Boolean) = prefs.apply { putBoolean(key, value) }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        prefs.getBoolean(key, defaultValue)

    override fun getBoolean(key: String): Boolean? =
        if (prefs.contains(key)) prefs.getBoolean(key, false) else null

    override fun remove(key: String) = prefs.apply { remove(key) }

    override fun clear() = prefs.apply { clear() }

    override fun hasKey(key: String): Boolean = prefs.contains(key)

}