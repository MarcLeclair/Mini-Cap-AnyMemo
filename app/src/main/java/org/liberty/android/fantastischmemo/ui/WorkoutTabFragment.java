package org.liberty.android.fantastischmemo.ui;

import android.support.v7.widget.RecyclerView;

import org.liberty.android.fantastischmemo.common.BaseFragment;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by User on 2018-03-11.
 */

public class WorkoutTabFragment extends BaseFragment {

    private RecyclerView recentListRecyclerView;

    private final AtomicInteger workoutList = new AtomicInteger(0);
    private final static String TAG = WorkoutTabFragment.class.getSimpleName();
}
