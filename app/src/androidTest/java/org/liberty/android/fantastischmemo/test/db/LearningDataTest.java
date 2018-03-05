package org.liberty.android.fantastischmemo.test.db;

/**
 * Created by Wei on 3/5/2018.
 */
import android.graphics.Color;
import android.support.test.filters.SmallTest;

import org.junit.Test;
import org.liberty.android.fantastischmemo.dao.LearningDataDao;
import org.liberty.android.fantastischmemo.entity.LearningData;
import org.liberty.android.fantastischmemo.test.AbstractExistingDBTest;

import java.sql.SQLException;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
public class LearningDataTest extends AbstractExistingDBTest{
    @SmallTest
    @Test
    public void testFavourite() throws Exception {
        LearningData learningData = getCurrentLearningData();
        learningData.setFavourite(1);
        LearningData savedLearningData = setLearningData(learningData);
        assertEquals(1, (int)savedLearningData.getFavourite());
    }

    private LearningData getCurrentLearningData() throws SQLException {
        LearningDataDao learningDataDao = helper.getLearningDataDao();
        return learningDataDao.queryForId(1);
    }

    // Return the saved setting.
    private LearningData setLearningData(LearningData learningData) throws SQLException {
        LearningDataDao learningDataDao = helper.getLearningDataDao();
        learningDataDao.update(learningData);
        return learningDataDao.queryForId(1);
    }
}
