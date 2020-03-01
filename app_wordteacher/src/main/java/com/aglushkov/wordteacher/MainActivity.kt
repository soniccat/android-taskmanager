package com.aglushkov.wordteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aglushkov.wordteacher.apiproviders.google.service.GoogleService
import com.aglushkov.wordteacher.apiproviders.google.service.create
import com.aglushkov.wordteacher.apiproviders.google.service.createWordTeacherWordService
import com.aglushkov.wordteacher.apiproviders.owlbot.service.OwlBotService
import com.aglushkov.wordteacher.apiproviders.owlbot.service.create
import com.aglushkov.wordteacher.apiproviders.owlbot.service.createWordTeacherWordService
import com.aglushkov.wordteacher.apiproviders.wordlink.service.WordLinkService
import com.aglushkov.wordteacher.apiproviders.wordlink.service.create
import com.aglushkov.wordteacher.apiproviders.yandex.service.YandexService
import com.aglushkov.wordteacher.apiproviders.yandex.service.create
import com.aglushkov.wordteacher.apiproviders.yandex.service.createWordTeacherWordService
import com.aglushkov.wordteacher.repository.ServiceConfig
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val testScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val owlBotConfig = ServiceConfig(listOf(getString(R.string.owlbot_base_url)),
//                listOf(getString(R.string.owlbot_token)))
//        val service = OwlBotService.createWordTeacherWordService(owlBotConfig.baseUrls.first(), owlBotConfig.keys.first())

//        val googleConfig = ServiceConfig(listOf(getString(R.string.goolge_base_url)),
//                emptyList(),
//                ServiceMethodParams(mapOf(GoogleService.EntriesMethod to mapOf(GoogleService.EntriesMethodLang to "en"))))
//        val service = GoogleService.createWordTeacherWordService(googleConfig.baseUrls.first(), googleConfig.methodOptions)

        val wordLinkService = WordLinkService.create(this)

        val yandexConfig = ServiceConfig(listOf(getString(R.string.yandex_base_url)),
                listOf(getString(R.string.yandex_key)),
                ServiceMethodParams(mapOf(YandexService.LookupMethod to
                        mapOf(YandexService.LookupMethodLang to "en-en",
                              YandexService.LookupMethodUi to "en",
                              YandexService.LookupMethodFlags to "4"
                            ))))
        val service = YandexService.createWordTeacherWordService(yandexConfig.baseUrls.first(),
                yandexConfig.keys.first(),
                yandexConfig.methodOptions)
        testScope.launch {
            val response = service.define("owl")
            //Log.d("owlbot", "response : $response")

//            val response = wordLinkService.definitions("owl",
//                    "wiktionary,century",
//                    20,
//                    null,
//                    true,
//                    true,
//                    true)
//            Log.d("wordlink", "response : $response")

//            val response = yandexService.definitions("owl", "en-en", "en", 4)
//            Log.d("yandex", "response : $response")

            //val response = googleService.definitions("hello", "en")
            Log.d("google", "response : $response")
        }
    }
}
