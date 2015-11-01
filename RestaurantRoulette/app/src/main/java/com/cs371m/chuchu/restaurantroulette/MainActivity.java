package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> temp=new HashMap<String, String>();
        temp.put("Host", "Ankit Karia");
        temp.put("Restaurant", "Male");
        temp.put("When?", "22");
        temp.put("Type", "Unmarried");
        temp.put("Price", "Unmarried");
        temp.put("Distance", "Unmarried");
        list.add(temp);

        HashMap<String,String> temp2=new HashMap<String, String>();
        temp2.put("Host", "Rajat Ghai");
        temp2.put("Restaurant", "Male");
        temp2.put("When?", "25");
        temp2.put("Type", "Unmarried");
        temp2.put("Price", "Unmarried");
        temp2.put("Distance", "Unmarried");
        list.add(temp2);

        HashMap<String,String> temp3=new HashMap<String, String>();
        temp3.put("Host", "Karina Kaif");
        temp3.put("Restaurant", "Female");
        temp3.put("When?", "31");
        temp3.put("Type", "Unmarried");
        temp3.put("Price", "Unmarried");
        temp3.put("Distance", "Unmarried");
        list.add(temp3);

        ListViewAdapter adapter=new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                int pos=position+1;
                Toast.makeText(MainActivity.this, Integer.toString(pos)+" Clicked", Toast.LENGTH_SHORT).show();
            }

        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_toggle_view) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        } else if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
