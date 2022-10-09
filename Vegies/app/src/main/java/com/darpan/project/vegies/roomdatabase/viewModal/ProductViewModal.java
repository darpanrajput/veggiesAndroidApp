package com.darpan.project.vegies.roomdatabase.viewModal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.darpan.project.vegies.roomdatabase.entity.ProductTable;
import com.darpan.project.vegies.roomdatabase.repo.ProductRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductViewModal extends AndroidViewModel {
    private ProductRepo productRepo;
    private LiveData<List<ProductTable>> getAllProductData;

    public ProductViewModal(@NonNull Application application) {
        super(application);
        productRepo = new ProductRepo(application);
        getAllProductData = productRepo.getAllProductData();
        //getImageAllTaskList=productRepo.getAllProductData();
    }

    public void insert(ProductTable productTable) {
        productRepo.insert(productTable);
    }

    public void update(ProductTable productTable) {
        productRepo.update(productTable);
    }

    public void delete(ProductTable productTable) {
        productRepo.delete(productTable);
    }

    public void deleteAll() {
        productRepo.deleteAllProduct();
    }

    public LiveData<List<ProductTable>> getAllCartData() {
        return getAllProductData;
    }

    public int getProductCount() throws ExecutionException, InterruptedException {
        return productRepo.getProductCount();
    }



    public void deleteThisCart(String PID) {
        productRepo.deleteThisCart(PID);
    }



    public void insertNewSavedQty(String PID, String newQty) {
        productRepo.inertNewSavedQty(PID, newQty);
    }



    public String getProductId(String PID) throws ExecutionException, InterruptedException {
        return productRepo.getProductId(PID);
    }

    public ArrayList<String>getAvailableOptionQty(String PID) throws ExecutionException,InterruptedException{
        return productRepo.getAvailableOptionQty(PID);
    }

}
