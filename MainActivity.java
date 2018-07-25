package com.example.jpolanco.vistasapp;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

//import com.amitshekhar.DebugDB;

import com.example.jpolanco.vistasapp.clasesAux.tableAjusteMedAdapter;
import com.example.jpolanco.vistasapp.clasesAux.tableInventarioAdapter;
import com.example.jpolanco.vistasapp.dao.daoInventario;
import com.example.jpolanco.vistasapp.dao.daoMedicamentos;
import com.example.jpolanco.vistasapp.entidades.ctl_medicamento;
import com.example.jpolanco.vistasapp.network.networking;
import com.example.jpolanco.vistasapp.network.networkingInventario;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    tableInventarioAdapter adapter;
    ListView myView;
    Activity me = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daoInventario daoI = new daoInventario(this);
        networkingInventario getInv = new networkingInventario(me);
        try {
            String outputServer  =
                    getInv
                            .execute("http://192.168.1.22:8080/sicia-minsal/rest/SiciaWS/InventoryDetails")
                            .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        daoMedicamentos daoM = new daoMedicamentos(this);
        try {
            daoM.JSONFromFiles(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            daoM.insertMedicamentosJSONFILE();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //daoI.insertInventarioPrueba();
        adapter = new tableInventarioAdapter(this, R.layout.inventario_partial,daoI.getInventario());
        myView = findViewById(R.id.listInventario);
        myView.setAdapter(adapter);
    }

    public void sendMessage(View view)
    {
        insertMED();
        Intent i = new Intent(getApplicationContext(), ajusteInventario.class);
        startActivity(i);
        //loadDataPrueba();
    }

    public void ingConsumo(View view)
    {
        Intent i = new Intent(getApplicationContext(), salida_inven.class);
        startActivity(i);
        //loadDataPrueba();
    }

    private void insertMED(){
        //DB debugger
        //DebugDB.getAddressLog();
    }

    public void updateInventario(View view){
        networkingInventario getInv = new networkingInventario(me);
        try {
            String outputServer  =
                    getInv
                            .execute("http://192.168.1.22:8080/sicia-minsal/rest/SiciaWS/InventoryDetails")
                            .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
