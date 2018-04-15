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

@PerApplication
public class TagsListUtil {
    private int recentLength = 7;

    private Option option;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private SharedPreferencesHelper helper;

    @Inject
    public TagsListUtil(@ForApplication Context context, Option option){
        helper = new SharedPreferencesHelper (context, "tagslist");
        recentLength = option.getTagsCount();
        this.option = option;
    }

    public String getTagsDBPath(){
        return trimPath(helper.getString(AMPrefKeys.getTagsCountKey(0), null));
    }

    public String[] getAllRecentDBPath(){
        recentLength = option.getTagsCount();

        String[] ret = new String[recentLength];

        for(int i = 0; i < recentLength; i++){
            ret[i] = trimPath(helper.getString(AMPrefKeys.getTagsCountKey(i), null));
        }

        return ret;
    }

    public void clearTagsList(){
        for (int i = 0; i < recentLength; i++){
            helper.putString(AMPrefKeys.getTagsCountKey(i), null);
        }
    }

    public void deleteFromTagsList(String dbpath){
        dbpath = trimPath(dbpath);
        String[] allPaths = getAllRecentDBPath();
        clearTagsList();
        for(int i = 0, counter = 0; i < recentLength; i++){
            if(allPaths[i] == null || allPaths[i].equals(dbpath)) {
                continue;
            } else {
                helper.putString(AMPrefKeys.getTagsCountKey(counter), allPaths[i]);
                counter++;
            }
        }
    }

    public void addToTagsList(String dbpath){
        dbpath = trimPath(dbpath);
        deleteFromTagsList(dbpath);
        String[] allPaths = getAllRecentDBPath();
        for(int i = recentLength - 1; i >= 1; i--){
            helper.putString(AMPrefKeys.getWorkoutCountKey(i), allPaths[i-1]);
        }
        helper.putString(AMPrefKeys.getTagsCountKey(0), dbpath);
    }

    private static String trimPath(String path){
        return FilenameUtils.normalize(path);
    }
}
