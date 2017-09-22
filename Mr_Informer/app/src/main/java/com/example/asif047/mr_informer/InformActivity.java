package com.example.asif047.mr_informer;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asif047.mr_informer.ImageUpload.ApiClient;
import com.example.asif047.mr_informer.ImageUpload.ApiInterface;
import com.example.asif047.mr_informer.ImageUpload.ImageClass;
import com.example.asif047.mr_informer.helper.AppConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InformActivity extends AppCompatActivity implements View.OnClickListener{



    //latest starts
    private static final String TAG = "Location";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private List<Address> addressList;



    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    //latest ends




    String BASE_URL = "https://asif047mrinformer.000webhostapp.com/";

    EditText edt_phone, edt_message;
    TextView profileTV,addressTV,cityTV,countryTV;
    Button btn_insert;


    //new starts
    private Button chooseBtn;
    private ImageView img;
    private static final int IMG_REQUEST=777;

    private Bitmap bitmap;

  private ImageClass imageClass;


    //new 2 starts

    private String email,latitude,longitude,address,city,country,date_time;

    //new 2 ends



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);



        edt_phone = (EditText) findViewById(R.id.phoneEditText);
        edt_message = (EditText) findViewById(R.id.messageEditText);
       // edt_image = (EditText) findViewById(R.id.imageEditText);





        profileTV= (TextView) findViewById(R.id.profile_textview);

        addressTV= (TextView) findViewById(R.id.address_textview);
        cityTV= (TextView) findViewById(R.id.city_textview);
        countryTV= (TextView) findViewById(R.id.country_textview);

        btn_insert = (Button) findViewById(R.id.insert);
        //logoutBtn= (Button) findViewById(R.id.log_out_button);









        //latest start

        //new starts

        if (!isGPSOn(InformActivity.this))buildDialog(InformActivity.this).show();
        else
        {

        }

        //new ends








        geocoder = new Geocoder(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){


                    Log.e(TAG, "current Latitude: "+location.getLatitude());
                    Log.e(TAG, "current Longitude: "+location.getLongitude());
                    try {
                        addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        Log.e(TAG, "address line: "+addressList.get(0).getAddressLine(0));
                        Log.e(TAG, "city: "+addressList.get(0).getLocality());
                        Log.e(TAG, "country: "+addressList.get(0).getCountryName());
                        Log.e(TAG, "postal code: "+addressList.get(0).getPostalCode());
                        Log.e(TAG, "sublocality: "+addressList.get(0).getSubLocality());
                        Log.e(TAG, "phone: "+addressList.get(0).getPhone());




                        //new 4 starts

                        latitude=""+location.getLatitude();
                        longitude=""+location.getLongitude();
                        address=""+addressList.get(0).getAddressLine(0);
                        city=""+addressList.get(0).getLocality();
                        country=""+addressList.get(0).getCountryName();



                        addressTV.setText(address);
                        cityTV.setText(city);
                        countryTV.setText(country);

                        //new 4 ends







                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        };




        getLastLocation();
        createLocationUpadates();






        //to get the time


        java.util.Calendar c = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sd=new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        date_time=sd.format(c.getTime());



        //Toast.makeText(this,dayofweek,Toast.LENGTH_LONG).show();







        //new2 ends








        firebaseAuth= FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,LogInActivity.class));
        }

        FirebaseUser user=firebaseAuth.getCurrentUser();
        email=""+user.getEmail();



        //String temp_name=getIntent().getStringExtra("user_name");
        //Toast.makeText(ProfileActivity.this,temp_name,Toast.LENGTH_LONG).show();
        if(user.getDisplayName()!=null)
            profileTV.setText("Welcome "+user.getDisplayName());
        if(user.getDisplayName()==null)
            profileTV.setText("Welcomeee "+getIntent().getStringExtra("user_name"));










        //logoutBtn.setOnClickListener(this);

        //latest end














        //new starts

        chooseBtn= (Button) findViewById(R.id.choose_bn);

        img= (ImageView) findViewById(R.id.imageview);

        chooseBtn.setOnClickListener(this);



        //new ends



        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new starts
                if(edt_phone.getText().toString().isEmpty()) edt_phone.setError("This field is required");
                if(edt_message.getText().toString().isEmpty()) edt_message.setError("This field is required");
                //new ends

                if(!edt_phone.getText().toString().isEmpty()&&!edt_message.getText().toString().isEmpty())
                {
                    uploadImage();
                    insert_data();

                    Intent intent=new Intent(InformActivity.this,InformActivity.class);
                    startActivity(intent);
                }

            }
        });


    }







    //new starts
    public void insert_data() {

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL) //Setting the Root URL
                .build();

        AppConfig.insert api = adapter.create(AppConfig.insert.class);



        api.insertData(
                edt_phone.getText().toString(),
                email,
                edt_message.getText().toString(),
                latitude,
                longitude,
                address,
                city,
               country,
                email+"_"+date_time,
                date_time,
                new retrofit.Callback<retrofit.client.Response>() {
                    @Override
                    public void success(retrofit.client.Response result, retrofit.client.Response response) {

                        try {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            String resp;
                            resp = reader.readLine();
                            Log.d("success", "" + resp);

                            JSONObject jObj = new JSONObject(resp);
                            int success = jObj.getInt("success");

                            if(success == 1){
                                Toast.makeText(getApplicationContext(), "Successfully inserted", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(getApplicationContext(), "Insertion Failed", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            Log.d("Exception", e.toString());
                        } catch (JSONException e) {
                            Log.d("JsonException", e.toString());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(InformActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {


        switch (view.getId())
        {
            case R.id.choose_bn:
                selectImage();
                break;
//            case R.id.upload_bn:
//
//                uploadImage();
//                break;


//            case R.id.log_out_button:
//                firebaseAuth.signOut();
//                finish();
//                startActivity(new Intent(this,LogInActivity.class));
//                break;
        }


    }




    //new starts

    private void uploadImage()
    {
        String Image=imageToString();
        String Title=email+"_"+date_time;
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        Call<ImageClass> call=apiInterface.UploadImage(Title,Image);
        //Toast.makeText(InformActivity.this,Image,Toast.LENGTH_LONG).show();
        call.enqueue(new retrofit2.Callback<ImageClass>() {
            @Override
            public void onResponse(Call<ImageClass> call, retrofit2.Response<ImageClass> response) {
                //Toast.makeText(MainActivity.this,"Hello "+imageClass.getResponse(),Toast.LENGTH_LONG).show();
                imageClass=response.body();
                Toast.makeText(InformActivity.this,"Server Response: "+imageClass.getResponse(),Toast.LENGTH_LONG).show();
                img.setVisibility(View.GONE);
                // image_title.setVisibility(View.GONE);
                chooseBtn.setEnabled(false);

                //image_title.setText("");
            }

            @Override
            public void onFailure(Call<ImageClass> call, Throwable t) {
                //Toast.makeText(MainActivity.this,"Server Response: "+t.getMessage().toString(),Toast.LENGTH_LONG).show();
                Log.e("Server Response: ", t.getMessage().toString());
                Log.e("Server Response: ", t.getStackTrace().toString());
            }
        });

    }



    private void selectImage()
    {




        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==IMG_REQUEST&&resultCode==RESULT_OK&&data!=null)
        {
            Uri path=data.getData();

            try
            {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
                //edt_image.setVisibility(View.VISIBLE);
                chooseBtn.setEnabled(false);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    private String imageToString()
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }


    //new ends

    //new ends





    //latest starts


    //new starts
    public boolean isGPSOn(Context context)
    {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        System.out.print("GPS STATUS"+statusOfGPS);

        return  statusOfGPS;
    }



    public AlertDialog.Builder buildDialog(Context c)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle("Turn on the GPS");
        builder.setMessage("You need to turn on the GPS.Press ok to exit");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        return builder;
    }

//new ends



    //new 3 starts

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void createLocationUpadates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    Toast.makeText(InformActivity.this, "Latitude: " + location.getLatitude() +
                            "Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "latitude"+location.getLatitude());
                    Log.e(TAG, "logintude"+location.getLongitude());

                }

            }
        });
    }


    //new 3 ends





    //latest ends


    public void log_out_click(MenuItem item) {

        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,LogInActivity.class));
    }


    public void refresh_activity(MenuItem item) {
        startActivity(new Intent(this,InformActivity.class));
    }





    //back button operation starts
    //back button operation starts

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    //back button operation ends



    //back button operation ends


}
