package com.balaabirami.abacusandroid.viewmodel;

import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.Student;

import java.util.List;

public interface StockListListener {
    void onStockListLoaded(Stock stock);

    void onStockListLoaded(List<Stock> stocks);

    void onStockUpdated(Stock stock);
}
