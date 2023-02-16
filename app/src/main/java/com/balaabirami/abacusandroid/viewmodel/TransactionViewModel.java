package com.balaabirami.abacusandroid.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.balaabirami.abacusandroid.firebase.FirebaseHelper;
import com.balaabirami.abacusandroid.model.Resource;
import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel implements TransactionListListener {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Resource<List<StockTransaction>>> result = new MutableLiveData<>();


    public TransactionViewModel(@NonNull Application application) {
        super(application);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.init(FirebaseHelper.TRANSACTIONS_REFERENCE);
    }

    public void getAllTransactions(Stock stock, User currentUser) {
        result.setValue(Resource.loading(null, null));
        firebaseHelper.getAllTransactions(this, stock, currentUser);
    }

    @Override
    public void onTransactionsLoaded(List<StockTransaction> stockTransactions) {
        result.setValue(Resource.success(stockTransactions));
    }

    public MutableLiveData<Resource<List<StockTransaction>>> getResult() {
        return result;
    }
}