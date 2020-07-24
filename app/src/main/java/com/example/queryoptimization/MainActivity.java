package com.example.queryoptimization;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.queryoptimization.adapter.MahasiswaAdapter;
import com.example.queryoptimization.database.MahasiswaHelper;
import com.example.queryoptimization.model.Mahasiswa;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MahasiswaAdapter mahasiswaAdapter = new MahasiswaAdapter();
        recyclerView.setAdapter(mahasiswaAdapter);

        MahasiswaHelper mahasiswaHelper = new MahasiswaHelper(this);
        mahasiswaHelper.open();

        ArrayList<Mahasiswa> mahasiswaArrayList = mahasiswaHelper.getAllData();
        mahasiswaHelper.close();

        mahasiswaAdapter.setData(mahasiswaArrayList);
    }
}