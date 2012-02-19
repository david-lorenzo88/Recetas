package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Mode;

public class VerListasGuardadasActivity extends ListActivity {
	private final static int SHOW_LISTA_ACTIVITY_CODE = 0;
	private final static int SHOW_RECETA_ACTIVITY_CODE = 3;

	List<String> elementos;
	FileXML file;
	String listaActual;
	int mode = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		file = new FileXML();
		Bundle extras = getIntent().getExtras();
		mode = extras.getInt("mode");
		try {
			if (mode == Mode.LISTAS_MODE) {
				elementos = file.leerElementos("listas.xml", "lista");

				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, elementos));
				setTitle("Lista almacenadas");
			} else if (mode == Mode.RECETAS_MODE) {
				elementos = file.leerElementos("recetas.xml", "receta");

				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, elementos));
				setTitle("Recetas almacenadas");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed(){
	    startActivity(new Intent(this, Main.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);

		if (mode == Mode.LISTAS_MODE) {
			listaActual = elementos.get(position);
			try {
				ArrayList<String> ingredientes = (ArrayList<String>) file
						.leerIngredientesLista(listaActual);
				Intent intent = new Intent(this, ShowListaActivity.class);
				intent.putStringArrayListExtra("ingredientes", ingredientes);
				intent.putExtra("mode", Mode.SHOW);
				intent.putExtra("title", listaActual);
				startActivityForResult(intent, SHOW_LISTA_ACTIVITY_CODE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mode == Mode.RECETAS_MODE) {
			listaActual = elementos.get(position);

			Intent intent = new Intent(this, ShowRecetaActivity.class);

			intent.putExtra("mode", Mode.RECETA_UNKNOWN_MODE);
			intent.putExtra("nombre", listaActual);
			startActivityForResult(intent, SHOW_RECETA_ACTIVITY_CODE);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Mode.DELETE) {
			elementos.remove(listaActual);
			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, elementos));
		} 
		if(resultCode == Mode.SAVE){
			try {
				elementos = file.leerElementos("recetas.xml", "receta");
				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, elementos));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

	}
}
