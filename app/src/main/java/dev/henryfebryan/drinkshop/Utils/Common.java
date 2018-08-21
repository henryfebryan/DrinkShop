package dev.henryfebryan.drinkshop.Utils;

import java.util.ArrayList;
import java.util.List;

import dev.henryfebryan.drinkshop.Model.Category;
import dev.henryfebryan.drinkshop.Model.Drink;
import dev.henryfebryan.drinkshop.Model.User;
import dev.henryfebryan.drinkshop.Retrofit.IDrinkShopAPI;
import dev.henryfebryan.drinkshop.Retrofit.RetrofitClient;

public class Common {
    //in Emulator , localhost = 10.0.2.2
    private static final String BASE_URL = "http://10.0.2.2/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded= new ArrayList<>();

    public static int sizeOfCup = -1; // -1: no choice, 0:M, 1:L
    public static int sugar = -1;// -1: no choice
    public static int ice = -1;

    public static IDrinkShopAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
}
