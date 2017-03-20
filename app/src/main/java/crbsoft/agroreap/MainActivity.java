package crbsoft.agroreap;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText txtusername;
    EditText txtpassword;
    ProgressBar progressbar;
    Button btningresar;

    JsonObjectRequest array;
    RequestQueue mRequestQueue;
    private final String url = "http://138.197.78.54/api/agrooreap/mobile/authenticate";
    private final String TAG = "PRUEBITA";

    TelephonyManager manager;
    private final String imei = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NFC
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC activo!", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "NFC inactivo!", Toast.LENGTH_LONG).show();
        }

        //IMEI
        manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Toast.makeText(this, manager.getDeviceId(), Toast.LENGTH_LONG).show();

        initializeComponents();
        progressbar.setVisibility(View.INVISIBLE);

        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
                progressbar.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response){
                        String token = response;
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Principal.class));
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressbar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse != null)
                        {
                            if (error.networkResponse.statusCode == 401)
                            {
                                Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d("TAG", error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("pers_id", txtusername.getText().toString());
                        map.put("password", txtpassword.getText().toString());
                        map.put("devi_id", manager.getDeviceId().toString());
                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }

                    @Override
                    public RetryPolicy getRetryPolicy() {
                        return new DefaultRetryPolicy(
                                5000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        );
                    }
                };
                mRequestQueue.add(request);
            }
        });
    }

    private void initializeComponents() {
        txtusername = (EditText) findViewById(R.id.txtUsername);
        txtpassword = (EditText) findViewById(R.id.txtPassword);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        btningresar = (Button) findViewById(R.id.btnIngresar);
    }
}
