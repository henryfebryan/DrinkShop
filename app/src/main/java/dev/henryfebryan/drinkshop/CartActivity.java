package dev.henryfebryan.drinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import dev.henryfebryan.drinkshop.Adapter.CartAdapter;
import dev.henryfebryan.drinkshop.Adapter.FavoriteAdapter;
import dev.henryfebryan.drinkshop.Database.ModelDB.Cart;
import dev.henryfebryan.drinkshop.Database.ModelDB.Favorite;
import dev.henryfebryan.drinkshop.Retrofit.IDrinkShopAPI;
import dev.henryfebryan.drinkshop.Utils.Common;
import dev.henryfebryan.drinkshop.Utils.RecyclerItemTouchHelper;
import dev.henryfebryan.drinkshop.Utils.RecyclerItemTouchHelperListener;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener{

    private static final int PAYMENT_REQUEST_CODE = 7777;
    RecyclerView recycler_cart;
    Button btn_place_order;

    RelativeLayout rootLayout;
    List<Cart> cartList = new ArrayList<>();

    CompositeDisposable compositeDisposable;

    CartAdapter cartAdapter;

    IDrinkShopAPI mService;
    IDrinkShopAPI mServiceScalars;

    String token,amount,orderAddress,orderComment;
    HashMap<String,String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        compositeDisposable = new CompositeDisposable();

        mService = Common.getAPI();
        mServiceScalars = Common.getScalarsAPI();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        recycler_cart = (RecyclerView) findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        btn_place_order = (Button) findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        loadCartItems();
        loadToken();
    }

    private void loadToken() {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(CartActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                waitingDialog.dismiss();
                btn_place_order.setEnabled(false);
                Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                waitingDialog.dismiss();

                token = responseString;
                btn_place_order.setEnabled(true);
            }
        });
    }

    private void placeOrder() {
        if(Common.currentUser != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Submit Order");

            View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout, null);

            final EditText edt_comment = (EditText) submit_order_layout.findViewById(R.id.edt_comment);
            final EditText edt_other_address = (EditText) submit_order_layout.findViewById(R.id.edt_other_address);

            final RadioButton rdi_user_address = (RadioButton) submit_order_layout.findViewById(R.id.rdi_user_address);
            final RadioButton rdi_other_address = (RadioButton) submit_order_layout.findViewById(R.id.rdi_other_address);

            rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        edt_other_address.setEnabled(false);
                    }
                }
            });
            rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        edt_other_address.setEnabled(true);
                    }
                }
            });

            builder.setView(submit_order_layout);
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    orderComment = edt_comment.getText().toString();
                    if (rdi_user_address.isChecked()) {
                        orderAddress = Common.currentUser.getAddress();
                    } else if (rdi_other_address.isChecked()) {
                        orderAddress = edt_other_address.getText().toString();
                    } else {
                        orderAddress = "";
                    }

                    //payment
                    DropInRequest dropInRequest = new DropInRequest().clientToken("sandbox_z38zgf98_d5nmcbkhfszypcyv");
                    Log.d("TOKEN", token);
                    startActivityForResult(dropInRequest.getIntent(CartActivity.this), PAYMENT_REQUEST_CODE);


                }
            });
            builder.show();
        }else {
            //require login
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Not Login ?");
            builder.setMessage("Please login or register account to submit order");
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();
                }
            });
            builder.show();
        }
    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        if(carts.size() > 0)
        {
            String orderDetail = new Gson().toJson(carts);

            mService.submitOrder(sumPrice,orderDetail,orderComment,orderAddress,Common.currentUser.getPhone())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(CartActivity.this, "Order Submit", Toast.LENGTH_SHORT).show();
                            Common.cartRepository.emptyCart();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("ERROR", t.getMessage());
                        }
                    });
        }

    }


    private void loadCartItems() {
        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayCartItem(carts);
                    }
                })
        );
    }

    private void displayCartItem(List<Cart> carts) {
        cartList = carts;
        cartAdapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(cartAdapter);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartAdapter.CartViewHolder){
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            cartAdapter.removeItem(deletedIndex);

            Common.cartRepository.deleteCartItem(deletedItem);

            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" removed from Cart List").toString(),Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deletedItem,deletedIndex);
                    Common.cartRepository.insertToCart(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYMENT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                if(Common.cartRepository.sumPrice() > 0 ){
                    amount = String.valueOf(Common.cartRepository.sumPrice());
                    params = new HashMap<>();

                    params.put("amount",amount);
                    params.put("nonce",strNonce);

                    sendPayment();

                }
                else {
                    Toast.makeText(this, "Payment amount is 0", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("PAYMENTERROR", error.getMessage());
            }
        }
    }

    private void sendPayment() {
        mServiceScalars.payment(params.get("nonce"),params.get("amount"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.body().toString().contains("Successful")){
                            Toast.makeText(CartActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                            compositeDisposable.add(
                                    Common.cartRepository.getCartItems()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new Consumer<List<Cart>>() {
                                                @Override
                                                public void accept(List<Cart> carts) throws Exception {
                                                    if(!TextUtils.isEmpty(orderAddress))
                                                        sendOrderToServer(Common.cartRepository.sumPrice(),carts,orderComment, orderAddress);
                                                    else
                                                        Toast.makeText(CartActivity.this, "Order Address can't null", Toast.LENGTH_SHORT).show();
                                                }
                                            }));
                        }
                        else {
                            Toast.makeText(CartActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("InfoErrorPayment", t.getMessage());
                    }
                });
    }
}
