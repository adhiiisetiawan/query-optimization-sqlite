package com.example.queryoptimization.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.queryoptimization.model.Mahasiswa;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.queryoptimization.database.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.queryoptimization.database.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.queryoptimization.database.DatabaseContract.TABLE_NAME;

public class MahasiswaHelper {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private static MahasiswaHelper INSTANCE;

    public MahasiswaHelper(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public static MahasiswaHelper getInstance(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new MahasiswaHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException{
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();

        if (database.isOpen()){
            database.close();
        }
    }

    public ArrayList<Mahasiswa> getAllData(){
        Cursor cursor = database.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null, _ID + " ASC", null);

        cursor.moveToFirst();
        ArrayList<Mahasiswa> mahasiswaArrayList = new ArrayList<>();
        Mahasiswa mahasiswa;
        if (cursor.getCount() > 0){
            do {
                mahasiswa = new Mahasiswa();
                mahasiswa.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswa.setNama(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswa.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                mahasiswaArrayList.add(mahasiswa);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return mahasiswaArrayList;
    }

    public long insert(Mahasiswa mahasiswa){
        ContentValues initialValues = new ContentValues();
        initialValues.put(NAMA, mahasiswa.getNama());
        initialValues.put(NIM, mahasiswa.getNim());
        return database.insert(TABLE_NAME, null, initialValues);
    }

    public ArrayList<Mahasiswa> getDataByName(String name){
        Cursor cursor = database.query(TABLE_NAME,
                null,
                NAMA + " LIKE ?", new String[]{name},
                null,
                null,
                _ID + " ASC",
                null);
        cursor.moveToFirst();
        ArrayList<Mahasiswa> mahasiswaArrayList = new ArrayList<>();
        Mahasiswa mahasiswa;
        if (cursor.getCount() > 0){
            do {
                mahasiswa = new Mahasiswa();
                mahasiswa.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswa.setNama(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswa.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                mahasiswaArrayList.add(mahasiswa);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return mahasiswaArrayList;
    }

    public void beginTransaction() {
        database.beginTransaction();
    }
    public void setTransactionSuccess() {
        database.setTransactionSuccessful();
    }
    public void endTransaction() {
        database.endTransaction();
    }
    public void insertTransaction(Mahasiswa mahasiswaModel) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" + NAMA + ", " + NIM + ") VALUES (?, ?)";
        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, mahasiswaModel.getNama());
        stmt.bindString(2, mahasiswaModel.getNim());
        stmt.execute();
        stmt.clearBindings();
    }
}
