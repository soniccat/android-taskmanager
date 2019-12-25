package com.example.alexeyglushkov.tools

import android.os.Bundle
import android.util.SparseArray

/**
 * Created by alexeyglushkov on 26.08.16.
 */
object SparceArrayTools {
    @JvmStatic
    fun storeSparceArray(array: SparseArray<String?>?, bundle: Bundle, id: Int) {
        val arrayBundle = Bundle(bundle.classLoader)
        val size = array?.size() ?: 0
        arrayBundle.putInt("sparceArrayLength", size)
        for (i in 0 until array!!.size()) {
            arrayBundle.putInt("sparceArrayKey$i", array.keyAt(i))
            arrayBundle.putString("sparceArrayValue$i", array.valueAt(i))
        }
        bundle.putBundle("sparceArray$id", arrayBundle)
    }

    @JvmStatic
    fun readSparceArray(bundle: Bundle, id: Int): SparseArray<String> {
        val arrayBundle = bundle.getBundle("sparceArray$id")
        val result = SparseArray<String>()
        val len = arrayBundle.getInt("sparceArrayLength")
        for (i in 0 until len) {
            val key = arrayBundle.getInt("sparceArrayKey$i")
            val value = arrayBundle.getString("sparceArrayValue$i")
            result.put(key, value)
        }
        return result
    }
}