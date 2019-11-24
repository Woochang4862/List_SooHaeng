package com.jeongwoochang.list_soohaeng;

import android.content.Context;

import com.jeongwoochang.list_soohaeng.Model.DBAdapter;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        DBAdapter dbAdapter = DBAdapter.getInstance();
        DBAdapter.connect(appContext);

        dbAdapter.addTest(new com.jeongwoochang.list_soohaeng.Model.Schema.Test(
                1,
               "test",
                "test",
                DateTime.now(),
                null,
                new DateTime().plusDays(1).millisOfSecond().get())
        );

        assertEquals(dbAdapter.getTest(1).size(), appContext.getPackageName());
        dbAdapter.close();
    }
}
