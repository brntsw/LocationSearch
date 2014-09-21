package com.br.locationsearch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.br.locationsearch.tasks.SearchTask;
import com.br.locationsearch.utils.ToastMessager;

/*
* MainActivity is the Activity that runs when the application starts.
* This activity is responsible to set the action to the button search, which sends the address informed on the text field
* to the AsyncTask (SearchTask)
* */

public class MainActivity extends Activity {
    public static final String EXTRA_NAME = "locationName";
    public static final String EXTRA_LAT = "latitude";
    public static final String EXTRA_LNG = "longitude";
    public static final String EXTRA_LIST = "list";

    ToastMessager messager;
    EditText txtSearch;
    Button btnSearch;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createScreenItems();
    }

    @Override
    public void onResume(){
        super.onResume();

        messager = new ToastMessager();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSearch = txtSearch.getText().toString();

                if(strSearch.equals("")){
                    messager.showMessage(MainActivity.this, "Type the address", 0);
                    txtSearch.setFocusable(true);
                }
                else{
                    new SearchTask(MainActivity.this, listView).execute(strSearch);
                }
            }
        });
    }

    //Function responsible to initialize the items from the screen
    private void createScreenItems(){
        txtSearch = (EditText)findViewById(R.id.txtSearch);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        listView = (ListView)findViewById(R.id.listLocation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
