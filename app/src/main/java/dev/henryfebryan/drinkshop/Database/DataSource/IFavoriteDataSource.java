package dev.henryfebryan.drinkshop.Database.DataSource;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import java.util.List;

import dev.henryfebryan.drinkshop.Database.ModelDB.Favorite;
import io.reactivex.Flowable;

public interface IFavoriteDataSource {
    Flowable<List<Favorite>> getFavItems();
    int isFavorite(int itemId);
    void insertFav(Favorite...favorites);
    void delete (Favorite favorite);
}
