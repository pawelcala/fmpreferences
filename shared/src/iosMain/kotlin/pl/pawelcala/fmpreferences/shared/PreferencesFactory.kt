package pl.pawelcala.fmpreferences.shared


import platform.Foundation.NSUserDefaults

actual class PreferencesFactory {

    actual fun createPreferences(): Preferences = IOSPreferences("prefs")

}

class IOSPreferences : Preferences {
    private val userDefault: NSUserDefaults

    constructor(name: String?) {
        userDefault = NSUserDefaults(suiteName = name)
    }

    override fun setInt(key: String, value: Int) {
        return userDefault.setInteger(value.toLong(), key)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return if (hasKey(key))
            userDefault.integerForKey(key).toInt()
        else defaultValue
    }

    override fun getInt(key: String): Int? {
        return if (hasKey(key))
            userDefault.integerForKey(key).toInt()
        else null
    }

    override fun setFloat(key: String, value: Float) {
        return userDefault.setFloat(value, key)
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return if (hasKey(key))
            userDefault.floatForKey(key)
        else defaultValue
    }

    override fun getFloat(key: String): Float? {
        return if (hasKey(key)) {
            userDefault.floatForKey(key)
        } else {
            null
        }
    }

    override fun setLong(key: String, value: Long) {
        return userDefault.setInteger(value, key)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return if (hasKey(key))
            userDefault.integerForKey(key)
        else defaultValue
    }

    override fun getLong(key: String): Long? {
        return if (hasKey(key)) {
            userDefault.integerForKey(key)
        } else {
            null
        }
    }

    override fun setString(key: String, value: String) {
        return userDefault.setObject(value, key)
    }

    override fun getString(key: String, defaultValue: String): String {
        return userDefault.stringForKey(key) ?: defaultValue
    }

    override fun getString(key: String): String? {
        return if (hasKey(key)) {
            userDefault.stringForKey(key) ?: ""
        } else {
            null
        }
    }

    override fun setBoolean(key: String, value: Boolean) {
        return userDefault.setBool(value, key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (hasKey(key))
            userDefault.boolForKey(key)
        else defaultValue
    }

    override fun getBoolean(key: String): Boolean? {
        return if (hasKey(key)) {
            userDefault.boolForKey(key)
        } else {
            null
        }
    }

    override fun hasKey(key: String): Boolean = userDefault.objectForKey(key) != null

    override fun clear() {
        for (key in userDefault.dictionaryRepresentation().keys) {
            remove(key as String)
        }
    }

    override fun remove(key: String) {
        userDefault.removeObjectForKey(key)
    }

}