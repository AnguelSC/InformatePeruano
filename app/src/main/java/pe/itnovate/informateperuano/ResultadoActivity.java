package pe.itnovate.informateperuano;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pe.itnovate.informateperuano.adapters.CandidatoAdapter;

public class ResultadoActivity extends AppCompatActivity {
    private VolleyS volley;
    protected RequestQueue fRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        volley = VolleyS.getInstance(this);

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("candidato");
        fRequestQueue = volley.getRequestQueue();
        String urlpost = "http://10.10.3.21:3000/candidatos/"+id;

        JsonArrayRequest req = new JsonArrayRequest(urlpost,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        TextView textView = (TextView) findViewById(R.id.textView5);
                        TextView textView2 = (TextView) findViewById(R.id.textView6);
                        ImageView imageView = (ImageView) findViewById(R.id.imageView3);
                        textView.setText(jsonObject.getString("can_Nombre") + " " + jsonObject.getString("can_Apellido"));

                        Picasso.with(ResultadoActivity.this)
                                .load(jsonObject.getString("can_Foto"))
                                .resize(110, 110)
                                .centerCrop()
                                .into(imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            },
            new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        addToQueue(req);
    }

    public void addToQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (fRequestQueue == null)
                fRequestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            onPreStartConnection();
            fRequestQueue.add(request);
        }
    }

    public void onPreStartConnection() {
        this.setProgressBarIndeterminateVisibility(true);
    }

    public void onConnectionFinished() {
        this.setProgressBarIndeterminateVisibility(false);
    }

}
