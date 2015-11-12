package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class Account extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        final Account that = this;

        Button logout = (Button) findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            doToast(e.getMessage());
                        } else {
                            Intent intent = new Intent(that, Login.class);
                            startActivityForResult(intent, 1);
                            finish();
                        }
                    }
                });
            }
        });

        Button resetPassword = (Button) findViewById(R.id.resetPasswordButton);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = ParseUser.getCurrentUser().getUsername();
                ParseUser.requestPasswordResetInBackground(username, new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            doToast(e.getMessage());
                        } else {
                            doToast("Password reset instructions sent to " + username);
                        }
                    }
                });
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
        } else if (id == R.id.action_account) {
            if (ParseUser.getCurrentUser() == null) {
                Intent intent = new Intent(this, Login.class);
                startActivityForResult(intent, 1);
            } else {
                Intent intent = new Intent(this, Account.class);
                startActivityForResult(intent, 1);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void doToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

}
