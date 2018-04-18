package org.liberty.android.fantastischmemo.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.dao.CardDao;
import org.liberty.android.fantastischmemo.utils.DatabaseUtil;
import org.liberty.android.fantastischmemo.utils.RecentListActionModeUtil;
import org.liberty.android.fantastischmemo.utils.TagsListUtil;
import android.support.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class TagsActivity extends BaseActivity {

    public static final String EXTRA_DBPATH = "dbpath";
    private FrameLayout statisticsGraphFrame;
    private CardDao cardDao;
    private AnyMemoDBOpenHelper dbOpenHelper;
    private RecyclerView recentListRecyclerView;

    private final AtomicInteger tagsListVersion = new AtomicInteger(0);
    private final static String TAG = TagsActivity.class.getSimpleName();

    @Inject TagsListUtil tagsList;
    @Inject DatabaseUtil databaseUtil;
    @Inject RecentListActionModeUtil recentListActionModeUtil;

    @Override
    public void onCreate(@Nullable Bundle bundle){
        super.onCreate(bundle);
        //setContentView(R.layout.tags_screen);
        setTitle(R.string.tags_text);

        Bundle extras = getIntent().getExtras();
        String dbPath = extras.getString(EXTRA_DBPATH);
        assert dbPath != null : "dbPath shouldn't be null";

        dbOpenHelper = AnyMemoDBOpenHelperManager.getHelper(this, dbPath);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

}
