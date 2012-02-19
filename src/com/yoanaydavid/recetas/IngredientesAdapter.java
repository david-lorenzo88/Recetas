package com.yoanaydavid.recetas;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.Ingrediente;


public class IngredientesAdapter extends BaseAdapter {
	private Context context;
	
    private List<Ingrediente> ingList;
    private OnClickListener listener;

    public IngredientesAdapter(Context context, List<Ingrediente> ingList, OnClickListener listener) {
        this.context = context;
        this.ingList = ingList;
        this.listener = listener;
        
    }
	@Override
	public int getCount() {
		
		return ingList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return ingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Ingrediente entry = ingList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list_ingredients, null);
        }
        

        TextView ingrediente = (TextView) convertView.findViewById(R.id.txtRecetasIngrediente);
        ingrediente.setText(entry.getNombre());

        TextView cantidad = (TextView) convertView.findViewById(R.id.txtRecetasCantidad);
        cantidad.setText(entry.getCantidad());
        
        Button btnEditar = (Button) convertView.findViewById(R.id.btnEditarIngrediente);
        btnEditar.setOnClickListener(listener);
        
        
        
        
        
        

        return convertView;
	}
	
	

}
