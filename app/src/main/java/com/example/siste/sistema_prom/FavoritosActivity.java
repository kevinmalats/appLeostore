package com.example.siste.sistema_prom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity {
    TextView menu, mensaje, cerrar;
    ListView scp;
    String usuario;
    BaseAdapter adaptador1;

    ArrayList<Favoritos> listaPro= new ArrayList<Favoritos>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        cerrar= findViewById(R.id.textView4);
        mensaje= findViewById(R.id.sesionFavo);
        this.usuario=getIntent().getExtras().getString("sesion");
        mensaje.setText(mensaje.getText().toString()+" "+usuario);

        scp= findViewById(R.id.listfavo);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(FavoritosActivity.this, MainActivity.class);
                startActivity(in);
            }
        });
        scp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View view, int position, long id) {

                int item= listaPro.get(position).id;

                dialog(item,position);

            }
        });

        menu=findViewById(R.id.tVMenu2);
        obtenerFavoritos();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent men= new Intent(FavoritosActivity.this, MenuActivity.class);
                startActivity(men);
            }
        });
    }
    private  void dialog(final int item, final int posicion){
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_message_favo);
        Button btnAcep= dialog.findViewById(R.id.btnAcepFavor);
        Button btnCan= dialog.findViewById(R.id.btnCancelarElimi);

        btnAcep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFavo(item,posicion);
                dialog.dismiss();



            }
        });
        btnCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void eliminarFavo(final int item, int posicion){
        URL url= null;
        String linea="";
        //final int respuesta=0;
        StringBuilder result=null;
        HttpURLConnection conexion;
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            url = new URL("http://95.216.33.133/~leostore/api/v1/deletetefavoritos?produc=" + item);

            conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();


            int respuesta = conexion.getResponseCode();
            result = new StringBuilder();
            String json;

            if (respuesta == HttpURLConnection.HTTP_OK) {


                InputStream in = new BufferedInputStream(conexion.getInputStream());
                BufferedReader buf = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                while (((linea = buf.readLine()) != null)) {
                    result.append(linea);

                }
                if(result.toString().equals("succes")){

                    listaPro.remove(posicion);
                                     Runnable mRunnable = new Runnable() {
                        public void run() {

                            adaptador1.notifyDataSetChanged();
                            Toast.makeText( getApplicationContext(), "Eliminado de sus favoritos", Toast.LENGTH_LONG).show();

                        }
                    };
                    runOnUiThread(mRunnable);

                }else
                    Toast.makeText( getApplicationContext(), "No se pudo eliminar", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){

        }
    }
    private void obtenerFavoritos(){
        URL url= null;
        String linea="";
        //final int respuesta=0;
        StringBuilder result=null;
        HttpURLConnection conexion;
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            url= new URL("http://95.216.33.133/~leostore/api/v1/favoritos?user="+usuario);

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
                String host= "http://95.216.33.133/~leostore/";

                for (int i=0;i<jsonArray.length();i++){

                    JSONObject obj;
                    obj = jsonArray.getJSONObject(i);
                    String path= obj.optString("pathimage");
                    String precio= obj.optString("precio");
                    String titulo= obj.optString("nombre");
                    int id=obj.optInt("id");
                    int id_producto=obj.optInt("id_producto");
                    //ImageView objIm= new ImageView(this);
                    String src=host+path;
                    Favoritos fa= new Favoritos();
                    fa.imagen=src;
                    fa.precio=precio;
                    fa.nombre=titulo;
                    fa.id=id;
                    fa.idProducto=id_producto;
                    listaPro.add(fa);


                    //Picasso.with(this).load(src).into(objIm);


                    //imagenContent.addView();

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adaptador1=new FavoritosActivity.ImagenAdapter(getApplicationContext(),listaPro);
                        scp.setAdapter(adaptador1);

                    }
                });


            }else{



            }
        }catch (Exception e){

        }

    }
    private  class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lay;
        ArrayList<Favoritos> listaProducto;
        TextView tvtitulo,tvprecio;
        ImageView img;

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public ImagenAdapter(Context application, ArrayList<Favoritos> lista) {
            this.ctx= application;
            this.listaProducto=lista;
            //lay= (LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return listaProducto.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =convertView;
            LayoutInflater layao= LayoutInflater.from(ctx);
            view=layao.inflate(R.layout.activity_main_item, null);
            //ViewGroup view= (ViewGroup)lay.inflate(R.layout.activity_favoritos_item, null);
            img=(ImageView)view.findViewById(R.id.imagen1);
            tvprecio=(TextView)view.findViewById(R.id.precio);
            tvtitulo=(TextView)view.findViewById(R.id.tvnombre);
            Glide.with(ctx).load(listaProducto.get(position).imagen.toString()).into(img);
            //Picasso.with(ctx).load(listaProducto.get(position).imagen.toString()).into(img);
            tvprecio.setText("$"+listaProducto.get(position).precio.toString());
            tvtitulo.setText(listaProducto.get(position).nombre.toString());

            return view;
        }
    }
}
