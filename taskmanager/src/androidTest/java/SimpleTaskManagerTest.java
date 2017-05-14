import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 23.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleTaskManagerTest {

    private TaskPoolTest poolTest;
    private TaskManagerTest taskManagerTest;
    private TaskManager taskManager;

    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManager = new SimpleTaskManager(10, new Handler(Looper.myLooper()));
                poolTest = new TaskPoolTest();
                taskManagerTest = new TaskManagerTest();

                poolTest.before(taskManager);
                taskManagerTest.before(taskManager);
            }
        });
    }

    @Test
    public void testSetMaxLoadingTasks() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.setMaxLoadingTasks();
            }
        });
    }

    @Test
    public void testGetLoadingTaskCount() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.getLoadingTaskCount();
            }
        });
    }

    @Test
    public void testAddTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTask();
            }
        });
    }

    @Test
    public void testAddStartedTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addStartedTask();
            }
        });
    }

    @Test
    public void testAddTheSameTaskWithSkipPolicy() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTheSameTaskWithSkipPolicy();
            }
        });
    }

    @Test
    public void testAddTheSameTaskWithCancelPolicy() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTheSameTaskWithCancelPolicy();
            }
        });
    }

    @Test
    public void testAddStateListener() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addStateListener();
            }
        });
    }

    @Test
    public void testRemoveStateListener() {
        taskManagerTest.removeStateListener();
    }

    @Test
    public void testRemoveTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.removeTask();
            }
        });
    }

    @Test
    public void testRemoveUnknownTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.removeUnknownTask();
            }
        });
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.checkTaskRemovingAfterFinishing();
            }
        });
    }

    @Test
    public void testGetTaskFromProvider() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.getTaskFromProvider();
            }
        });
    }

    @Test
    public void testAddTaskProvider() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTaskProvider();
            }
        });
    }

    @Test
    public void testAddTaskProvider2() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTaskProvider2();
            }
        });
    }

    @Test
    public void testAddTaskProviderWithTheSameId() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.addTaskProviderWithTheSameId();
            }
        });
    }

    @Test
    public void testRemoveTaskProvider() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.removeTaskProvider();
            }
        });
    }

    @Test
    public void testSetTaskExecutor() {
        taskManagerTest.setTaskExecutor();
    }

    @Test
    public void testStartImmediately() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.startImmediately();
            }
        });
    }

    @Test
    public void testStartImmediatelySkipPolicy() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.startImmediatelySkipPolicy();
            }
        });
    }

    @Test
    public void testStartImmediatelyFinish() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.startImmediatelyFinish();
            }
        });
    }

    @Test
    public void testSetTaskProviderPriority() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.setTaskProviderPriority();
            }
        });
    }

    @Test
    public void testSetGetHandler() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.setGetHandler();
            }
        });
    }

    @Test
    public void testGetTaskCount() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.getTaskCount();
            }
        });
    }

    @Test
    public void testGetTasks() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.getTasks();
            }
        });
    }

    @Test
    public void testSetLimit() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.setLimit();
            }
        });
    }

    @Test
    public void testSetLimitRemove() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                taskManagerTest.setLimitRemove();
            }
        });
    }

    // PoolTests

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
    public void testSetGetUserData() {
        poolTest.setGetUserData();
    }
}
