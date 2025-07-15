package com.example.vk_coffee.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.vk_coffee.dao.CoffeeDao;
import com.example.vk_coffee.dao.OrderDao;
import com.example.vk_coffee.dao.UserDao;
import com.example.vk_coffee.model.Coffee;
import com.example.vk_coffee.model.Order;
import com.example.vk_coffee.model.User;

@Database(entities = {User.class, Coffee.class, Order.class}, version = 5, exportSchema = false) // Incremented version to 5
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CoffeeDao coffeeDao();
    public abstract OrderDao orderDao();

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create a new temporary table with the new schema
            database.execSQL("CREATE TABLE coffee_table_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT, " +
                    "price INTEGER NOT NULL, " +
                    "image BLOB, " +
                    "quantity INTEGER NOT NULL)");

            // Copy the data from the old table to the new table
            database.execSQL("INSERT INTO coffee_table_new (id, name, price, quantity) " +
                    "SELECT id, name, price, quantity FROM coffee_table");

            // Remove the old table
            database.execSQL("DROP TABLE coffee_table");

            // Rename the new table to the old table's name
            database.execSQL("ALTER TABLE coffee_table_new RENAME TO coffee_table");
        }
    };
}
