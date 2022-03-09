package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Stock;
import com.balaabirami.abacusandroid.model.User;

import java.util.List;

public class StockRepository {

    static StockRepository repository;
    private List<Stock> stocks;

    public static StockRepository getInstance() {
        if (repository == null) {
            repository = new StockRepository();
        }
        return repository;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public void updateStock(Stock stock) {
        int index = stocks.indexOf(stock);
        stocks.set(index, stock);
    }

    public void clear() {
        setStocks(null);
        setStocks(null);
    }
}
