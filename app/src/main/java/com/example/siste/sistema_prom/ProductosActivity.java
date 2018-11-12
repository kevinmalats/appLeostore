package com.example.siste.sistema_prom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.snowdream.android.widget.SmartImage;
import com.github.snowdream.android.widget.SmartImageView;
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

public class ProductosActivity extends AppCompatActivity {
    TextView menu, mensaje, cerrar;

    ListView scp;
    String usuario;

    ArrayList<Productos> listaPro= new ArrayList<Productos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        menu=findViewById(R.id.tVMenu);
        cerrar= findViewById(R.id.sesionp);
        scp= findViewById(R.id.dynamic);
        mensaje=findViewById(R.id.sesionProd);
        this.usuario=getIntent().getExtras().getString("sesion");
        mensaje.setText(mensaje.getText().toString()+" "+usuario);
        final String categoria=getIntent().getExtras().getString("seleccion");
        new Thread(){
            @Override
            public void run() {
                obtenerProductos(categoria);
                //menu.setText("hola Mundo");
            }
        }.start();
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte= new Intent(ProductosActivity.this, MainActivity.class);
                startActivity(inte);
            }
        });
        scp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View view, int position, long id) {

                    int item= listaPro.get(position).id;
                    dialog(item);

            }
        });

        //contProduc=findViewById(R.id.lyoProductos);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent men= new Intent(ProductosActivity.this, MenuActivity.class);
                startActivity(men);
            }
        });
    }
    private  void dialog(final int item){
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogo_message);
        Button btnAcep= dialog.findViewById(R.id.btnFavor);
        Button btnCan= dialog.findViewById(R.id.btnCancelar);

        btnAcep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                addFavoritos(item);
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
private void addFavoritos(int item){
    URL url= null;
    String linea="";
    //final int respuesta=0;
    StringBuilder result=null;
    HttpURLConnection conexion;
    StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);


    try {
        url= new URL("http://95.216.33.133/~leostore/api/v1/createfavoritos?user="+usuario+"&producto="+item);

        conexion= (HttpURLConnection)url.openConnection();
        conexion.connect();



        int respuesta= conexion.getResponseCode();
        result= new StringBuilder();
        String json;

        if(respuesta== HttpURLConnection.HTTP_OK){
            InputStream in= new BufferedInputStream(conexion.getInputStream());
            BufferedReader buf= new BufferedReader(new InputStreamReader(in));

            while (((linea=buf.readLine())!=null)){
                result.append(linea);

            }
            if(result.toString().equals("ingreso_ok")){
                Toast.makeText( getApplicationContext(), "Agregado a sus favoritos", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText( getApplicationContext(), "Producto ya existe en sus favoritos", Toast.LENGTH_LONG).show();
            }
        }
    }catch (Exception e){

    }

}
    private void obtenerProductos(String categoria){
        URL url= null;
        String linea="";
       //final int respuesta=0;
        StringBuilder result=null;
        HttpURLConnection conexion;
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            url= new URL("http://95.216.33.133/~leostore/api/v1/Prodcategorias?categoria="+categoria);

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
                    //ImageView objIm= new ImageView(this);
                    String src=host+path;
                    Productos pro= new Productos();
                    pro.imgen=src;
                    pro.precio=precio;
                    pro.titulo=titulo;
                    pro.id=id;
                    listaPro.add(pro);


                            //Picasso.with(this).load(src).into(objIm);


                    //imagenContent.addView();

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scp.setAdapter(new ImagenAdapter(getApplicationContext(),listaPro));

                    }
                });


            }else{



            }
        }catch (Exception e){

        }

    }
  private  class ImagenAdapter extends BaseAdapter{
        Context ctx;
        LayoutInflater lay;
        ArrayList<Productos> listaProducto;
        TextView tvtitulo,tvprecio;
       ImageView img;

      public ImagenAdapter(Context application, ArrayList<Productos> lista) {
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
          //ViewGroup view= (ViewGroup)lay.inflate(R.layout.activity_main_item, null);
          img=(ImageView)view.findViewById(R.id.imagen1);
         tvprecio=(TextView)view.findViewById(R.id.precio);
          tvtitulo=(TextView)view.findViewById(R.id.tvnombre);
          Glide.with(ctx).load(listaProducto.get(position).imgen.toString()).into(img);

          tvprecio.setText("$"+listaProducto.get(position).precio.toString());
          tvtitulo.setText(listaProducto.get(position).titulo.toString());

          return view;
      }
  }
}
