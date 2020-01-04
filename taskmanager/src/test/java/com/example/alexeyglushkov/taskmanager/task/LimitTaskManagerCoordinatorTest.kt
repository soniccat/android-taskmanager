package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.taskmanager.coordinators.LimitTaskManagerCoordinator
import com.example.alexeyglushkov.taskmanager.runners.ScopeThreadRunner
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test

open class LimitTaskManagerCoordinatorTest {

    lateinit var coordinator: LimitTaskManagerCoordinator

    @Before
    @Throws(Exception::class)
    fun setUp() {
        coordinator = LimitTaskManagerCoordinator(10)
        coordinator.threadRunner = ScopeThreadRunner(TestCoroutineScope(), "SimpleTaskManagerScopeTheadId")
    }

    @Test
    fun setMaxLoadingTasks() {
        // Act
        coordinator.maxLoadingTasks = 100

        // Verify
        Assert.assertEquals(100, coordinator.maxLoadingTasks)
    }

    @Test
    fun setLimit() {
        val listener = mock<LimitTaskManagerCoordinator.Listener>()

        // Act
        coordinator.addListener(listener)
        coordinator.setLimit(1, 0.5f)

        // Verify
        verify(listener).onLimitsChanged(coordinator, 1, 0.5f)
        Assert.assertEquals(0.5f, coordinator.limits.get(1)!!, 0.001f)

    }

    @Test
    fun setLimitRemove() {
        // Act
        coordinator.setLimit(1, 0.5f)
        coordinator.setLimit(1, 0.0f)

        // Verify
        Assert.assertNull(coordinator.limits.get(1))
    }
}