package com.example.androidexample;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;



/**
 * This class extends Application and is used to maintain global application state.
 * It initializes and provides a RequestQueue for making network requests.
 */
public class AppController extends Application {
    private static AppController instance;
    private RequestQueue requestQueue;

    /**
     * Called when the application is starting, before any activity, service, or receiver objects have been created.
     * Initializes the instance and requestQueue variables.
     */
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    /**
     * Returns the singleton instance of the AppController class.
     * @return instance of AppController
     */
    public static synchronized AppController getInstance(){
        return instance;
    }

    /**
     * Returns the RequestQueue for making network requests.
     * @return RequestQueue for network requests
     */
    public RequestQueue getRequestQueue(){
        return requestQueue;
    }

}
