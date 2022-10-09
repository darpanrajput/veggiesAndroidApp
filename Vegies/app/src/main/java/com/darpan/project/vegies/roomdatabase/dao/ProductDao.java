package com.darpan.project.vegies.roomdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverter;
import androidx.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(ProductTable data);

    @Update()
    void update(ProductTable data);

    @Delete
    void delete(ProductTable couponTable);

    @Query("DELETE FROM product_table")
    void deleteAllProduct();

    @Query("SELECT * FROM product_table ORDER BY productName")
    LiveData<List<ProductTable>> getAllProductData();

    @Query("SELECT * FROM product_table ORDER BY productName")
    List<ProductTable> getAllProducts();

    @Query("SELECT * FROM product_table where productId=:id")
    ProductTable getAProduct(String id);

    @Query("SELECT optionQty FROM product_table ORDER BY productName")
    List<String> getAllOptionQty();

    @Query("SELECT productName FROM product_table ORDER BY productName")
    List<String> getAllNames();

    @Query("SELECT productId FROM product_table ORDER BY productName")
    List<String> getAllIds();

    @Query("SELECT productDescription FROM product_table ORDER BY productName")
    List<String> getAllDesc();

    @Query("SELECT productImage FROM product_table ORDER BY productName")
    List<String> getAllImage();

    @Query("SELECT uniquePid FROM product_table ORDER BY productName")
    List<String> getAllUniquePid();


    @Query("SELECT COUNT(ID) FROM  product_table")
    int getProductCount();

    @Query("SELECT optionQty FROM PRODUCT_TABLE where productId=:Pid")
    String getOptionQty(String Pid);


    @Query("DELETE FROM PRODUCT_TABLE WHERE productId=:PID")
    void deleteCart(String PID);


    @Query("UPDATE product_table SET optionQty=:Qty WHERE productId=:PID")
    void insertNewQty(String PID, String Qty);


    @Query("SELECT productId FROM PRODUCT_TABLE where productId=:PID")
    String getProductId(String PID);

    /*get option qty with unique id*/
    @Query("SELECT AvailableOptions FROM PRODUCT_TABLE where productId=:PID")
    List<String> getAvailableOptions(String PID);

    public class CustomTypeConverters{
        @TypeConverter
        public static ArrayList<String> fromString(String value){
            Type listType=new TypeToken<ArrayList<String>>(){}.getType();
            return new Gson().fromJson(value,listType);
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<String> list){
           Gson gson=new Gson();
            return gson.toJson(list);
        }
    }


}
