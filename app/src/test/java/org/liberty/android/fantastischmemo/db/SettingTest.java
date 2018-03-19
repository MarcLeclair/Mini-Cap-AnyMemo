package org.liberty.android.fantastischmemo.db;

/**
 * Created by Wei on 3/18/2018.
 */
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.entity.Setting;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class SettingTest {
    private final Setting setting = new Setting();

    String TAG = "SettingTest.java";

    @SmallTest
    @Test
    public void testDefaults() {
        assertEquals(setting.getLearningMode(),(Integer)0);
    }

    @SmallTest
    @Test
    public void testLearningMode() throws Exception {
        setting.setLearningMode(0);
        assertEquals((Integer) 0, setting.getLearningMode());
    }

}
