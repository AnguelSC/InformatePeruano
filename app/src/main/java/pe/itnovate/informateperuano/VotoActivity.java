package pe.itnovate.informateperuano;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.itnovate.informateperuano.Entities.Candidato;
import pe.itnovate.informateperuano.adapters.CandidatoAdapter;

public class VotoActivity extends AppCompatActivity {
    protected RequestQueue fRequestQueue;
    private VolleyS volley;
    private Integer total = 0;
    private TextView textView;
    private ListView listView;
    private List<Integer> MisRespuestas = new  ArrayList<>();
    private JSONArray preguntas  = new JSONArray();
    private List<String> candidatos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voto);

        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView3);
        String urlpost = "http://10.10.3.21:3000/getconfiguracion";

        JsonArrayRequest req = new JsonArrayRequest(urlpost,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        preguntas = response;
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = preguntas.getJSONArray(0);

                            for(int j = 0; j < jsonArray.length();j++){
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                candidatos.add(jsonObject.getString("pro_Nombre"));
                                textView.setText(jsonObject.getString("pre_Nombre"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VotoActivity.this,android.R.layout.simple_list_item_1,candidatos);
                        listView.setAdapter(arrayAdapter);
                        total++;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        addToQueue(req);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(total >= preguntas.length()){
                    if(total == preguntas.length())
                        MisRespuestas.add(position);
                    try {
                        final int moda;// = hallarModa(MisRespuestas,0,totalAlt-1);
                        final int prop;
                        if(MisRespuestas.get(0) == MisRespuestas.get(1)){
                            JSONArray awdawd = preguntas.getJSONArray(0);
                            JSONObject jsonObject = awdawd.getJSONObject(0);
                            moda = jsonObject.getInt("can_Codigo");
                            prop = jsonObject.getInt("pro_Codigo");
                        }else{
                            JSONArray awdawd = preguntas.getJSONArray(1);
                            JSONObject jsonObject = awdawd.getJSONObject(0);
                            moda = jsonObject.getInt("can_Codigo");
                            prop = jsonObject.getInt("pro_Codigo");
                        }

                        HashMap<String, String> mRequestParams = new HashMap<String, String>();
                        BackendlessUser current = Backendless.UserService.CurrentUser();
                        mRequestParams.put("usuario", current.getEmail());
                        mRequestParams.put("propuesta", prop + "");
                        String url =  "http://10.10.3.21:3000/respuestas";
                        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,url,new JSONObject(mRequestParams),
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray jsonArray) {

                                        finish();
                                        Intent _i = new Intent(VotoActivity.this,ResultadoActivity.class);
                                        _i.putExtra("candidato",moda);
                                        startActivity(_i);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getMessage()+"", Toast.LENGTH_LONG).show();
                            }
                        });
                        addToQueue(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    MisRespuestas.add(position);
                    JSONArray jsonArray = null;
                    candidatos.clear();
                    try {
                        jsonArray = preguntas.getJSONArray(total);
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                            candidatos.add(jsonObject.getString("pro_Nombre"));
                            textView.setText(jsonObject.getString("pre_Nombre"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(VotoActivity.this, android.R.layout.simple_list_item_1, candidatos);
                    listView.setAdapter(arrayAdapter);

                    total++;
                }
            }
        });


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


    public static int hallarModa (List<Integer> a, int prim, int ult) {
        int i, frec, maxfrec, moda;
        if (prim == ult) return a.get(prim);
        moda = a.get(prim);
        maxfrec = Frecuencia(a, a.get(prim), prim, ult);
        for (i = prim + 1; i<=ult; i++) {
            frec = Frecuencia (a, a.get(i), i, ult);
            if (frec > maxfrec) {
                maxfrec = frec;
                moda = a.get(i);
            }
        }

        return moda;


    }
    public static int Frecuencia (List<Integer> a, int p, int prim, int ult) {
        int i, suma;
        if (prim > ult) return 0;
        suma = 0;
        for (i = prim; i<= ult; i++)
            if(a.get(i) == p)
                suma++;

        return suma;

    }

}
