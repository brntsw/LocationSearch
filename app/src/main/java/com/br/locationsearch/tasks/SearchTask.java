package com.br.locationsearch.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.br.locationsearch.activity.MainActivity;
import com.br.locationsearch.activity.MapMarkerActivity;
import com.br.locationsearch.classes.Location;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BRUNO on 19/09/2014.
 *
 * This task is responsible to, through an address received by parameter on the doInBackground method, concatenate it as a
 * get parameter on the Google API URL and, using this URL, makes an HTTP request and receives a List of locations, then
 * on the onPostExecute method responsible for the UI add the results to the list (ListView) and, when clicking on one of
 * the list's items, an Intent is created by the context received by parameter on the constructor, then it starts the
 * activity MapMarkerActivity, sending the proper data to this activity
 *
 */
public class SearchTask extends AsyncTask<String, Void, List<Location>> {

    private ProgressDialog progress;
    private Context context;
    private ListView listView;

    public SearchTask(Context context, ListView listView){
        this.context = context;
        this.listView = listView;
    }

    @Override
    protected List<Location> doInBackground(String... params) {
        //Check if internet is available and, if it is, search for the location, if it's not, return null
        if(isNetworkAvailable()) {
            HttpGet httpGet = null;
            HttpClient client = null;
            HttpResponse response;
            StringBuilder builder = null;

            try {
                //Set the parameter received to UTF-8 and replace spaces with %20(equivalent to a space on a URL)
                String param = URLDecoder.decode(params[0], "UTF-8").replace(" ", "%20");
                httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?address=" + param +
                        "&sensor=false");
                //Get the HttpClient responsible to handle the HTTP request
                client = new DefaultHttpClient();
                //StringBuilder to create the String for the JSONObject
                builder = new StringBuilder();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                //Get the response from the URL
                response = client.execute(httpGet);
                //Entity to get the content from the response
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                //BufferedReader to read all the characters of the Entity content and append to the StringBuilder
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                int val;

                while ((val = br.read()) != -1) {
                    builder.append((char) val);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject;

            List<Location> listLocation = new ArrayList<Location>();

            try {
                jsonObject = new JSONObject(builder.toString());

                //Get all the nodes from the JSON which names are "results"
                JSONArray jArray = jsonObject.getJSONArray("results");
                int countJson = jArray.length();

                for (int i = 0; i < countJson; i++) {
                    Location location = new Location();

                    //Get the specific node from the JSON which name is "formatted_address"
                    String formattedAddress = ((JSONArray) jsonObject.get("results")).getJSONObject(i)
                            .getString("formatted_address");

                    //Get the specific node from the JSON node "location" which name is "lat"
                    double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(i)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    //Get the specific node from the JSON node "location" which name is "lng"
                    double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(i)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    location.setFormattedAddress(formattedAddress);
                    location.setLat(lat);
                    location.setLng(lng);

                    listLocation.add(location);
                }
            } catch (JSONException e) {
                Log.i("ERRO", e.getMessage());
            }

            return listLocation;
        }
        else{
            return null;
        }
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        progress = new ProgressDialog(context);
        progress.setMessage("Loading...");
        progress.show();
    }

    @Override
    protected void onPostExecute(final List<Location> locations){
        super.onPostExecute(locations);
        if(locations != null){
            /* ArrayAdapter of Location object to fill the ListView with Location objects showing the toString()
               method of the Location class */
            ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(context, android.R.layout.simple_list_item_1, locations);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Location location = (Location) parent.getItemAtPosition(position);

                    Intent intent = new Intent(context, MapMarkerActivity.class);
                    intent.putExtra(MainActivity.EXTRA_NAME, location.getFormattedAddress());
                    intent.putExtra(MainActivity.EXTRA_LAT, String.valueOf(location.getLat()));
                    intent.putExtra(MainActivity.EXTRA_LNG, String.valueOf(location.getLng()));
                    intent.putExtra(MainActivity.EXTRA_LIST, (ArrayList<Location>)locations);
                    context.startActivity(intent);
                }
            });
        }

        progress.dismiss();
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
