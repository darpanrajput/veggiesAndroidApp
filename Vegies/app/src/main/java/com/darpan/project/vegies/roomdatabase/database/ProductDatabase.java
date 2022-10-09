package com.darpan.project.vegies.roomdatabase.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.darpan.project.vegies.roomdatabase.dao.ProductDao;
import com.darpan.project.vegies.roomdatabase.entity.ProductTable;


@androidx.room.Database(entities = ProductTable.class,version = 1)
@TypeConverters({ProductDao.CustomTypeConverters.class})
public abstract class ProductDatabase extends RoomDatabase {

    private static ProductDatabase instance;

    public abstract ProductDao productDao();

    public static synchronized ProductDatabase getInstance(Context ctx) {
        if (instance == null) {
            instance = Room.databaseBuilder(ctx.getApplicationContext(),
                    ProductDatabase.class, "product_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }
    private static Callback callback = new Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new ProductDatabase.populateDbAsyn(instance).execute();
        }
    };

    private static class populateDbAsyn extends AsyncTask<Void, Void, Void> {
        private ProductDao productTableDao;

        private populateDbAsyn(ProductDatabase Db) {
            productTableDao = Db.productDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
         /* productTableDao.insert(
                  new ProductTable("457GFJFKD",
                          "Image Ulr",
                          "Potato",
                          "7kg",
                          "4",
                          "78rs")
          );*/
            return null;
        }
    }

}
