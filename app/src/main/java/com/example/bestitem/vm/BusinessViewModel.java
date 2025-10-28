package com.example.bestitem.vm;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.bestitem.data.model.Business;
import com.example.bestitem.data.repo.BusinessRepository;

import java.util.List;
public class BusinessViewModel extends ViewModel {
    private final BusinessRepository repo = new BusinessRepository();
    private LiveData<List<Business>> businesses;

    public void createWithKnownId(Business b, BusinessRepository.OnComplete cb) { repo.createWithKnownId(b, cb); }
    public void deleteWithImage(String id, BusinessRepository.OnComplete cb) { repo.deleteWithImage(id, cb); }


    public LiveData<List<Business>> getBusinesses() {
        if (businesses == null) businesses = repo.listenAll();
        return businesses;
    }
    public void create(Business b, BusinessRepository.OnComplete cb) {
        repo.create(b, cb);
    }
    public void update(Business b, BusinessRepository.OnComplete cb) {
        repo.update(b, cb);
    }
    public void delete(String id, BusinessRepository.OnComplete cb) {
        repo.delete(id, cb);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        repo.stopListening();
    }
}
