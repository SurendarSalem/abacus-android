package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.StockTransaction;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public interface TransactionListListener {
    void onTransactionsLoaded(StockTransaction stockTransaction);
}
