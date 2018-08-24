package dev.henryfebryan.drinkshop.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import dev.henryfebryan.drinkshop.Database.ModelDB.Cart;
import dev.henryfebryan.drinkshop.Database.ModelDB.Favorite;

@Database(entities = {Cart.class, Favorite.class},version = 1)
public abstract class DrinkRoomDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();

    private static DrinkRoomDatabase instance;

    public static DrinkRoomDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, DrinkRoomDatabase.class, "DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }



}
