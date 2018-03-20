package org.liberty.android.fantastischmemo.db;

/**
 * Created by Wei on 3/18/2018.
 */
import android.test.suitebuilder.annotation.SmallTest;
<<<<<<< HEAD

=======
import org.junit.After;
import org.junit.Before;
>>>>>>> 05983955cbfbbf73dee502a745378103ab5a3ba3
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liberty.android.fantastischmemo.entity.Setting;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class SettingTest {
    private Setting setting;

    @Before
    public void setUp() {
        setting = new Setting();
    }


<<<<<<< HEAD
=======
    @After
    public void tearDown() {
        setting = null;
    }

>>>>>>> 05983955cbfbbf73dee502a745378103ab5a3ba3
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
