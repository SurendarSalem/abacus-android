package com.balaabirami.abacusandroid.repository;

import android.os.Bundle;

import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public class FranchiseRepository {

    static FranchiseRepository repository;
    private List<User> franchises;

    public static FranchiseRepository getInstance() {
        if (repository == null) {
            repository = new FranchiseRepository();
        }
        return repository;
    }

    public void setFranchises(List<User> franchises) {
        this.franchises = franchises;
    }

    public List<User> getFranchises() {
        return franchises;
    }

    public void clear() {
        repository = null;
        setFranchises(null);
    }
}
