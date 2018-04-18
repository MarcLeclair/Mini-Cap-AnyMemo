package org.liberty.android.fantastischmemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AMPrefKeys;
import org.liberty.android.fantastischmemo.ui.FileBrowserFragment;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by User on 2018-04-18.
 */

public class DbBrowser {


    public final static String EXTRA_DEFAULT_ROOT = "default_root";
    public final static String EXTRA_FILE_EXTENSIONS = "file_extension";
    public final static String EXTRA_DISMISS_ON_SELECT = "dismiss_on_select";
    public final static String EXTRA_SHOW_CREATE_DB_BUTTON = "show_create_db_button";

    private enum DISPLAYMODE{ABSOLUTE, RELATIVE;}
    private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
    private ArrayList<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = new File("/");
    private String defaultRoot;
    private String[] fileExtensions;


    private final static String UP_ONE_LEVEL_DIR = "..";
    private final static String CURRENT_DIR = ".";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    public DbBrowser(Activity mActivity){

        settings = PreferenceManager.getDefaultSharedPreferences(mActivity.getBaseContext());
        fileExtensions = new String[]{".db"};
        defaultRoot = settings.getString(AMPrefKeys.SAVED_FILEBROWSER_PATH_KEY, null);
        if (!Strings.isNullOrEmpty(defaultRoot) && !new File(defaultRoot).exists()) {
            defaultRoot = null;
        }

    }
    public ArrayList<String>  browseTo( File aDirectory){
        aDirectory =new File(defaultRoot + aDirectory.getAbsolutePath());
        if(aDirectory.isDirectory()){
            File[] listedFiles = aDirectory.listFiles();
            if (listedFiles != null) {
                aDirectory.getPath();
                this.currentDirectory = aDirectory;
                return fill(listedFiles);
            }
        }
        return new ArrayList<String>();
    }

    private ArrayList<String> fill(File[] files){
        this.directoryEntries.clear();

        if(this.currentDirectory.getParent() != null){
            this.directoryEntries.add(UP_ONE_LEVEL_DIR);
        }
        switch(this.displayMode){
            case ABSOLUTE:
                for(File file : files){
                    this.directoryEntries.add(file.getPath());
                }
                break;
            case RELATIVE:
                int currentPathStringLength = this.currentDirectory.getAbsolutePath().length();
                for(File file: files){
                    if(file.isDirectory()){
                        this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLength) + "/");
                    }
                    else{
                        for(String fileExtension : fileExtensions){
                            if(file.getName().toLowerCase().endsWith(fileExtension.toLowerCase())){
                                this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLength));
                            }
                        }
                    }

                }
        }
        return this.directoryEntries;
    }


    private void upOneLevel(){
        if(this.currentDirectory.getParent() != null){
            this.browseTo(this.currentDirectory.getParentFile());
        }
    }
}
