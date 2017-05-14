import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by alexeyglushkov on 13.08.16.
 */

@RunWith(AndroidJUnit4.class)
public class StackTaskProviderWithDependentTasksTest {
    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private StackTaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskProvider = new StackTaskProvider(true, new Handler(Looper.myLooper()), "TestId");

                poolTest = new TaskPoolTest();
                providerTest = new TaskProviderTest();

                poolTest.before(taskProvider);
                providerTest.before(taskProvider);
            }
        });
    }

    // StackTaskProviderTests

    @Test
    public void testAddedIsTriggeredWhenTaskIsFinished() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                addedIsTriggeredWhenTaskIsFinished();
            }
        });

    }

    private void addedIsTriggeredWhenTaskIsFinished() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);
        TaskPool.TaskPoolListener listener = Mockito.mock(TaskPool.TaskPoolListener.class);

        // Act
        providerMock.addListener(listener);
        providerMock.addTask(testTask1);
        providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);

        // Verify
        Mockito.verify(listener, Mockito.never()).onTaskAdded(providerMock, testTask2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        Mockito.verify(listener).onTaskAdded(providerMock, testTask2);
        assertEquals(1, taskProvider.getTaskCount());
    }

    @Test
    public void testTakeTopTaskIsEmptyIfBlocked() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                takeTopTaskIsEmptyIfBlocked();
            }
        });
    }

    private void takeTopTaskIsEmptyIfBlocked() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);

        // Act
        providerMock.addTask(testTask1);
        Task task1 = providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);
        Task task2 = providerMock.takeTopTask(null);

        // Verify
        assertEquals(testTask1, task1);
        assertNull(task2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        assertEquals(1, taskProvider.getTaskCount());
    }

    @Test
    public void testGetTopTaskIsEmptyIfBlocked() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getTopTaskIsEmptyIfBlocked();
            }
        });

    }

    private void getTopTaskIsEmptyIfBlocked() {
        // Arrange
        TestTask testTask1 = new TestTask();
        TestTask testTask2 = new TestTask();
        StackTaskProvider providerMock = Mockito.spy(taskProvider);

        // Act
        providerMock.addTask(testTask1);
        Task task1 = providerMock.getTopTask(null);
        providerMock.takeTopTask(null);

        providerMock.addTask(testTask2);
        Task task2 = providerMock.getTopTask(null);

        // Verify
        assertEquals(testTask1, task1);
        assertNull(task2);
        testTask1.getPrivate().setTaskStatus(Task.Status.Finished);

        assertEquals(1, taskProvider.getTaskCount());
    }

    // ProviderTests

    @Test
    public void testGetTopTaskWithBlockedTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.getTopTaskWithBlockedTask();
            }
        });
    }

    @Test
    public void testTakeTopTaskWithBlockedTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.takeTopTaskWithBlockedTask();
            }
        });
    }

    @Test
    public void testSetProviderId() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.setProviderId();
            }
        });
    }

    @Test
    public void testSetPriority() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.setPriority();
            }
        });
    }

    @Test
    public void testGetTopTaskWithoutFilter() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.getTopTaskWithoutFilter();
            }
        });
    }

    @Test
    public void testGetTopTaskWithFilter() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.getTopTaskWithFilter();
            }
        });
    }

    @Test
    public void testTakeTopTaskWithFilter() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.takeTopTaskWithFilter();
            }
        });
    }

    @Test
    public void testRemoveTaskWithUnknownType() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                providerTest.removeTaskWithUnknownType();
            }
        });
    }

    // PoolTests

    @Test
    public void testSetGetHandler() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.setGetHandler();
            }
        });
    }

    @Test
    public void testAddTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.addTask();
            }
        });
    }

    @Test
    public void testAddStartedTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.addStartedTask();
            }
        });
    }

    @Test
    public void testRemoveTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.removeTask();
            }
        });
    }

    @Test
    public void testRemoveUnknownTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.removeUnknownTask();
            }
        });
    }

    @Test
    public void testGetTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.getTask();
            }
        });
    }

    @Test
    public void testGetUnknownTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.getUnknownTask();
            }
        });
    }

    @Test
    public void testGetTaskCount() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.getTaskCount();
            }
        });
    }

    @Test
    public void testGetTaskCount2() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.getTaskCount2();
            }
        });
    }

    @Test
    public void testSetGetUserData() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.setGetUserData();
            }
        });
    }

    @Test
    public void testAddStateListener() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.addStateListener();
            }
        });
    }

    @Test
    public void testRemoveStateListener() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.removeStateListener();
            }
        });
    }

    @Test
    public void testChangeTaskStatus() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.changeTaskStatus();
            }
        });
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                poolTest.checkTaskRemovingAfterFinishing();
            }
        });
    }
}
