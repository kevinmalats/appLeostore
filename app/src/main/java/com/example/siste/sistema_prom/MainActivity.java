package com.example.siste.sistema_prom;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.OpenOption;

public class MainActivity extends AppCompatActivity {
   TextView registro, user, password;
   Button inicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registro= (TextView)findViewById(R.id.tVregistro);
        inicio= findViewById(R.id.btnInicio);
        user=findViewById(R.id.txtLogin);
        password=findViewById(R.id.txtPass);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                           new Thread(){
                               @Override
                               public void run() {
                                    String resp=logeo(user.getText().toString(),password.getText().toString());
                                    if (resp.equals("login_ok")) {
                                        Intent inc = new Intent(MainActivity.this, MenuActivity.class);
                                        inc.putExtra("sesion",user.getText().toString());
                                        TextView sesion;
                                        //sesion=findViewById(R.id.sesion);
                                        //sesion.setText(sesion.getText().toString()+user.getText().toString());
                                        startActivity(inc);
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                   Toast.makeText( getApplicationContext(), "Usuario o Password Incorrectos", Toast.LENGTH_LONG).show();  
                                            }
                                        });

                                    }
                               }                                                                                                                   
                           }.start();













            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register= new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(register);
            }
        });


    }
    public String logeo(String user, String password){
        URL url= null;
        String linea="";
        int respuesta=0;
        StringBuilder result=null;

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            url= new URL("http://95.216.33.133/~leostore/api/login?login="+user+"&password="+password);

            HttpURLConnection conexion= (HttpURLConnection)url.openConnection();
            respuesta= conexion.getResponseCode();
            result= new StringBuilder();

            if(respuesta== HttpURLConnection.HTTP_OK){
                InputStream in= new BufferedInputStream(conexion.getInputStream());
                BufferedReader buf= new BufferedReader(new InputStreamReader(in));

                while (((linea=buf.readLine())!=null)){
                    result.append(linea);

                }
            }
        }catch (Exception e){

        }


        return result.toString();
    }

}
