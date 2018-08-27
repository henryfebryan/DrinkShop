package dev.henryfebryan.drinkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dev.henryfebryan.drinkshop.Adapter.DrinkAdapter;
import dev.henryfebryan.drinkshop.Model.Category;
import dev.henryfebryan.drinkshop.Model.Drink;
import dev.henryfebryan.drinkshop.Retrofit.IDrinkShopAPI;
import dev.henryfebryan.drinkshop.Utils.Common;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IDrinkShopAPI mService;

    RecyclerView lst_drink;

    TextView txt_banner_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        initService();
        initView();
        setCategoryTitle();
        loadListDrink(Common.currentCategory.ID);
    }

    private void setCategoryTitle() {
        txt_banner_name.setText(Common.currentCategory.Name);
    }

    private void loadListDrink(String menuId) {
        compositeDisposable.add(mService.getDrink(menuId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrinkList(drinks);
                    }
                }));
    }

    private void displayDrinkList(List<Drink> drinks) {
        DrinkAdapter adapter = new DrinkAdapter(this,drinks);
        lst_drink.setAdapter(adapter);
    }


    private void initView() {
        txt_banner_name = (TextView) findViewById(R.id.txt_menu_name);

        lst_drink = (RecyclerView) findViewById(R.id.recycler_drinks);
        lst_drink.setLayoutManager(new GridLayoutManager(this,2));
        lst_drink.setHasFixedSize(true);
    }

    private void initService() {
        mService = Common.getAPI();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
