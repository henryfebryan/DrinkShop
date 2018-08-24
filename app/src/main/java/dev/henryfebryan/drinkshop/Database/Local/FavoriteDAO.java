package dev.henryfebryan.drinkshop.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import java.util.List;

import dev.henryfebryan.drinkshop.Database.ModelDB.Favorite;
import io.reactivex.Flowable;

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM Favorite")
    Flowable<List<Favorite>> getFavItems();

    @Query("SELECT EXISTS (SELECT 1 FROM Favorite WHERE id=:itemId)")
    int isFavorite(int itemId);

    @Delete
    void delete (Favorite favorite);

}
