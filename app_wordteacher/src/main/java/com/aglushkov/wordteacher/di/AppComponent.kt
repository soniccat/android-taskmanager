package com.aglushkov.wordteacher.di

import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.wordteacher.repository.ConfigConnectParamsStatRepository
import com.aglushkov.wordteacher.repository.ConfigRepository
import com.aglushkov.wordteacher.repository.ServiceRepository
import com.aglushkov.wordteacher.repository.WordTeacherWordServiceFactory
import com.aglushkov.wordteacher.service.ConfigService
import dagger.Component


@AppComp
@Component(modules = [AppModule::class, AppContextModule::class] )
public interface AppComponent {
    fun getConfigService(): ConfigService
    fun getConfigRepository(): ConfigRepository
    fun getConfigConnectParamsStatRepository(): ConfigConnectParamsStatRepository
    fun getServiceRepository(): ServiceRepository
    fun getWordTeacherWordServiceFactory(): WordTeacherWordServiceFactory
    fun getWordRepository(): WordRepository
}
