package com.example.queryoptimization.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.queryoptimization.R;
import com.example.queryoptimization.database.MahasiswaHelper;
import com.example.queryoptimization.model.Mahasiswa;
import com.example.queryoptimization.preference.AppPreference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DataManagerService extends Service {
    private String TAG = DataManagerService.class.getSimpleName();
    private Messenger messengerActivity;

    public static final int PREPARATION_MESSAGE = 0;
    public static final int UPDATE_MESSAGE = 1;
    public static final int SUCCESS_MESSAGE = 2;
    public static final int FAILED_MESSAGE = 3;
    public static final int CANCEL_MESSAGE = 4;
    public static final String ACTIVITY_HANDLER = "activity_handler";

    private LoadDataAsync loadDataAsync;

    public DataManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        loadDataAsync = new LoadDataAsync(this, myCallback);
        Log.d(TAG, "On Create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadDataAsync.cancel(true);
        Log.d(TAG, "OnDestroy");
    }

    private final LoadDataCallback myCallback = new LoadDataCallback() {
        @Override
        public void onPreLoad() {
            sendMessage(PREPARATION_MESSAGE);
        }

        @Override
        public void onProgressUpdate(long progress) {
            try {
                Message message = Message.obtain(null, UPDATE_MESSAGE);
                Bundle bundle = new Bundle();
                bundle.putLong("KEY_PROGRESS", progress);
                message.setData(bundle);
                messengerActivity.send(message);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadSuccess() {
            sendMessage(SUCCESS_MESSAGE);
        }

        @Override
        public void onLoadFailed() {
            sendMessage(FAILED_MESSAGE);
        }

        @Override
        public void onLoadCancel() {
            sendMessage(CANCEL_MESSAGE);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        messengerActivity = intent.getParcelableExtra(ACTIVITY_HANDLER);
        loadDataAsync.execute();
        return messengerActivity.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        loadDataAsync.cancel(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "OnRebind");
    }

    public static class LoadDataAsync extends AsyncTask<Void, Integer, Boolean>{
        private final String TAG = LoadDataAsync.class.getSimpleName();
        private final WeakReference<Context> contextWeakReference;
        private final WeakReference<LoadDataCallback> loadDataCallbackWeakReference;
        private static final double MAX_PROGRESS = 100;

        LoadDataAsync(Context context, LoadDataCallback callback){
            contextWeakReference = new WeakReference<>(context);
            loadDataCallbackWeakReference = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            loadDataCallbackWeakReference.get().onPreLoad();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            MahasiswaHelper mahasiswaHelper = MahasiswaHelper.getInstance(contextWeakReference.get());
            AppPreference appPreference = new AppPreference(contextWeakReference.get());
            Boolean firstRun = appPreference.getFristRun();

            if (firstRun){
                ArrayList<Mahasiswa> mahasiswaArrayList = preLoadRaw();

                mahasiswaHelper.open();

                double progress = 30;
                publishProgress((int) progress);
                double progressMaxinsert = 80.0;
                double progressDiff = (progressMaxinsert - progress) / mahasiswaArrayList.size();

                boolean isInsertSuccess;

                try {
                    for (Mahasiswa model : mahasiswaArrayList){
                        mahasiswaHelper.insert(model);
                        progress += progressDiff;
                        publishProgress((int) progress);
                    }
                    isInsertSuccess = true;
                    appPreference.setFirstRun(false);
                }catch (Exception e){
                    Log.e(TAG, "doInBackground: Exception");
                    isInsertSuccess = false;
                }

                mahasiswaHelper.close();
                publishProgress((int) MAX_PROGRESS);
                return isInsertSuccess;
            } else {
                try {
                    synchronized (this){
                        this.wait(2000);
                        publishProgress(50);

                        this.wait(2000);
                        publishProgress((int) MAX_PROGRESS);

                        return true;
                    }
                }catch (Exception e){
                    return false;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            loadDataCallbackWeakReference.get().onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                loadDataCallbackWeakReference.get().onLoadSuccess();
            } else {
                loadDataCallbackWeakReference.get().onLoadFailed();
            }
        }


        private ArrayList<Mahasiswa> preLoadRaw(){
            ArrayList<Mahasiswa> mahasiswaArrayList = new ArrayList<>();
            String line;
            BufferedReader reader;
            try {
                InputStream raw = contextWeakReference.get().getResources().openRawResource(R.raw.data_mahasiswa);
                reader = new BufferedReader(new InputStreamReader(raw));
                do {
                    line = reader.readLine();
                    String[] splitstr = line.split("\t");

                    Mahasiswa mahasiswa;
                    mahasiswa = new Mahasiswa();
                    mahasiswa.setNama(splitstr[0]);
                    mahasiswa.setNim(splitstr[1]);
                    mahasiswaArrayList.add(mahasiswa);
                } while (line != null);
            } catch (Exception e){
                e.printStackTrace();
            }
            return mahasiswaArrayList;
        }
    }

    public void sendMessage(int messageStatus){
        Message message = Message.obtain(null, messageStatus);
        try {
            messengerActivity.send(message);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }
}

interface LoadDataCallback{
    void onPreLoad();
    void onProgressUpdate(long progress);
    void onLoadSuccess();
    void onLoadFailed();
    void onLoadCancel();
}
