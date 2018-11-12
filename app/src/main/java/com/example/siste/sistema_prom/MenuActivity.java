package com.example.siste.sistema_prom;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class MenuActivity extends AppCompatActivity {
    Button productos, favoritos;
    List<String> categorias;
    TextView mensaje, cerrar;
    Spinner spinner;
    String seleccionado;
    String  usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        productos= findViewById(R.id.btnProducto);
        favoritos= findViewById(R.id.btnFavoritos);
        cerrar= findViewById(R.id.cerrarsesionMenu);
        spinner= findViewById(R.id.spinner);
        mensaje= findViewById(R.id.sesionMenu);
        this.usuario=getIntent().getExtras().getString("sesion");
        mensaje.setText(mensaje.getText().toString()+" "+usuario);
        categorias = new ArrayList<>();
        getCategoria();
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(MenuActivity.this, MainActivity.class);
                startActivity(in);
            }
        });
        favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fav= new Intent(MenuActivity.this, FavoritosActivity.class);
                fav.putExtra("sesion",usuario);
                startActivity(fav);
            }
        });
        productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {

                    }
                }.start();

                Intent prod= new Intent(MenuActivity.this, ProductosActivity.class);
                prod.putExtra("seleccion",seleccionado);
                prod.putExtra("sesion",usuario);
                startActivity(prod);
            }
        });
    }

    public void getCategoria(){
        URL url= null;
        String linea="";
        //final int respuesta=0;
        StringBuilder result=null;
        HttpURLConnection conexion;
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            url= new URL("http://95.216.33.133/~leostore/api/v1/categorias");

            conexion= (HttpURLConnection)url.openConnection();
            conexion.connect();



            int respuesta= conexion.getResponseCode();
            result= new StringBuilder();
            String json;

            if(respuesta== HttpURLConnection.HTTP_OK){


                InputStream in= new BufferedInputStream(conexion.getInputStream());
                BufferedReader buf= new BufferedReader(new InputStreamReader(in,"UTF-8"));

                while (((linea=buf.readLine())!=null)){
                    result.append(linea);

                }
                json=result.toString();

                JSONArray jsonArray= new JSONArray(json);
                // scp.setAdapter(new ImageView() );


                //LinearLayout imagenContent= new LinearLayout(this);
                // line= findViewById(R.id.linerimagen);
                //scp.addView(line);


                for (int i=0;i<jsonArray.length();i++){

                    JSONObject obj;
                    obj = jsonArray.getJSONObject(i);
                    String nombre= obj.optString("nombre");

                    categorias.add(nombre);

                }
                ArrayAdapter adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> lista, View view, int position, long id) {
                        for(int i=0; i<categorias.size();i++){
                            if(lista.getItemAtPosition(position).equals(categorias.get(i))){
                                seleccionado=categorias.get(i);

                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



            }else{



            }
        }catch (Exception e){

        }
    }

}
