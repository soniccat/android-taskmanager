package com.aglushkov.modelcore.extensions

import android.app.Application
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getString(res: Int) = getApplication<Application>().getString(res)