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

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by widemos on 1/4/15.
 */
public class LazyAdapter extends BaseAdapter {

    // Referencia a la actividad en la que está la lista
    private Activity activity = null;

    // Objeto que permite fabricar las filas
    private LayoutInflater inflater = null;

    // Lista de objetos de tipo teléfono
    private List<ParseObject> telefonos = null;

    public LazyAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Dibujamos una fila personalizada para el ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.fila, null);

        try {

            TextView titulo = (TextView) vi.findViewById(R.id.titulo);
            TextView resumen = (TextView) vi.findViewById(R.id.resumen);
            CheckBox conectado = (CheckBox) vi.findViewById(R.id.conectado);

            titulo.setText(telefonos.get(position).getString("nombre"));
            resumen.setText(telefonos.get(position).getString("androidId"));
            conectado.setChecked(telefonos.get(position).getBoolean("conectado"));

        } catch (Exception e) {
            Log.e("MobileBox", e.getMessage());
        }

        return vi;
    }

    @Override
    public int getCount() {
        if (telefonos != null)
            return telefonos.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return telefonos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<ParseObject> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<ParseObject> telefonos) {
        this.telefonos = telefonos;
    }
}