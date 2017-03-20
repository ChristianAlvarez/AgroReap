package crbsoft.agroreap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Christian on 15-03-2017.
 */

public class VolleySingleton {
    private static VolleySingleton instance = null;
    private RequestQueue mRequestQueue;
    private VolleySingleton()
    {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static VolleySingleton getInstance()
    {
        if (instance == null)
        {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public RequestQueue getmRequestQueue()
    {
        return mRequestQueue;
    }
}
