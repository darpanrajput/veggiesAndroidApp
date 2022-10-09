package com.darpan.project.vegies.roomdatabase.repo;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;


import com.darpan.project.vegies.roomdatabase.dao.ProductDao;
import com.darpan.project.vegies.roomdatabase.database.ProductDatabase;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProductRepo {
    private ProductDao productDao;
    private LiveData<List<ProductTable>> getAllProductData;

    private List<ProductTable> getAllItems;
    private LiveData<List<String>> getImageAllTaskList;


    public ProductRepo(Context context) {
        ProductDatabase couponDatabase = ProductDatabase.getInstance(context);
        productDao = couponDatabase.productDao();
        getAllProductData = productDao.getAllProductData();

        // getAllItems=productDao.getAllItems();
        // getImageAllTaskList = couponDao.getImageAllTaskList();

    }

    public void insert(ProductTable data) {
        new ProductRepo.InsertAsynctask(productDao).execute(data);

    }

    public void update(ProductTable data) {
        new ProductRepo.UpdateAsynctask(productDao).execute(data);
    }

    public void delete(ProductTable data) {
        new ProductRepo.DeleteAsynctask(productDao).execute(data);
    }

    public void deleteAllProduct() {
        new ProductRepo.DeleteALLAsynctask(productDao).execute();
    }


    public LiveData<List<ProductTable>> getAllProductData() {
        return getAllProductData;

    }

    public List<ProductTable> getAllProducts() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllAsyncProduct(productDao).execute().get();

    }

    public int getProductCount() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAsyncProductCount(productDao).execute().get();
    }


    public void deleteThisCart(String PID) {
        new ProductRepo.AsyncDeleteCart(productDao, PID).execute();
    }


    public void inertNewSavedQty(String PID, String newQty) {
        new ProductRepo.AsyncInsertSavedQty(productDao, PID, newQty).execute();

    }


    public String getProductId(String PID) throws ExecutionException, InterruptedException {
        return new ProductRepo.GETPRODUCTID(productDao).execute(PID).get();
    }


    public List<String> getAllOptionQty() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllOptionQty(productDao).execute().get();
    }

    public List<String> getAllNames() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllAsyncNames(productDao).execute().get();
    }

    public List<String> getAllIds() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllAsyncIds(productDao).execute().get();
    }

    public List<String> getAllDesc() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllAsyncDesc(productDao).execute().get();
    }

    public List<String> getAllImage() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllImages(productDao).execute().get();
    }

    public List<String> getAllUniquePid() throws ExecutionException, InterruptedException {
        return new ProductRepo.getAllUniquePid(productDao).execute().get();
    }


    public ProductTable getAProduct(String pid) throws ExecutionException, InterruptedException {
        return new ProductRepo.getAProduct(productDao, pid)
                .execute().get();
    }

    public String getOptionQty(String pid) throws ExecutionException, InterruptedException {
        return new ProductRepo.getAsyncOptionQty(productDao,pid)
                .execute().get();
    }

    public ArrayList<String>getAvailableOptionQty(String pid) throws ExecutionException,InterruptedException{
        /*gives the list of all options array available in firebase
        * but stored it at local database*/
        return new ProductRepo.getAsyncAvailableOptionQty(productDao,pid)
                .execute().get();
    }

    /*.............................................................................*/
    private static class UpdateAsynctask extends AsyncTask<ProductTable, Void, Void> {
        private ProductDao productDao;

        public UpdateAsynctask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductTable... productTables) {
            productDao.update(productTables[0]);
            return null;
        }
    }


    private static class InsertAsynctask extends AsyncTask<ProductTable, Void, Void> {
        private ProductDao productDao;

        public InsertAsynctask(ProductDao productDao) {
            this.productDao = productDao;
        }


        @Override
        protected Void doInBackground(ProductTable... productTables) {
            productDao.insert(productTables[0]);
            return null;
        }
    }


    private static class DeleteAsynctask extends AsyncTask<ProductTable, Void, Void> {
        private ProductDao productDao;

        public DeleteAsynctask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductTable... productTables) {
            productDao.delete(productTables[0]);
            return null;
        }
    }

    private static class DeleteALLAsynctask extends AsyncTask<ProductTable, Void, Void> {
        private ProductDao productDao;

        public DeleteALLAsynctask(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Void doInBackground(ProductTable... productTables) {
            productDao.deleteAllProduct();
            return null;
        }
    }

    private static class getAsyncProductCount extends AsyncTask<Void, Void, Integer> {
        private ProductDao productDao;

        public getAsyncProductCount(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return productDao.getProductCount();
        }
    }


    private static class AsyncDeleteCart extends AsyncTask<Void, Void, Void> {
        private ProductDao productDao;
        private String PID;


        public AsyncDeleteCart(ProductDao productDao, String PID) {
            this.productDao = productDao;
            this.PID = PID;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            productDao.deleteCart(PID);
            return null;
        }
    }


    private static class AsyncInsertSavedQty extends AsyncTask<Void, Void, Void> {
        private ProductDao productDao;
        private String PID;
        private String newSavedQty;

        public AsyncInsertSavedQty(ProductDao productDao, String PID, String newSavedQty) {
            this.productDao = productDao;
            this.PID = PID;
            this.newSavedQty = newSavedQty;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            productDao.insertNewQty(PID, newSavedQty);
            return null;
        }
    }


    private static class GETPRODUCTID extends AsyncTask<String, Void, String> {
        private ProductDao productDao;

        public GETPRODUCTID(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected String doInBackground(String... pID) {
            return productDao.getProductId(pID[0]);
        }
    }

    private static class getAllAsyncProduct extends AsyncTask<Void, Void, List<ProductTable>> {
        private ProductDao productDao;

        private getAllAsyncProduct(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<ProductTable> doInBackground(Void... voids) {
            return productDao.getAllProducts();
        }
    }

    private static class getAllAsyncNames extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllAsyncNames(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllNames();
        }
    }

    private static class getAllAsyncDesc extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllAsyncDesc(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllDesc();
        }
    }

    private static class getAllAsyncIds extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllAsyncIds(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllIds();
        }
    }

    private static class getAllOptionQty extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllOptionQty(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllOptionQty();
        }
    }

    private static class getAllImages extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllImages(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllImage();
        }
    }

    private static class getAllUniquePid extends AsyncTask<Void, Void, List<String>> {
        private ProductDao productDao;

        public getAllUniquePid(ProductDao productDao) {
            this.productDao = productDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return productDao.getAllUniquePid();
        }
    }


    private static class getAProduct extends AsyncTask<Void, Void, ProductTable> {
        private ProductDao productDao;
        private String Pid;

        public getAProduct(ProductDao productDao, String pid) {
            this.productDao = productDao;
            Pid = pid;
        }

        @Override
        protected ProductTable doInBackground(Void... voids) {
            return productDao.getAProduct(Pid);
        }
    }


    private static class getAsyncOptionQty extends AsyncTask<Void, Void, String> {
        private ProductDao productDao;
        private String Pid;

        public getAsyncOptionQty(ProductDao productDao, String pid) {
            this.productDao = productDao;
            Pid = pid;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return productDao.getOptionQty(Pid);
        }
    }

    private static class getAsyncAvailableOptionQty extends AsyncTask<Void, Void, ArrayList<String>> {
        private ProductDao productDao;
        private String Pid;
        public getAsyncAvailableOptionQty(ProductDao productDao, String pid) {
            this.productDao = productDao;
            Pid = pid;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return new ArrayList<>(productDao.getAvailableOptions(Pid));
        }
    }
}
