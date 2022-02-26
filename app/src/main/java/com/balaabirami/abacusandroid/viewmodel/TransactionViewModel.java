package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel implements TransactionListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<StockTransaction>> result = new MutableLiveData<>();


    public TransactionViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.TRANSACTIONS_REFERENCE);
    }

    public void getAllTransactions() {
        result.setValue(Resource.loading(null));
        firebaseHelper.getAllTransactions(this);
    }

    @Override
    public void onTransactionsLoaded(StockTransaction stockTransaction) {
        result.setValue(Resource.success(stockTransaction));
    }

    public MutableLiveData<Resource<StockTransaction>> getResult() {
        return result;
    }
}