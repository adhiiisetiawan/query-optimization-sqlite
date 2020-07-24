package com.example.queryoptimization.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private static final String PREF_NAME = "mahasiswa_pref";
    private static final String APP_FIRST_RUN = "app_first_run";
    private SharedPreferences preferences;

    public AppPreference(Context context){
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setFirstRun(Boolean input){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(APP_FIRST_RUN, input);
        editor.apply();
    }

    public boolean getFristRun(){
        return preferences.getBoolean(APP_FIRST_RUN, true);
    }
}
