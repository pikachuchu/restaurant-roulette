package com.cs371m.chuchu.restaurantroulette;

import android.app.Application;
import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "v5qViBKOMvGQO59OR4Y4nleFXXFxwCaQ4gA587uW", "kVozZCFPyRwL7dAZ1fl1WgIjyAvCXM9Be1hRWnzZ");
    }
}
