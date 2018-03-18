package org.liberty.android.fantastischmemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.apache.commons.io.FilenameUtils;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.ui.helper.SelectableAdapter;
import org.liberty.android.fantastischmemo.utils.DatabaseUtil;
import org.liberty.android.fantastischmemo.utils.RecentListActionModeUtil;
import org.liberty.android.fantastischmemo.utils.WorkOutListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by User on 2018-03-11.
 */

public class WorkoutTabFragment extends BaseFragment {

    private RecyclerView recentListRecyclerView;

    private final AtomicInteger workoutListVersion = new AtomicInteger(0);
    private final static String TAG = WorkoutTabFragment.class.getSimpleName();

    private WorkoutListAdapter woAdapter;

    @Inject WorkOutListUtil workoutList;

    @Inject DatabaseUtil databaseUtil;

    @Inject RecentListActionModeUtil recentListActionModeUtil;



    private BroadcastReceiver mRemoveSelectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            woAdapter.removeSelected();
        }
    };

    private BroadcastReceiver mClearSelectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            woAdapter.deselectAll();
        }
    };

    public WorkoutTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponents().inject(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.open_screen_menu, menu);
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (woAdapter != null && !isVisibleToUser) {
            woAdapter.stopActionMode();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_list, container, false);
        recentListRecyclerView = (RecyclerView) v.findViewById(R.id.recent_open_list);
        //RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(v.getContext());
        //recentListRecyclerView.addItemDecoration(dividerItemDecoration);

        recentListRecyclerView.setLayoutManager(new LinearLayoutManager(recentListRecyclerView.getContext()));

        /* pre loading stat */
        woAdapter = new WorkoutListAdapter(getContext(), workoutList, recentListActionModeUtil);

        recentListRecyclerView.setAdapter(woAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(1, null, new RecentListLoaderCallbacks());
        getLoaderManager().restartLoader(2, null, new RecentListDetailLoaderCallbacks());
    }

    private class RecentListLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<WorkoutItem>> {

        private int loadedVersion;

        @Override
        public Loader<List<WorkoutItem>> onCreateLoader(int id, Bundle args) {
            Loader<List<WorkoutItem>> loader = new AsyncTaskLoader<List<WorkoutItem>>(getContext()) {
                @Override
                public List<WorkoutItem> loadInBackground() {
                    loadedVersion = workoutListVersion.get();
                    return loadRecentItemsWithName();
                }
            };
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<WorkoutItem>> loader, List<WorkoutItem> ril) {
            if (workoutListVersion.get() == loadedVersion) {
                woAdapter.setItems(ril);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<WorkoutItem>> loader) {
            // Nothing
        }
    }
    private class RecentListDetailLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<WorkoutItem>> {

        private int loadedVersion;

        @Override
        public Loader<List<WorkoutItem>> onCreateLoader(int id, Bundle args) {
            Loader<List<WorkoutItem>> loader = new AsyncTaskLoader<List<WorkoutItem>>(getContext()) {
                @Override
                public List<WorkoutItem> loadInBackground() {
                    loadedVersion = workoutListVersion.get();
                    return loadRecentItemsWithDetails();
                }
            };
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<WorkoutItem>> loader, List<WorkoutItem> ril) {
            // Make sure the recentListVersion is updated so the previous running loadRecentItemsWithNames will fail.
            // Also if multiple loadRecentItemsWithDetails, only one succeeded
            if (workoutListVersion.get() == loadedVersion) {
                woAdapter.setItems(ril);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<WorkoutItem>> loader) {
            // Nothing
        }
    }
    private static class WorkoutItem {
        public String dbName;
        public String dbPath;
        public String dbInfo;
        public int index;
    }


    private List<WorkoutItem> loadRecentItemsWithName() {
        String[] allPath = workoutList.getAllRecentDBPath();
        final List<WorkoutItem> ril = new ArrayList<WorkoutItem>();
        int index = 0;
        for(int i = 0; i < allPath.length; i++){
            if(allPath[i] == null){
                continue;
            }
            final WorkoutItem ri = new WorkoutItem();
            if (!databaseUtil.checkDatabase(allPath[i])) {
                workoutList.deleteFromWorkoutList(allPath[i]);
                continue;
            }

            ri.dbInfo = getContext().getString(R.string.loading_database);
            ri.index = index++;
            ril.add(ri);
            ri.dbPath = allPath[i];
            ri.dbName = FilenameUtils.getName(allPath[i]);
        }
        return ril;
    }

    private List<WorkoutItem> loadRecentItemsWithDetails() {
        final List<WorkoutItem> ril = loadRecentItemsWithName();
        for (final WorkoutItem ri : ril){
            try {
                Context context = getContext();
                if (context == null) {
                    break;
                }
                AnyMemoDBOpenHelper helper = AnyMemoDBOpenHelperManager.getHelper(getContext(), ri.dbPath);
                CardDao dao = helper.getCardDao();
                ri.dbInfo = context.getString(R.string.stat_total) + dao.getTotalCount(null) + " " +
                        getContext().getString(R.string.stat_new) + dao.getNewCardCount(null) + " " +
                        getContext().getString(R.string.stat_scheduled)+ dao.getScheduledCardCount(null);
                ril.set(ri.index, ri);
                AnyMemoDBOpenHelperManager.releaseHelper(helper);
            } catch (Exception e) {
                Log.e(TAG, "Recent list throws exception (Usually can be safely ignored)", e);
            }
        }

        return ril;
    }

    private static class WorkoutListAdapter extends SelectableAdapter<WorkoutListAdapter.ViewHolder> {

        private List<WorkoutItem> workout =new ArrayList<>();

        private RecentListActionModeUtil recentListActionModeUtil;
        private final Context context;

        private final WorkOutListUtil workoutUtil;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView filenameView;
            private TextView infoView;
            private TextView dayStudy;
            private Button moreButton;
            private View selectedOverlay;

            public ViewHolder(View view) {
                super(view);
                filenameView = (TextView) view.findViewById(R.id.workout_db_name);
                dayStudy = (TextView) view.findViewById(R.id.workout_day);
                infoView = (TextView) view.findViewById(R.id.statistcs);
                moreButton = (Button) view.findViewById(R.id.recent_item_more_button);
                selectedOverlay = (View) view.findViewById(R.id.selected_overlay);
            }

            public void setItem(WorkoutItem item) {
                filenameView.setText(item.dbName);
                infoView.setText(item.dbInfo);
            }
        }

        public WorkoutListAdapter(Context context,
                                 WorkOutListUtil workoutListUtil, RecentListActionModeUtil recentListActionModeUtil) {
            this.context = context;
            this.workoutUtil = workoutListUtil;
            this.recentListActionModeUtil = recentListActionModeUtil;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.workout_item, parent, false);


            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final WorkoutItem currentItem = workout.get(position);
            holder.setItem(currentItem);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSelectedItemCount() == 0) {
                        Intent myIntent = new Intent();
                        myIntent.setClass(context, StudyActivity.class);
                        String dbPath = currentItem.dbPath;
                        myIntent.putExtra(StudyActivity.EXTRA_DBPATH, dbPath);
                        workoutUtil.addToRecentList(dbPath);
                        context.startActivity(myIntent);
                    } else {
                        toggleSelection(position);
                    }
                   //TODO recentListActionModeUtil.updateActionMode(getSelectedItemCount());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getSelectedItemCount() == 0) {
                        //TODO recentListActionModeUtil.startActionMode();
                    }
                    toggleSelection(position);
                   //TODO recentListActionModeUtil.updateActionMode(getSelectedItemCount());
                    return true;
                }
            });

            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dbPath = currentItem.dbPath;
                    DialogFragment df = new OpenActionsFragment();
                    Bundle b = new Bundle();
                    b.putString(OpenActionsFragment.EXTRA_DBPATH, dbPath);
                    df.setArguments(b);
                    df.show(((FragmentActivity) context).getSupportFragmentManager(), "OpenActions");
                }
            });

            holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        }
        @Override public int getItemCount() {
            return workout.size();
        }

        public synchronized void setItems(List<WorkoutItem> items) {
            this.workout.clear();
            this.workout.addAll(items);
            this.notifyDataSetChanged();
        }

       public void stopActionMode() {
            recentListActionModeUtil.stopActionMode();
        }

        public void removeSelected() {
            final List<Integer> indices = getSelectedItems();
            final List<WorkoutItem> itemsToRemove = new ArrayList<>();
            for (Integer i:indices) {
                final WorkoutItem currentItem = this.workout.get(i);
                workoutUtil.deleteFromWorkoutList(currentItem.dbPath);
                itemsToRemove.add(currentItem);
            }
            this.workout.removeAll(itemsToRemove);

            this.notifyDataSetChanged();
        }


    }
}
