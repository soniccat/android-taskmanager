package com.aglushkov.wordteacher.di

import android.content.Context
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.wordteacher.repository.ConfigConnectParamsStatRepository
import com.aglushkov.wordteacher.repository.ConfigRepository
import com.aglushkov.wordteacher.repository.ServiceRepository
import com.aglushkov.wordteacher.repository.WordTeacherWordServiceFactory
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.create
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
class AppModule {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @AppComp
    @Provides
    fun createConfigService(context: Context): ConfigService {
        val baseUrl = context.getString(R.string.config_base_url)
        return ConfigService.create(baseUrl)
    }

    @AppComp
    @Provides
    fun createConfigRepository(configService: ConfigService): ConfigRepository {
        return ConfigRepository(configService, ioScope)
    }

    @AppComp
    @Provides
    fun createConfigConnectParamsStatRepository(context: Context): ConfigConnectParamsStatRepository {
        return ConfigConnectParamsStatRepository(context)
    }

    @AppComp
    @Provides
    fun createServiceRepository(configRepository: ConfigRepository,
                                configConnectParamsStatRepository: ConfigConnectParamsStatRepository,
                                factory: WordTeacherWordServiceFactory): ServiceRepository {
        return ServiceRepository(configRepository, configConnectParamsStatRepository, factory)
    }

    @AppComp
    @Provides
    fun createWordRepository(serviceRepository: ServiceRepository): WordRepository {
        return WordRepository(serviceRepository)
    }

    @AppComp
    @Provides
    fun getWordTeacherWordServiceFactory(): WordTeacherWordServiceFactory {
        return WordTeacherWordServiceFactory()
    }
}