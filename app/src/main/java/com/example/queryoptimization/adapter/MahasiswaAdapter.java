package com.example.queryoptimization.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queryoptimization.R;
import com.example.queryoptimization.model.Mahasiswa;

import java.util.ArrayList;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder> {
    private ArrayList<Mahasiswa> listMahasiswa = new ArrayList<>();

    public MahasiswaAdapter() {
    }

    public void setData(ArrayList<Mahasiswa> listMahasiswa){
        if (listMahasiswa.size() > 0){
            this.listMahasiswa.clear();
        }
        this.listMahasiswa.addAll(listMahasiswa);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MahasiswaAdapter.MahasiswaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new MahasiswaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaAdapter.MahasiswaViewHolder holder, int position) {
        holder.txtNim.setText(listMahasiswa.get(position).getNim());
        holder.txtNama.setText(listMahasiswa.get(position).getNama());
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public class MahasiswaViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNim, txtNama;

        public MahasiswaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNim = itemView.findViewById(R.id.txt_nim);
            txtNama = itemView.findViewById(R.id.txt_nama);
        }
    }
}
