package com.example.thanh.instagram;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyHandler {

    // == variables ==
    private static VolleyHandler mVolleyHandler;
    private RequestQueue mRequestQueue;
    private static Context mContext;



    // == constructor ==
    private VolleyHandler(Context mContext) {
        this.mContext = mContext;
        this.mRequestQueue = getRequestQueue();
    }

    // == public methods ==
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    /*
     * synchronized keyword helps to prevent calling the getInstance methods multiple
     * time at the same time. We have to finish the first call then the second call and so on.
     * */
    public static synchronized VolleyHandler getInstance(Context mContext) {
        if (mVolleyHandler == null) {
            mVolleyHandler = new VolleyHandler(mContext);
        }
        return mVolleyHandler;
    }

    public <T> void addRequestToQueued(Request<T> req) {
        getRequestQueue().add(req);
    }
}
