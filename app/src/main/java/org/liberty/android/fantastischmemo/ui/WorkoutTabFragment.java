package org.liberty.android.fantastischmemo.ui;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.utils.DatabaseUtil;
import org.liberty.android.fantastischmemo.utils.RecentListActionModeUtil;
import org.liberty.android.fantastischmemo.utils.WorkOutListUtil;



import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Created by User on 2018-03-11.
 */

public class WorkoutTabFragment extends BaseFragment {

    private RecyclerView recentListRecyclerView;

    private final AtomicInteger workoutListVersion = new AtomicInteger(0);
    private final static String TAG = WorkoutTabFragment.class.getSimpleName();

    private int colorIndex = 0;
    private String[] colors;
    private HashMap<String,int[]> dbPathColored = new HashMap<>();
    private BaseActivity mActivity;
    WorkoutListAdapter woAdapter;
    @Inject WorkOutListUtil workoutList;

    @Inject DatabaseUtil databaseUtil;

    @Inject RecentListActionModeUtil recentListActionModeUtil;




    public WorkoutTabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RecentListLoaderCallbacks().onCreateLoader(1, savedInstanceState);
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recent_list, container, false);

       colors = getContext().getResources().getStringArray(R.array.color_materials);

        recentListRecyclerView = (RecyclerView) v.findViewById(R.id.recent_open_list);
        recentListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recentListRecyclerView.setLayoutManager(new LinearLayoutManager(recentListRecyclerView.getContext()));
        woAdapter = new WorkoutListAdapter(workoutList);


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
        public int colorId1;
        public int colorId2;
        public TimeInterpolator interpolator;
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
            if(!dbPathColored.containsKey(FilenameUtils.getName(allPath[i]))) {
                assignColor(ri);
            }
            else{
                int temp[] = new int[2];
                for(int temp_index = 0; temp_index < 2 ; temp_index++){
                    temp[temp_index] = dbPathColored.get(FilenameUtils.getName(allPath[i]))[temp_index];
                }
                ri.colorId1 = temp[0];
                ri.colorId2 = temp[1];
            }
            ri.interpolator = Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR);


        }
        return ril;
    }

    private void assignColor(WorkoutItem ri){
        int temp[] = new int[2];
        if (colorIndex >= colors.length) {
            colorIndex = colorIndex - colors.length;
        }
        if((colorIndex + 1)==colors.length){
            ri.colorId1 = Color.parseColor(colors[colorIndex]);
            ri.colorId1 = Color.parseColor(colors[0]);
            temp[0] = Color.parseColor(colors[colorIndex]);
            temp[1] = Color.parseColor(colors[0]);
            colorIndex = 1;
        }else{
            ri.colorId1 = Color.parseColor(colors[colorIndex]);
            ri.colorId2 = Color.parseColor(colors[colorIndex + 1]);
            temp[0] = Color.parseColor(colors[colorIndex]);
            temp[1] = Color.parseColor(colors[colorIndex + 1]);
            colorIndex = colorIndex + 2;
        }
        dbPathColored.put(FilenameUtils.getName(ri.dbPath), temp);
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
                Log.e(TAG, "Workout list throws exception (Usually can be safely ignored)", e);
            }
        }

        return ril;
    }

    private  class WorkoutListAdapter extends RecyclerSwipeAdapter<WorkoutListAdapter.ViewHolder> {

        private List<WorkoutItem> workout = new ArrayList<>();

        private  Context context;
        private SparseBooleanArray expandState = new SparseBooleanArray();
        private WorkOutListUtil woUtil;

        public WorkoutListAdapter(WorkOutListUtil woUtil) {

            this.woUtil = woUtil;
        }
        public  class ViewHolder extends RecyclerView.ViewHolder {

            private TextView filenameView;
            private TextView insideView;
            public RelativeLayout buttonLayout;
            public ExpandableLinearLayout expandableLayout;

            SwipeLayout swipeLayout;
            TextView textViewPos;
            TextView textViewData;
            Button buttonDelete;
            Button startWorkout;


            public ViewHolder(View view) {
                super(view);

                filenameView = (TextView) view.findViewById(R.id.textView);
                buttonLayout = (RelativeLayout) view.findViewById(R.id.button);
                insideView = (TextView) view.findViewById(R.id.insideView);
                expandableLayout = (ExpandableLinearLayout) view.findViewById(R.id.expandableLayout);
                startWorkout = (Button) view.findViewById(R.id.start_workout);

                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
                buttonDelete = (Button) itemView.findViewById(R.id.delete);

            }
            public void setItem(WorkoutItem item) {
                //filenameView.setText(item.dbName);
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.context = parent.getContext();
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.recycler_view_list_row, parent, false);


            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final WorkoutItem item = workout.get(position);

            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                holder.filenameView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
            @Override
            public void onOpen(SwipeLayout layout) {
                holder.filenameView.setVisibility(View.INVISIBLE);
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
             holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(holder.swipeLayout);
                String dbName = workout.get(position).dbPath;
                woUtil.deleteFromWorkoutList(dbName);
                workout.remove(position);

                notifyDataSetChanged();

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, workout.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + dbName + "!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.startWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("START WORKOUT", "CLICKED START WORKOUT");
                Intent myIntent = new Intent();
                myIntent.setClass(getActivity(), StudyActivity.class);
                myIntent.putExtra(StudyActivity.EXTRA_DBPATH, item.dbPath);
                myIntent.putExtra(StudyActivity.WORKOUT_MODE, true);
                startActivity(myIntent);
            }
        });
       // holder.textViewPos.setText((position + 1) + ".");
        mItemManger.bindView(holder.itemView, position);

            holder.setIsRecyclable(false);
            holder.filenameView.setText(item.dbName);
            holder.insideView.setText("DbInfo " + item.dbInfo + "\n DbPath " + item.dbPath );
            holder.itemView.setBackgroundColor(item.colorId1);
            holder.expandableLayout.setInRecyclerView(true);
            holder.expandableLayout.setBackgroundColor(item.colorId2);
            holder.expandableLayout.setInterpolator(item.interpolator);
            holder.expandableLayout.setExpanded(expandState.get(position));
            holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                    expandState.put(position, true);
                }

                @Override
                public void onPreClose() {
                    createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                    expandState.put(position, false);
                }
            });

            holder.buttonLayout.setRotation(expandState.get(position) ? 180f : 0f);
            holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onClickButton(holder.expandableLayout);
                }
            });

        }

        private void onClickButton(final ExpandableLayout expandableLayout) {
            expandableLayout.toggle();
        }
        public synchronized void setItems(List<WorkoutItem> items) {

            for (int i = 0; i < items.size(); i++) {
                expandState.append(i, false);

            }
            this.workout.clear();
            this.workout.addAll(items);
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return workout.size();
        }

        public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
            return animator;
        }

        @Override

        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

    }

}