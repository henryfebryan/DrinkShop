package dev.henryfebryan.drinkshop.Utils;

import dev.henryfebryan.drinkshop.Model.Category;
import dev.henryfebryan.drinkshop.Model.User;
import dev.henryfebryan.drinkshop.Retrofit.IDrinkShopAPI;
import dev.henryfebryan.drinkshop.Retrofit.RetrofitClient;

public class Common {
    //in Emulator , localhost = 10.0.2.2
    private static final String BASE_URL = "http://10.0.2.2/drinkshop/";

    public static User currentUser = null;
    public static Category currentCategory = null;

    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
}
