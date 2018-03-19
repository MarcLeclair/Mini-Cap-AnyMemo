package org.liberty.android.fantastischmemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.viralypatel.sharedpreferenceshelper.lib.SharedPreferencesHelper;

import org.apache.commons.io.FilenameUtils;
import org.liberty.android.fantastischmemo.common.AMPrefKeys;
import org.liberty.android.fantastischmemo.entity.Option;
import org.liberty.android.fantastischmemo.modules.ForApplication;
import org.liberty.android.fantastischmemo.modules.PerApplication;

import javax.inject.Inject;

/**
 * Created by User on 2018-03-16.
 */

@PerApplication
public class WorkOutListUtil {
    private int recentLength = 7;

    private Option option;

    private SharedPreferencesHelper helper;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Inject
    public WorkOutListUtil(@ForApplication Context context, Option option) {

        //SharedPreferencesHelper avoids some boiler plate code like the use of editor and the use of .commit() after doing a transaction with the shared pereference file
        helper = new SharedPreferencesHelper(context, "workoutlist");

        recentLength = option.getWorkoutCount();
        this.option = option;
    }

    public String getWorkoutDbPath() {
        return trimPath(helper.getString(AMPrefKeys.getWorkoutCountKey(0), null));
    }

    public String[] getAllRecentDBPath() {
        // TODO: Reload the recentLength from user option.
        // FIXME: temp hack, need re-write, don't need to get it again.
        recentLength = option.getWorkoutCount();

        String[] ret = new String[recentLength];

        for(int i = 0; i < recentLength; i++){
            ret[i] = trimPath(helper.getString(AMPrefKeys.getWorkoutCountKey(i), null));
        }

        return ret;
    }

    public void clearWorkoutList() {
        for(int i = 0; i < recentLength; i++){
            helper.putString(AMPrefKeys.getWorkoutCountKey(i), null);
        }
    }

    public void deleteFromWorkoutList(String dbpath){
        dbpath = trimPath(dbpath);
        String[] allPaths = getAllRecentDBPath();
        clearWorkoutList();
        for(int i = 0, counter = 0; i < recentLength; i++){
            if(allPaths[i] == null || allPaths[i].equals(dbpath)) {
                continue;
            } else {
                helper.putString(AMPrefKeys.getWorkoutCountKey(counter), allPaths[i]);
                counter++;
            }
        }
    }

    public void addToRecentList(String dbpath){
        dbpath = trimPath(dbpath);
        deleteFromWorkoutList(dbpath);
        String[] allPaths = getAllRecentDBPath();
        for(int i = recentLength - 1; i >= 1; i--){
            helper.putString(AMPrefKeys.getWorkoutCountKey(i), allPaths[i - 1]);
        }
        helper.putString(AMPrefKeys.getWorkoutCountKey(0), dbpath);
    }

    private static String trimPath(String path){
        return FilenameUtils.normalize(path);
    }
}