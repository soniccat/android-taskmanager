import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.assertEquals;

import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Created by alexeyglushkov on 09.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class PriorityTaskProviderTest {

    private TaskPoolTest poolTest;
    private TaskProviderTest providerTest;
    private PriorityTaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskProvider = new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId");

                poolTest = new TaskPoolTest();
                providerTest = new TaskProviderTest();

                poolTest.before(taskProvider);
                providerTest.before(taskProvider);
            }
        });
    }

    // PriorityTaskProviderTests

    @Test
    public void testUpdatePriorities() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                updatePriorities();
            }
        });
    }

    private void updatePriorities() {
        // Arrange
        TaskProvider.TaskPoolListener listener = Mockito.mock(TaskProvider.TaskPoolListener.class);

        taskProvider.addListener(listener);
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        taskProvider.updatePriorities(new PriorityTaskProvider.PriorityProvider() {
            @Override
            public int getPriority(Task task) {
                if (task.getTaskType() == 2) {
                    return 2 * task.getTaskPriority();
                }
                return task.getTaskPriority();
            }
        });

        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("d", task.getTaskId());
    }

    @Test
    public void testTopTaskWithPriorityWithoutFilter() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                topTaskWithPriorityWithoutFilter();

            }
        });
    }

    private void topTaskWithPriorityWithoutFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 1, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 2, 6));

        // Act
        Task task = taskProvider.getTopTask(null);

        // Verify
        assertEquals("f", task.getTaskId());
    }

    @Test
    public void testGetTopTaskWithPriorityWithFilter() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getTopTaskWithPriorityWithFilter();

            }
        });
    }

    private void getTopTaskWithPriorityWithFilter() {
        // Arrange
        taskProvider.addTask(TestTasks.createTestTaskSpy("a", 1, 1));
        taskProvider.addTask(TestTasks.createTestTaskSpy("b", 2, 2));
        taskProvider.addTask(TestTasks.createTestTaskSpy("c", 3, 3));
        taskProvider.addTask(TestTasks.createTestTaskSpy("d", 2, 4));
        taskProvider.addTask(TestTasks.createTestTaskSpy("e", 1, 5));
        taskProvider.addTask(TestTasks.createTestTaskSpy("f", 3, 6));

        // Act
        Task task = taskProvider.getTopTask(Arrays.asList(new Integer[]{3}));

        // Verify
        assertEquals("e", task.getTaskId());
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
