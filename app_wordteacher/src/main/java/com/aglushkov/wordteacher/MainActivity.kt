package com.aglushkov.wordteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aglushkov.wordteacher.apiproviders.owlbot.service.OwlBotService
import com.aglushkov.wordteacher.apiproviders.owlbot.service.create
import com.aglushkov.wordteacher.apiproviders.wordlink.service.WordLinkService
import com.aglushkov.wordteacher.apiproviders.wordlink.service.create
import com.aglushkov.wordteacher.apiproviders.yandex.service.YandexService
import com.aglushkov.wordteacher.apiproviders.yandex.service.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val testScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = OwlBotService.create(this)
        val wordLinkService = WordLinkService.create(this)
        val yandexService = YandexService.create(this)
        testScope.launch {
            //val response = service.definitions("owl")
            //Log.d("owlbot", "response : $response")

//            val response = wordLinkService.definitions("owl",
//                    "wiktionary,century",
//                    20,
//                    null,
//                    true,
//                    true,
//                    true)
//            Log.d("wordlink", "response : $response")

            val response = yandexService.definitions("owl", "en-en", "en", 4)
            Log.d("yandex", "response : $response")
        }
    }
}
