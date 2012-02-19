package com.yoanaydavid.recetas;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.yoanaydavid.recetas.java.Ingrediente;

public class ListaCompraAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<Ingrediente> ingListCopy;
	private ArrayList<Ingrediente> ingListReference;
	private ArrayList<Ingrediente> lista;
	LayoutInflater inflater;
	
	public ListaCompraAdapter(Context context, ArrayList<Ingrediente> ingList) {
		
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.ingListReference = ingList;

		try {

			//this.ingListCopy = (ArrayList<Ingrediente>) ingList.clone();
			this.lista = (ArrayList<Ingrediente>) ingList.clone();
			
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
	}



	public void setListReference(ArrayList<Ingrediente> list) {
		ingListReference = list;
	}

	public void setListCopy(ArrayList<Ingrediente> list) {
		try {
			ingListCopy = (ArrayList<Ingrediente>) list.clone();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
	}
	
	public void setList(ArrayList<Ingrediente> list){
		try {

			//this.ingListCopy = (ArrayList<Ingrediente>) ingList.clone();
			this.lista = (ArrayList<Ingrediente>) list.clone();
			
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
	}
	
	public ArrayList<Ingrediente> getList(){
		return lista;
	}

	@Override
	public int getCount() {

		return lista.size();
	}

	@Override
	public Object getItem(int position) {

		return lista.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Ingrediente entry = lista.get(position);
		ViewHolder holder;
		if (convertView == null) {
            
            convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
            holder = new ViewHolder();
            
            holder.checkbox = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            
            convertView.setTag(holder);
            
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }
		
		
		holder.checkbox.setText(entry.getNombre());
		holder.checkbox.setChecked(entry.isChecked());
		
		
		
		return convertView;

	}

	static class ViewHolder {
		private CheckedTextView checkbox;
		
		
	}

}
