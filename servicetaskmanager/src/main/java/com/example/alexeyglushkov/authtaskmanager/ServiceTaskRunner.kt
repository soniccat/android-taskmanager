package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner.Callback
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.example.alexeyglushkov.taskmanager.task.TaskProvider
import com.example.alexeyglushkov.taskmanager.task.Tasks.isTaskCompleted
import io.reactivex.Single
import io.reactivex.disposables.Disposables

/**
 * Created by alexeyglushkov on 04.11.15.
 */
class ServiceTaskRunner(private val taskManager: TaskManager, id: String) : ServiceCommandRunner {
    private val taskProvider: TaskProvider


    override suspend fun <R, C : ServiceCommand<R>> run(command: C): R {
        val serviceTask = command as ServiceTask<*>
        val task = serviceTask.task
        task.taskCallback = object : Task.Callback {
            override fun onCompleted(cancelled: Boolean) {
                callback.onCompleted(serviceTask.commandError, cancelled)
            }
        }
        taskProvider.addTask(task)
    }

//    override fun <T : ServiceCommand<*>> run(command: T): Single<T> {
//        return Single.create { emitter ->
//            val serviceTask = command as ServiceTask<*>
//            emitter.setDisposable(Disposables.fromRunnable {
//                if (!isTaskCompleted(serviceTask.task)) {
//                    cancel(serviceTask)
//                }
//            })
//            run(serviceTask, object : Callback {
//                override fun onCompleted(error: Error?, isCancelled: Boolean) {
//                    if (!emitter.isDisposed && !isCancelled) {
//                        if (error != null) {
//                            emitter.onError(error)
//                        } else {
//                            emitter.onSuccess(command)
//                        }
//                    }
//                }
//            })
//        }
//    }

//    override fun <T : ServiceCommand<*>> run(command: T, callback: Callback) {
//        val serviceTask = command as ServiceTask<*>
//        val task = serviceTask.task
//        task.taskCallback = object : Task.Callback {
//            override fun onCompleted(cancelled: Boolean) {
//                callback.onCompleted(serviceTask.commandError, cancelled)
//            }
//        }
//        taskProvider.addTask(task)
//    }

    override fun <T : ServiceCommand<*>> cancel(command: T) {
        val serviceTask = command as ServiceTask<*>
        taskManager.cancel(serviceTask.task, null)
    }

    init {
        taskProvider = StackTaskProvider(true, id, taskManager.threadRunner)
        taskManager.addTaskProvider(taskProvider)
    }
}