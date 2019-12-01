package com.example.alexeyglushkov.quizletservice

import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet
import com.example.alexeyglushkov.service.SimpleService
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Function

/**
 * Created by alexeyglushkov on 26.03.16.
 */
class QuizletService(account: Account,
                     commandProvider: QuizletCommandProvider,
                     commandRunner: ServiceCommandRunner) : SimpleService() {
    //// Actions
    fun loadSets(progressListener: ProgressListener): Single<List<QuizletSet>> {
        return authorizeIfNeeded()
                .flatMap(object : Function<AuthCredentials, SingleSource<List<QuizletSet>>> {
                    @Throws(Exception::class)
                    override fun apply(authCredentials: AuthCredentials): SingleSource<List<QuizletSet>> {
                        val command = createSetsCommand(progressListener)
                        return runCommand(command, true)
                    }
                })
    }

    // OLD restoration approach based on command internal cache
//    public Single<List<QuizletSet>> restoreSets(final ProgressListener progressListener) {
//        return RxTools.justOrError(getAccount().getCredentials())
//            .flatMap(new Function<AuthCredentials, SingleSource<? extends QuizletSetsCommand>>() {
//                @Override
//                public SingleSource<? extends QuizletSetsCommand> apply(AuthCredentials authCredentials) throws Exception {
//                    QuizletSetsCommand command = createSetsCommand(StorageClient.CacheMode.ONLY_LOAD_FROM_CACHE, progressListener);
//                    return runCommand(command, false);
//                }
//            }).flatMap(new Function<QuizletSetsCommand, SingleSource<? extends List<QuizletSet>>>() {
//                @Override
//                public SingleSource<? extends List<QuizletSet>> apply(QuizletSetsCommand quizletSetsCommand) throws Exception {
//                    return Single.just(new ArrayList<>(Arrays.asList(quizletSetsCommand.getResponse())));
//                }
//            });
//    }
    private fun createSetsCommand(progressListener: ProgressListener): QuizletSetsCommand {
        val userId = oAuthCredentials?.userId
        checkNotNull(userId)

        return quizletCommandProvider.getLoadSetsCommand(SERVER, userId, progressListener)
    }

    // Cast Getters
    private val oAuthCredentials: OAuthCredentials?
        get() = account?.credentials as? OAuthCredentials

    private val quizletCommandProvider: QuizletCommandProvider
        get() = commandProvider as QuizletCommandProvider

    companion object {
        private const val SERVER = "https://api.quizlet.com/2.0"
    }

    //// Initialization
    init {
        this.account = account
        setServiceCommandProvider(commandProvider)
        setServiceCommandRunner(commandRunner)
    }
}