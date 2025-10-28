package com.example.bestitem.data.repo;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bestitem.data.model.Business;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BusinessRepository {
    private final CollectionReference col = FirebaseFirestore.getInstance().collection("businesses");
    private ListenerRegistration listListener;

    public void createWithKnownId(Business b, OnComplete cb) {
        DocumentReference ref = col.document(); // genera ID
        b.setId(ref.getId());
        ref.set(b)
                .addOnSuccessListener(v -> cb.ok(ref.getId()))
                .addOnFailureListener(cb::err);
    }

    // Borrar doc y su imagen principal en Storage
    public void deleteWithImage(String id, OnComplete cb) {
        FirebaseStorage.getInstance().getReference()
                .child("businesses/" + id + "/main.jpg")
                .delete()
                .addOnCompleteListener(task -> {
                    // borre o no la imagen, intenta borrar el doc
                    col.document(id).delete()
                            .addOnSuccessListener(v -> cb.ok(id))
                            .addOnFailureListener(cb::err);
                });
    }

    public LiveData<List<Business>> listenAll() {
        MutableLiveData<List<Business>> live = new MutableLiveData<>();
        listListener = col.orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) { live.setValue(new ArrayList<>()); return; }
                    List<Business> list = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Business b = d.toObject(Business.class);
                        if (b != null) { b.setId(d.getId()); list.add(b); }
                    }
                    live.setValue(list);
                });
        return live;
    }

    public void stopListening() {
        if (listListener != null) listListener.remove();
    }

    public void create(Business b, OnComplete cb) {
        col.add(b).addOnSuccessListener(ref -> cb.ok(ref.getId()))
                .addOnFailureListener(err -> cb.err(err));
    }

    public void update(Business b, OnComplete cb) {
        if (b.getId() == null) { cb.err(new IllegalArgumentException("Missing id")); return; }
        col.document(b.getId()).set(b, SetOptions.merge())
                .addOnSuccessListener(v -> cb.ok(b.getId()))
                .addOnFailureListener(cb::err);
    }

    public void delete(String id, OnComplete cb) {
        col.document(id).delete()
                .addOnSuccessListener(v -> cb.ok(id))
                .addOnFailureListener(cb::err);
    }

    public interface OnComplete {
        void ok(@Nullable String id);
        void err(Exception e);
    }
}
