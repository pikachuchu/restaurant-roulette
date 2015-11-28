package com.cs371m.chuchu.restaurantroulette;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class Register extends AppCompatActivity {

    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        final EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText passwordAgainEditText = (EditText) findViewById(R.id.passwordAgainEditText);
        final EditText emailEditText = (EditText) findViewById(R.id.email);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String passwordAgain = passwordAgainEditText.getText().toString().trim();
                String validationError = LoginManager.validate(username, email, password, passwordAgain);
                if (!validationError.equals("")) {
                    doToast(validationError);
                    return;
                }

                ParseUser newUser = new ParseUser();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);

                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            doToast(e.getMessage());
                            return;
                        }
                        ParseObject po = ParseObject.create("Rating");
                        po.put("username", username);
                        po.put("rating", 0.0);
                        po.put("numRatings", 0);
                        po.put("onTime", 0);
                        po.put("late", 0);
                        po.put("noShow", 0);
                        po.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.d("Register", e.getMessage());
                                    return;
                                }
                                doToast("Successfully registered as " + ParseUser.getCurrentUser().getUsername());
                                finish();
                            }
                        });
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_events) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
