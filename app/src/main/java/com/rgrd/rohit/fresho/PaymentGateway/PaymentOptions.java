package com.rgrd.rohit.fresho.PaymentGateway;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rgrd.rohit.fresho.Activities.HomePage;
import com.rgrd.rohit.fresho.ItemsPOJO.Category_Items_POJO;
import com.rgrd.rohit.fresho.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class PaymentOptions extends AppCompatActivity implements PaymentResultListener {

    String finalAmount, name, mobile, state, city, pin, email, landmark, flatno, orderId, userMobile;
    CardView paytmcv, cashcv;
    TextView totaltv;
    Intent intent;
    private String TAG = "PaymentOptions";
    String payeeAddress = "9146725145@paytm";
    String payeeName = "Rohit Ramtirthe";
    String transactionNote = "UPI Payment";
    String amount;
    String currencyUnit = "INR";
    String appName = "Fresho";
    String transactionReference;
    String merchantCode = "1000200300";
    DatabaseReference databaseReference, databaseReferenceSearch, databaseReferenceCart, databaseReferenceCartStatus, databaseReferenceItems, databaseReferenceProducts;
    ProgressDialog dialog;
    String mode;


    String CurrentDate, currentTime;

    FirebaseDatabase firebaseDatabase;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);

        paytmcv = findViewById(R.id.paytm);
//        phonePecv = findViewById(R.id.paytm);
        cashcv = findViewById(R.id.cash);
        totaltv = findViewById(R.id.totalamount);

        firebaseDatabase = FirebaseDatabase.getInstance();
        intent = getIntent();
        finalAmount = intent.getStringExtra("finalAmount");
        String finalAmountTxt = "₹" + finalAmount;
        totaltv.setText(finalAmountTxt);

        name = intent.getStringExtra("name2");
        mobile = intent.getStringExtra("mobile2");
        email = intent.getStringExtra("email2");
        state = intent.getStringExtra("state2");
        city = intent.getStringExtra("city2");
        pin = intent.getStringExtra("pin2");
        flatno = intent.getStringExtra("flatno2");
        landmark = intent.getStringExtra("landmark2");

        //dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("please wait.....");
        dialog.setTitle("Placing your order");
        dialog.setCanceledOnTouchOutside(false);


        amount = finalAmount;
        transactionReference = String.valueOf(getRandomNumber());
        orderId = String.valueOf(getRandomNumber() + getRandomNumber() + getRandomNumber());

        sharedPreferences = getSharedPreferences("save", 0);
        userMobile = sharedPreferences.getString("userMobile", "");

        databaseReferenceProducts = firebaseDatabase.getReference("data").child("users").child(userMobile).child("products");
        databaseReferenceSearch = firebaseDatabase.getReference("data").child("users").child(userMobile).child("search").child("products");
        databaseReferenceItems = firebaseDatabase.getReference("data").child("users").child(userMobile).child("items");
        databaseReference = firebaseDatabase.getReference("data").child("users").child(userMobile).child("Orders");
        databaseReferenceCart = firebaseDatabase.getReference("data").child("users").child(userMobile).child("Cart").child("products");
        databaseReferenceCartStatus = firebaseDatabase.getReference("data").child("users").child(userMobile).child("cartStatus").child("totalCount");

//        paytmcv.setEnabled(false);
        //Bhim
        paytmcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mobile.length() < 10) {
                    Toast.makeText(PaymentOptions.this, "Please Enter valid phone number in address module", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    makepayment();
                }

//                CurrentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
//                currentTime = CurrentDate.substring(CurrentDate.lastIndexOf(" ") + 1);
//
//                Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&mc=" + merchantCode + "&tr=" + transactionReference + "&tn=" + transactionNote +
//                        "&am=" + Integer.valueOf(amount) + "&cu=" + currencyUnit + "&appname=" + appName);
//                PackageManager pm = getPackageManager();
//                Intent intent = pm.getLaunchIntentForPackage("in.org.npci.upiapp");
//
//                if (null != intent) {
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(uri);
//                    mode = "PAYTM UPI";
//                    dialog.show();
//                    startActivityForResult(intent, 1);
//
//
//                } else {
//
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "in.org.npci.upiapp")));
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "in.org.npci.upiapp")));
//                    }
//                    Toast.makeText(PaymentOptions.this, "App Not installed", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        //bhimm close

