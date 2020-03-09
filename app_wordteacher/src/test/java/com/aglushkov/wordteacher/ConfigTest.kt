package com.aglushkov.wordteacher

import com.aglushkov.wordteacher.repository.Config
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.decodeConfigs
import com.aglushkov.wordteacher.service.encodeConfigs
import com.google.gson.GsonBuilder
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ConfigTest {
    @Test
    fun parseConfigTest() {
        // Assume
        val json = """
            {
                type: "owlbot",
                baseUrls: ["https://owlbot.info/"],
                keys: ["o"],
                params: {}
            }
        """.trimIndent()

        val gson = GsonBuilder().create()

        // Act
        val decodedConfig = gson.fromJson<Config>(json, Config::class.java)

        // Assert
        assertNotNull(decodedConfig)
        assertEquals(Config.Type.OwlBot, decodedConfig.type)
        assertEquals("https://owlbot.info/", decodedConfig.baseUrls.first())
        assertEquals("o", decodedConfig.keys.first())
    }

    @Test
    fun encodeConfigListTest() {
        // Assume
        val config = Config(Config.Type.OwlBot, listOf("url"), listOf("key"))

        // Act
        val encodedList = ConfigService.encodeConfigs(listOf(config))

        // Assert
        assertTrue(encodedList.isNotEmpty())
    }

    @Test
    fun parseConfigListTest() {
        // Assume
        val jsonByteArray = """
            [
            	{
            		type: "owlbot",
            		baseUrls: ["https://owlbot.info/"],
            		keys: ["o"],
            		params: {}
            	}
            ]
        """.trimIndent().toByteArray()

        // Act
        val decodedList = ConfigService.decodeConfigs(jsonByteArray)

        // Assert
        assertNotNull(decodedList)
        assertEquals(Config.Type.OwlBot, decodedList.first().type)
        assertEquals("https://owlbot.info/", decodedList.first().baseUrls.first())
        assertEquals("o", decodedList.first().keys.first())
    }
}