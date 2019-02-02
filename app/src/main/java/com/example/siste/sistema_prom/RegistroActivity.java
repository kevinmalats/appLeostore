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

public class RegistroActivity extends AppCompatActivity {
  TextView regresar, nombre,apellido, user, password, correo;
  Button aceptar;
    String  mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        regresar=findViewById(R.id.textView8);
        nombre=findViewById(R.id.txtNombres);
        apellido=findViewById(R.id.txtApellidos);
        user=findViewById(R.id.txtUser);
        password=findViewById(R.id.txtPassword);
        correo=findViewById(R.id.txtCorreo);
        aceptar=findViewById(R.id.btnAceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {


                                           if (registrarUser(correo.getText().toString(), password.getText().toString(), user.getText().toString(), nombre.getText().toString(), apellido.getText().toString())) {

                                               Toast.makeText(getApplicationContext(), "Registro Exitoso", Toast.LENGTH_LONG).show();
                                           } else
                                               Toast.makeText(getApplicationContext(), "No se pudo registra, usuario o correo ya existentes", Toast.LENGTH_LONG).show();
                                       }
                                   });

                regresar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent regreso = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(regreso);
                    }
                });

            }

            public boolean registrarUser(String email, String password, String user, String nombre, String apellido) {
                URL url = null;
                String linea = "";
                int respuesta = 0;
                boolean ver = false;
                StringBuilder result = null;

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {

                    url = new URL("http://95.216.33.133/~leostore/api/newuser?email=" + email + "&password=" + password + "&name=" + nombre + "&apellido=" + apellido + "&login=" +user);

                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    respuesta = conexion.getResponseCode();
                    result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {
                        InputStream in= new BufferedInputStream(conexion.getInputStream());
                        BufferedReader buf= new BufferedReader(new InputStreamReader(in));

                        while (((linea=buf.readLine())!=null)){
                            result.append(linea);

                        }
                        if (result.toString().equals("succes")){
                            ver = true;
                        }



                    }
                } catch (Exception e) {

                }


                return ver;
            }


        }



