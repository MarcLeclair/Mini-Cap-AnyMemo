package org.liberty.android.fantastischmemo.dao;

import org.liberty.android.fantastischmemo.entity.LearningData;

public interface LearningDataDao extends HelperDao<LearningData, Integer> {
    void updateLearningData(LearningData ld);
    void resetLearningData(LearningData ld);
    void resetAllLearningData();
    void markAsLearnedForever(LearningData ld);
    void markAsFavourite(LearningData ld);
}
