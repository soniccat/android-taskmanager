import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskPool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 08.08.15.
 */

@RunWith(AndroidJUnit4.class)
public class SimpleTaskPoolTest extends TaskPoolTest {

    @Before
    public void setUp() throws Exception{
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.before(new SimpleTaskPool(new Handler(Looper.myLooper())));
            }
        });
    }

    @Test
    public void testSetGetHandler() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.setGetHandler();
            }
        });
    }

    @Test
    public void testAddTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.addTask();
            }
        });
    }

    @Test
    public void testAddStartedTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.addStartedTask();
            }
        });
    }

    @Test
    public void testRemoveTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.removeTask();
            }
        });
    }

    @Test
    public void testRemoveUnknownTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.removeUnknownTask();
            }
        });
    }

    @Test
    public void testGetTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.getTask();
            }
        });
    }

    @Test
    public void testGetUnknownTask() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.getUnknownTask();
            }
        });
    }

    @Test
    public void testGetTaskCount() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.getTaskCount();
            }
        });
    }

    @Test
    public void testGetTaskCount2() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.getTaskCount2();
            }
        });
    }

    @Test
    public void testSetGetUserData() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.setGetUserData();
            }
        });
    }

    @Test
    public void testAddStateListener() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.addStateListener();
            }
        });
    }

    @Test
    public void testRemoveStateListener() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.removeStateListener();
            }
        });
    }

    @Test
    public void testChangeTaskStatus() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.changeTaskStatus();
            }
        });
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                SimpleTaskPoolTest.this.checkTaskRemovingAfterFinishing();
            }
        });
    }
}
