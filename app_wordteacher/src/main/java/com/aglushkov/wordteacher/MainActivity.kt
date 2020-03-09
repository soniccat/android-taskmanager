package com.aglushkov.wordteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aglushkov.wordteacher.apiproviders.wordlink.service.WordLinkService
import com.aglushkov.wordteacher.apiproviders.wordlink.service.createWordTeacherWordService
import com.aglushkov.wordteacher.repository.Config
import com.aglushkov.wordteacher.repository.ConfigRepository
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.create
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    val testScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val mainScope = CoroutineScope(Dispatchers.Main) + SupervisorJob()

    lateinit var configRepository: ConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val baseUrl = getString(R.string.config_base_url)
        val configService = ConfigService.create(baseUrl)

        configRepository = ConfigRepository(configService, testScope)

        mainScope.launch {
            configRepository.flow.collect {
                Log.d("Config", "" + it)
            }
        }

//        val owlBotConfig = ServiceConfig(listOf(getString(R.string.owlbot_base_url)),
//                listOf(getString(R.string.owlbot_token)))
//        val service = OwlBotService.createWordTeacherWordService(owlBotConfig.baseUrls.first(), owlBotConfig.keys.first())

//        val googleConfig = ServiceConfig(listOf(getString(R.string.goolge_base_url)),
//                emptyList(),
//                ServiceMethodParams(mapOf(GoogleService.EntriesMethod to mapOf(GoogleService.EntriesMethodLang to "en"))))
//        val service = GoogleService.createWordTeacherWordService(googleConfig.baseUrls.first(), googleConfig.methodOptions)

        val wordLinkConfig = Config(
                Config.Type.Wordlink,
                listOf(getString(R.string.wordlink_base_url)),
                listOf(getString(R.string.wordlink_key)),
                ServiceMethodParams(mapOf(WordLinkService.Definitions to mapOf(
                        WordLinkService.DefinitionsSourceDictionaries to WordLinkService.Dictionary.Ahd5.value,
                        WordLinkService.DefinitionsIncludeRelated to true.toString(),
                        WordLinkService.DefinitionsIncludeTags to true.toString(),
                        WordLinkService.DefinitionsLimit to 11.toString(),
                        WordLinkService.DefinitionsUseCanonical to true.toString()
                ))))
        val service = WordLinkService.createWordTeacherWordService(
                wordLinkConfig.baseUrls.first(),
                wordLinkConfig.keys.first(),
                wordLinkConfig.methodOptions)

//        val yandexConfig = ServiceConfig(listOf(getString(R.string.yandex_base_url)),
//                listOf(getString(R.string.yandex_key)),
//                ServiceMethodParams(mapOf(YandexService.Lookup to
//                        mapOf(YandexService.LookupLang to "en-en",
//                              YandexService.LookupUi to "en",
//                              YandexService.LookupFlags to "4"
//                            ))))
//        val service = YandexService.createWordTeacherWordService(yandexConfig.baseUrls.first(),
//                yandexConfig.keys.first(),
//                yandexConfig.methodOptions)
        testScope.launch {
            try {
//                val response = service.define("owl")
//            Log.d("owlbot", "response : $response")

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

//            val response = googleService.definitions("hello", "en")
//                Log.d("google", "response : $response")
            } catch (ex: Exception) {
                Log.d("abc", ex.message)
                ex.printStackTrace()
            }
        }
    }
}
