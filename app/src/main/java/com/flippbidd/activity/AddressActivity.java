package com.flippbidd.activity;

import static com.flippbidd.Others.Constants.PRO_VERSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flippbidd.R;
import com.flippbidd.utils.PreferenceUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddressActivity extends AppCompatActivity implements OnMapReadyCallback {
    String latitude = "", longitude = "", address1 = "", address2 = "", plan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        getBundleExtra();
        if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
            ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtPropertyLocation1)).setTextColor(getResources().getColor(R.color.grey_font));
            ((TextView) findViewById(R.id.txtPropertyLocation1)).setText(address1);
            ((TextView) findViewById(R.id.txtPropertyLocation2)).setText(address2);
        } else {
            ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtPropertyLocation1)).setText("****");
            ((TextView) findViewById(R.id.txtPropertyLocation2)).setText(address2);
        }
        if(PreferenceUtils.getPlanVersionStatus()){
            ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtPropertyLocation1)).setTextColor(getResources().getColor(R.color.grey_font));
            ((TextView) findViewById(R.id.txtPropertyLocation1)).setText(address1);
            ((TextView) findViewById(R.id.txtPropertyLocation2)).setText(address2);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getBundleExtra() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getString("Lat");
            longitude = extras.getString("Lng");
            address1 = extras.getString("Address1");
            address2 = extras.getString("Address2");
            plan = extras.getString("Plan");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).icon(BitmapFromVector(getApplicationContext(), R.drawable.buysell)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 15));
        drawCircle(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), googleMap);
    }

    private void drawCircle(LatLng point, GoogleMap googleMap) {

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(400);
        circleOptions.strokeColor(R.color.orange);
        circleOptions.fillColor(0x30D7651A);
        circleOptions.strokeWidth(2);
        googleMap.addCircle(circleOptions);

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}