//        phonePecv.setEnabled(false);
//        phonePecv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CurrentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
//                currentTime = CurrentDate.substring(CurrentDate.lastIndexOf(" ") + 1);
//                Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&mc=" + merchantCode + "&tr=" + transactionReference + "&tn=" + transactionNote +
//                        "&am=" + amount + "&cu=" + currencyUnit + "&appname=" + appName);
//                PackageManager pm = getPackageManager();
//                Intent intent = pm.getLaunchIntentForPackage("com.phonepe.app");
//
//                if (null != intent) {
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(uri);
//                    mode = "PhonePe UPI";
//                    dialog.show();
//                    startActivityForResult(intent, 1);
//
//                } else {
//
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "in.org.npci.upiapp")));
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "in.org.npci.upiapp")));
//                    }
//                    Toast.makeText(PaymentOptions.this, "App Not installed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        cashcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
                currentTime = CurrentDate.substring(CurrentDate.lastIndexOf(" ") + 1);
                Log.d("0667", "onClick: " + currentTime);
                mode = "Cash";

                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OrderPojo orderPojo = new OrderPojo();
                        orderPojo.setName(name);
                        orderPojo.setCity(city);
                        orderPojo.setCurrentDate(CurrentDate);
                        orderPojo.setCurrentTime(currentTime);
                        orderPojo.setEmail(email);
                        orderPojo.setFinalAmount(amount);
                        orderPojo.setFlatNo(flatno);
                        orderPojo.setLandMark(landmark);
                        orderPojo.setMobile(mobile);
                        orderPojo.setState(state);
                        orderPojo.setTransactionReference(transactionReference);
                        orderPojo.setOrderId(orderId);
                        orderPojo.setLandMark(landmark);
                        orderPojo.setMode(mode);
                        databaseReference.push().setValue(orderPojo);
                        databaseReferenceCart.setValue(null);
                        databaseReferenceCartStatus.setValue(0);

                        databaseReferenceSearch.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String name = data.getKey();
                                    if (name != null)
                                        databaseReferenceSearch.child(name).child("buttonItemCount").setValue(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        databaseReferenceProducts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String categoryName = data.getKey();
                                    for (DataSnapshot data2 : data.getChildren()) {
                                        String itemCatName = data2.getKey();
                                        for (DataSnapshot data3 : data2.getChildren()) {
                                            String itemName = data3.getKey();
                                            Log.e("databaseReferenceItems", "categoryName is : " + categoryName + " itemName is : " + itemName);
                                            if (categoryName != null && itemCatName != null && itemName != null)
                                                databaseReferenceProducts.child(categoryName).child(itemCatName).child(itemName).child("buttonItemCount").setValue(0);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        databaseReferenceItems.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    String categoryName = data.getKey();
                                    for (DataSnapshot data2 : data.getChildren()) {
                                        String itemName = data2.getKey();
                                        Log.e("databaseReferenceItems", "categoryName is : " + categoryName + " itemName is : " + itemName);
                                        if (categoryName != null && itemName != null)
                                            databaseReferenceItems.child(categoryName).child(itemName).child("buttonItemCount").setValue(0);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dialog.cancel();

                        Toast.makeText(PaymentOptions.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(PaymentOptions.this, HomePage.class);
                        startActivity(intent1);
                        finish();
                    }
                }, 2000);


            }
        });
    }

    private void makepayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_4W3AzNuZZuRpdA");

        checkout.setImage(R.drawable.logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Fresho");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(amount) * 100);//pass amount in currency subunits 500/100 they will divide by 100 so for ammount multiply by 100 then put value
            options.put("prefill.email", "rohit.myrg@gmail.com");
            //options.put("prefill.contact","9766029396");
            options.put("prefill.contact",mobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String res = data.getStringExtra("response");
            String search = "SUCCESS";
            if (res != null)
                if (res.toLowerCase().contains(search.toLowerCase())) {

                    OrderPojo orderPojo = new OrderPojo();
                    orderPojo.setName(name);
                    orderPojo.setCity(city);
                    orderPojo.setCurrentDate(CurrentDate);
                    orderPojo.setCurrentTime(currentTime);
                    orderPojo.setEmail(email);
                    orderPojo.setFinalAmount(amount);
                    orderPojo.setFlatNo(flatno);
                    orderPojo.setLandMark(landmark);
                    orderPojo.setMobile(mobile);
                    orderPojo.setState(state);
                    orderPojo.setTransactionReference(transactionReference);
                    orderPojo.setOrderId(orderId);
                    orderPojo.setLandMark(landmark);
                    orderPojo.setMode(mode);
                    databaseReference.push().setValue(orderPojo);


                    dialog.cancel();
                    Toast.makeText(PaymentOptions.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                    /*String senderEmail = email;
                    Intent email = new Intent(android.content.Intent.ACTION_SENDTO);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{senderEmail});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Grocery Store Order Status");
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    //need this to prompts email client only
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));*/

                    Intent intent1 = new Intent(PaymentOptions.this, HomePage.class);
                    startActivity(intent1);
                    finish();

                } else {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private int getRandomNumber() {
        Random random = new Random();
        int a = random.nextInt(50000000);
        int b = random.nextInt(50000000);
        int c = random.nextInt(50000000);
        return a + b + c;
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(PaymentOptions.this, "Payment Successful & Transactional id:"+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(PaymentOptions.this, "Payment Fail and cause is:"+s, Toast.LENGTH_SHORT).show();
    }
}
