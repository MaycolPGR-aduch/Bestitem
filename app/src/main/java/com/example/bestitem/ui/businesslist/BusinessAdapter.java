package com.example.bestitem.ui.businesslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bestitem.R;
import com.example.bestitem.data.model.Business;

import java.util.ArrayList;
import java.util.List;
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.VH> {
    private final List<Business> data = new ArrayList<>();
    private final OnItemClick cb;

    public interface OnItemClick {
        void onEdit(Business b);
        void onDelete(Business b);
    }
    public BusinessAdapter(OnItemClick cb) { this.cb = cb; }

    public void submit(List<Business> list) {
        data.clear(); if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_business, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Business b = data.get(pos);
        h.txtName.setText(b.getName());
        h.txtAddress.setText(b.getAddress());
        h.btnEdit.setOnClickListener(v -> cb.onEdit(b));
        h.btnDelete.setOnClickListener(v -> cb.onDelete(b));

        ImageView img = h.itemView.findViewById(R.id.imgThumb);
        if (b.getImageUrl() != null && !b.getImageUrl().isEmpty()) {
            Glide.with(img.getContext()).load(b.getImageUrl()).into(img);
        } else {
            img.setImageResource(android.R.color.darker_gray);
        }

    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtAddress; ImageButton btnEdit, btnDelete;
        VH(@NonNull View v) {
            super(v);
            txtName = v.findViewById(R.id.txtName);
            txtAddress = v.findViewById(R.id.txtAddress);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }

}
