package com.example.jpolanco.vistasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.jpolanco.vistasapp.clasesAux.tableAjusteMedAdapter;
import com.example.jpolanco.vistasapp.dao.daoInventario;
import com.example.jpolanco.vistasapp.dao.daoMedicamentos;
import com.example.jpolanco.vistasapp.entidades.ctl_medicamento;
import com.example.jpolanco.vistasapp.entidades.inventario;
import com.example.jpolanco.vistasapp.entidades.tableAjusteMed;
import com.example.jpolanco.vistasapp.network.networking;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class salida_inven extends AppCompatActivity {
    Spinner medicamentos;
    TextView fechaVenc, undMedida,cantConsumo,cantDisp, undMED,just,idHIDDEN;
    TableLayout tableDesecho;
    ArrayList<ctl_medicamento> medsList;
    ArrayList<inventario> invList;
    daoMedicamentos daoM ;
    daoInventario daoInv ;
    ArrayList<tableAjusteMed> ajusteTable = new ArrayList<tableAjusteMed>();;
    tableAjusteMedAdapter adapter;
    Context Cont;
    ListView myView;
    ArrayList<String> ids = new ArrayList<String>();
    String idMedicamentoSelected;
    ArrayAdapter<CharSequence> Adapter;
    Activity me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salida_inven);

        daoM = new daoMedicamentos(this);
        daoInv = new daoInventario(this);
        //daoM.insertMedicamentos();
        //daoInv.insertInventarioPrueba();
        Cont = this;
        //ListView myView;
        this.me = this;
        ArrayList<inventario> arrayInv = daoInv.getInventario();

        medicamentos = (Spinner)findViewById(R.id.medsSpinner);
        fechaVenc = (TextView) findViewById(R.id.fechaVenc);
        undMedida = (TextView) findViewById(R.id.undMedida);
        cantDisp = (TextView) findViewById(R.id.cant);
        undMED = (TextView) findViewById(R.id.undMED);
        just= (TextView) findViewById(R.id.just);
        idHIDDEN= (TextView) findViewById(R.id.idHidden);
        idHIDDEN.setVisibility(View.INVISIBLE);
        //medsList = daoM.getMedicamentos();
        invList = daoInv.getInventario();
        //ArrayList<String> listMedicamentos = getMedicamentosSpinner(medsList);
        ArrayList<String> listMedicamentos = getMedicamentosSpinnerFromInv(invList);
        //ArrayAdapter<CharSequence> Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listMedicamentos);
        Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listMedicamentos);
        medicamentos.setAdapter(Adapter);
        medicamentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //fechaVenc.setText(medsList.get(position).getDs_fechaVencimiento().toString());
                //undMedida.setText(medsList.get(position).getDs_presentacion().toString());
                ctl_medicamento medSelected = daoM.getMedicamentoById(invList.get(position).getId_medicamento());
                idMedicamentoSelected = medSelected.getId();
                Log.v("MEDSELECTED",idMedicamentoSelected);
                fechaVenc.setText(invList.get(position).getFecha_vencimiento());
                undMedida.setText(medSelected.getDs_presentacion());
                cantDisp.setText(invList.get(position).getCantidad().toString());
                undMED.setText(undMedida.getText());
                idHIDDEN.setText(medSelected.getId());
                //invList.remove(medSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //adapter = new tableAjusteMedAdapter(this, R.layout.table_ajuste, ajusteTable);
        fechaVenc = findViewById(R.id.fechaVenc);
        undMedida =  findViewById(R.id.undMedida);
        cantConsumo = findViewById(R.id.cantD);
        cantDisp = findViewById(R.id.cant);
        medicamentos = (Spinner)findViewById(R.id.medsSpinner);
        adapter = new tableAjusteMedAdapter(this, R.layout.table_ajuste,ajusteTable);
        myView = findViewById(R.id.listAjuste);
        myView.setAdapter(adapter);
    }

    private ArrayList<String> getMedicamentosSpinnerFromInv(ArrayList<inventario> inventarios){
        ArrayList<String> listMedicamentos = new ArrayList<String>();
        for (int i = 0; i < inventarios.size(); i++){
            listMedicamentos.add(daoM.getMedicamentoById(inventarios.get(i).getId_medicamento()).getDs_medicamento());
        }
        return listMedicamentos;
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        if(input.equals(null) || input.equals("")){
            parsable = false;
        }else{
            try{
                Integer.parseInt(input);
            }catch(ParseException e){
                parsable = false;
            }

        }
        return parsable;
    }

    private boolean verificarCantMedSeleccionado(int cantActual, int nuevaCantidad){
        if(cantActual >= nuevaCantidad) return true;
        else return false;
    }

    private String getCantidad(int cantActual, int nuevaCantidad){
        int cantNueva = cantActual - nuevaCantidad;
        return Integer.toString(cantNueva);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void cleanTextviews(){
        fechaVenc.setText("");
        undMedida.setText("");
        cantConsumo.setText("");
        cantDisp.setText("");
    }

    public void alertError(String titulo, String mensaje){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(titulo);
        builder1.setMessage(mensaje);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void addMedicamentoToTable(View view) {
        if (isParsable(cantConsumo.getText().toString())) {
            Integer cant = Integer.parseInt(cantConsumo.getText().toString());
            if (invList.size() == 0 || medicamentos.getSelectedItem().toString() == null || medicamentos.getSelectedItem().toString() == "") {
                alertError("Error", "No se ha seleccionado ningún medicamento");
            } else if (cant <= 0) {
                alertError("Error", "La cantidad debe ser mayor que 0");
            } else if (!verificarCantMedSeleccionado(Integer.parseInt(cantDisp.getText().toString()), Integer.parseInt(cantConsumo.getText().toString()))) {
                alertError("Error", "La cantidad a desechar debe ser menor o igual a la actual");
            } else {
                ids.add(idMedicamentoSelected);
                ArrayList<String> listMedicamentos = getMedicamentosSpinnerFromInv(daoInv.getInventarioFilter(ids));
                hideSoftKeyboard(this);
                tableAjusteMed ajusteT = new tableAjusteMed();
                ajusteT.setId_medicamento(idHIDDEN.getText().toString());
                ajusteT.setDs_ds_medicamento(medicamentos.getSelectedItem().toString());
                ajusteT.setAjuste(cantConsumo.getText().toString());
                ajusteT.setNuevaCant(getCantidad(Integer.parseInt(cantDisp.getText().toString()), Integer.parseInt(cantConsumo.getText().toString())));
                Log.v("view", ajusteT.getDs_ds_medicamento());
                ajusteTable.add(ajusteT);
                adapter.notifyDataSetChanged();
                cleanTextviews();
                alertError("Agregado", "Se ha agregado el medicamento a la vista previa");

                Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listMedicamentos);
                medicamentos.setAdapter(Adapter);
                invList = daoInv.getInventarioFilter(ids);
            }
        } else {
            alertError("Error", "Ingrese una cantidad a desechar");
        }
    }
    public void AceptarOperation(View view) throws ExecutionException, InterruptedException {
        if(ajusteTable.size() == 0 ){
            alertError("Error","No se han ingresado datos para el ajuste");
        }
        else{
            networking saveOp = new networking(ajusteTable,me);
            String outputServer  =
                    saveOp
                            .execute("http://192.168.1.22:8080/sicia-minsal/rest/SiciaWS/SaveOperation")
                            .get();
            Intent k = new Intent(salida_inven.this, MainActivity.class);
            startActivity(k);
        }

    }
}
