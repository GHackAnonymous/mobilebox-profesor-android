/*
 * MobileBox
 * Copyright (C) 2015 Ion Jaureguialzo Sarasola
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.jaureguialzo.mobileboxprofesor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;
import java.util.UUID;


public class MobileBoxProfesor extends ActionBarActivity {

    // Objeto Box alojado en Parse.com
    private ParseObject caja = null;

    // ANDROID_ID del teléfono del profesor
    private String androidId = null;

    // Referencias a los controles
    private TextView etiquetaCaja = null;
    private TextView etiquetaAyuda = null;
    private Button botonRefrescar = null;
    private ListView lista = null;

    // Adaptador para la lista
    private LazyAdapter adaptador = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_box_profesor);

        // Obtener referencias a los controles del interfaz
        etiquetaCaja = (TextView) findViewById(R.id.etiquetaCaja);
        etiquetaAyuda = (TextView) findViewById(R.id.etiquetaAyuda);
        botonRefrescar = (Button) findViewById(R.id.botonRefrescar);

        // Obtener el ANDROID_ID
        androidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        // Texto temporal mientras recuperamos los datos
        etiquetaCaja.setText(getResources().getString(R.string.etiqueta_caja));
        etiquetaAyuda.setText(getResources().getString(R.string.etiqueta_obteniendo_id));

        // Conectar a Parse y buscar si ya existe el objeto de este profesor
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Caja");
        query.whereEqualTo("androidId", androidId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("Parse", "El objeto no existe, creando uno nuevo");

                    // Calcular un ID aleatorio para la caja
                    String id = UUID.randomUUID().toString().substring(0, 5);

                    // Nuevo objeto Box con los datos
                    caja = new ParseObject("Caja");
                    caja.put("androidId", androidId);
                    caja.put("caja", id);
                    caja.saveInBackground();
                } else {
                    Log.d("Parse", "Objeto recuperado correctamente");
                    caja = object;
                }

                etiquetaCaja.setText(caja.getString("caja"));
                etiquetaAyuda.setText(getResources().getString(R.string.etiqueta_moviles_caja) + ": 0");
            }
        });

        // Colgar el adaptador de la lista
        lista = (ListView) findViewById(R.id.lista);
        adaptador = new LazyAdapter(this);
        lista.setAdapter(adaptador);

        // Evento para el botón de recargar la lista
        findViewById(R.id.botonRefrescar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lanzamos el proceso en segundo plano
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        ParseRelation<ParseObject> relation = caja.getRelation("telefonos");
                        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                                if (results != null) {
                                    // Cargamos el adaptador con los datos y refrescamos la lista
                                    adaptador.setTelefonos(results);
                                    adaptador.notifyDataSetChanged();
                                    etiquetaAyuda.setText(getResources().getString(R.string.etiqueta_moviles_caja) + ": " + results.size());
                                }
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });

        // Evento para el botón de limpiar la lista
        findViewById(R.id.botonVaciar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lanzamos el proceso en segundo plano
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        ParseRelation<ParseObject> relation = caja.getRelation("telefonos");
                        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                                if (results != null) {
                                    // Borramos los datos
                                    for( ParseObject r : results ) {
                                        r.deleteInBackground();
                                    }

                                    // Vaciamos el adaptador y actualizamos el interfaz
                                    adaptador.setTelefonos(null);
                                    adaptador.notifyDataSetChanged();
                                    etiquetaAyuda.setText(getResources().getString(R.string.etiqueta_moviles_caja) + ": " + 0);
                                }
                            }
                        });
                        return null;
                    }
                }.execute();

            }
        });

    }

    // Crear el menú "Acerca de..."
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobile_box_profesor, menu);
        return true;
    }

    // Abrir la actividad "Acerca de..."
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_acerca_de).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MobileBoxProfesor.this, AcercaDe.class));
                return true;
            }
        });
        return result;
    }

}
