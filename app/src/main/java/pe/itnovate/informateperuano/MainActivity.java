package pe.itnovate.informateperuano;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pe.itnovate.informateperuano.Entities.Candidato;
import pe.itnovate.informateperuano.adapters.CandidatoAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    BackendlessUser current;
    private List<Candidato> candidatos = new ArrayList<>();
    private VolleyS volley;
    private CandidatoAdapter candidatoAdapter;
    protected RequestQueue fRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyS.getInstance(this);
        fRequestQueue = volley.getRequestQueue();
        setContentView(R.layout.activity_main);
        current = Backendless.UserService.CurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String appVersion = "v1";
        Backendless.initApp(this, "F89F55EE-64AD-32BF-FFE1-96C258DA8800", "C7F9D9B6-6A7E-6FCF-FF7E-B1D7272E9900", appVersion);
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        String urlpost = "http://10.10.3.21:3000/topcandidatos";
        JsonArrayRequest req = new JsonArrayRequest(urlpost,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            for(int i = 0; i< response.length();i++){
                                Candidato candidato = new Candidato();
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    candidato.setApellidos(jsonObject.getString("can_Apellido"));
                                    candidato.setNombres(jsonObject.getString("can_Nombre"));
                                    candidato.setId(jsonObject.getInt("can_Codigo"));
                                    candidato.setFoto(jsonObject.getString("can_Foto"));
                                    candidato.setPopularidad(jsonObject.getDouble("percent"));
                                    candidato.setVotos(jsonObject.getInt("Respuestas"));
                                    candidatos.add(candidato);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            candidatoAdapter = new CandidatoAdapter(MainActivity.this,R.layout.top_item_candidato,candidatos);
                            ListView tablesListView = (ListView) findViewById(R.id.mainList);
                            candidatoAdapter.notifyDataSetChanged();
                            tablesListView.setAdapter(candidatoAdapter);
                        /*try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        addToQueue(req);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) header.findViewById(R.id.imageView);
        TextView title = (TextView) header.findViewById(R.id.titleHeader);
        TextView email = (TextView) header.findViewById(R.id.email);
        Picasso.with(this)
                .load("https://graph.facebook.com/"+ current.getProperty("email").toString() +"/picture?type=large")
                .resize(110, 110)
                .centerCrop()
                .into(imageView);
        title.setText(current.getProperty("name").toString());
        email.setText(current.getProperty("fb_email").toString());

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            Intent intent = new Intent(MainActivity.this,VotoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            new MaterialDialog.Builder(MainActivity.this)
                    .title("Mi Centro de Votación")
                    .content("Ingresa número de DNI")
                    .inputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_NUMBER_VARIATION_NORMAL |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .positiveText("Buscar")
                    .input("", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            Intent intent = new Intent(MainActivity.this,CentroVotacionActivity.class);
                            intent.putExtra("dni",input.toString());
                            startActivity(intent);
                        }
                    }).show();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}