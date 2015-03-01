package com.apps.chris.hotjobs;

import java.util.*;
import java.io.*;
import java.net.*;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.*;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.*;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button mButton;
    EditText mEdit;
    private String query = "";
    TextView viewtext;
    URL url;

    private String[] locations = {"Chicago", "Dallas", "New York", "Houston", "San Francisco", "Seattle", "Miami", "Atlanta" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(40.000, -95.000), 3.0f) );
        mEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEdit.setText("");
            }
        });
        mButton = (Button) findViewById(R.id.button);
        mEdit = (EditText) findViewById(R.id.input1);
        viewtext = (TextView) findViewById(R.id.output1);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        query= mEdit.getText().toString();
                        //viewtext.setText(query);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mEdit.getWindowToken(),0);
                        System.out.println("hello");
                        try {
                            String[] data = query.split(":");
                            String[] jobskill = data[0].split(",");
                            int temp = Integer.parseInt(data[1]);
                            String skillsearch = "";
                            for(String d: jobskill)
                            {skillsearch+=d+"+";}
                            for(String l: locations) {
                                url = new URL("https://api.usa.gov/jobs/search.json?query="+skillsearch+"+in+"+l);
                                String res = new jobRetrieve().execute(url).get();
                                String findStr = "id";
                                int lastIndex = 0;
                                int count =0;
                                while(lastIndex != -1){

                                    lastIndex = res.indexOf(findStr,lastIndex);

                                    if( lastIndex != -1){
                                        count ++;
                                        lastIndex+=findStr.length();
                                    }
                                }
                                System.out.println(count+ " "+l);

                            }


                           }
                        catch(Exception e) {e.printStackTrace();}

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void addHeatMap() {
        HeatmapTileProvider mProvider;
        TileOverlay mOverlay;
        List<WeightedLatLng> list = new ArrayList<>();
        list.add(new WeightedLatLng(new LatLng(32.7767, -96.7970),100));

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list).build();
        mProvider.setRadius(35);
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(40.000, -95.0000), 3.0f) );
                addHeatMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}


 class jobRetrieve extends AsyncTask<URL, Integer, String>{

     protected String doInBackground(URL... url){
       try {
           HttpURLConnection connection = null;
           InputStream is = null;
           connection = (HttpURLConnection) url[0].openConnection();
           connection.setRequestMethod("GET");
           connection.connect();
           is = connection.getInputStream();
           BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

           return reader.readLine();
       }
       catch(Exception e){e.printStackTrace();}
        return null;
    }


}