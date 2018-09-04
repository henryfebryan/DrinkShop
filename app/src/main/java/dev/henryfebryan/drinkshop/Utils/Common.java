package dev.henryfebryan.drinkshop.Utils;

import java.util.ArrayList;
import java.util.List;

import dev.henryfebryan.drinkshop.Database.DataSource.CartRepository;
import dev.henryfebryan.drinkshop.Database.DataSource.FavoriteRepository;
import dev.henryfebryan.drinkshop.Database.Local.DrinkRoomDatabase;
import dev.henryfebryan.drinkshop.Model.Category;
import dev.henryfebryan.drinkshop.Model.Drink;
import dev.henryfebryan.drinkshop.Model.User;
import dev.henryfebryan.drinkshop.Retrofit.IDrinkShopAPI;
import dev.henryfebryan.drinkshop.Retrofit.RetrofitClient;
import dev.henryfebryan.drinkshop.Retrofit.RetrofitScalarsClient;

public class Common {
    //in Emulator , localhost = 10.0.2.2
    public static final String BASE_URL = "http://10.0.2.2/drinkshop/";
    public static final String API_TOKEN_URL = "http://10.0.2.2/drinkshop/braintree/main.php";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded= new ArrayList<>();

    public static int sizeOfCup = -1; // -1: no choice, 0:M, 1:L
    public static int sugar = -1;// -1: no choice
    public static int ice = -1;

    //Database
    public static DrinkRoomDatabase drinkRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;


    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static IDrinkShopAPI getScalarsAPI(){
        return RetrofitScalarsClient.getScalarsClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static String convertCodeToStatus(int orderStatus) {
        switch (orderStatus){
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Cancelled";
                default:
                    return "Order Error";
        }
    }
}
