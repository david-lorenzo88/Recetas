package com.yoanaydavid.recetas;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;

public class EditFragment extends Fragment {
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	private EditText txtBuscar;
	private FileXML file;
	private ArrayList<Ingrediente> lista;
	private Button addButton;
	private Button clearButton;
	private Button listaButton;
	private ListView listViewIngs;
	private ListaCompraAdapter adapter = null;
	private String nombreLista;
	int mode = -1;
	OnClickListener listener;

	public String getNombreLista() {
		return nombreLista;
	}

	public void setNombreLista(String nombreLista) {
		this.nombreLista = nombreLista;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public ListaCompraAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ListaCompraAdapter adapter) {
		this.adapter = adapter;
	}

	public ListView getListViewIngs() {
		return listViewIngs;
	}

	public void setListViewIngs(ListView listViewIngs) {
		this.listViewIngs = listViewIngs;
	}

	private OnClickListener clearButtonOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {

			txtBuscar.setText("");
			try {
				adapter.setListCopy(lista);
				adapter.notifyDataSetChanged();
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			}

		}
	};
	private OnClickListener addIngredienteOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// do something when the button is clicked
			if (!txtBuscar.getText().toString().trim().equals(""))
				try {
					String s = txtBuscar.getText().toString();
					boolean result = file
							.escribirArchivo("ingredientes.xml", s);
					if (result) {

						lista.add(new Ingrediente(s.substring(0, 1)
								.toUpperCase() + s.substring(1).toLowerCase()));
						Toast.makeText(getActivity(), "¡Ingrediente añadido!",
								Toast.LENGTH_SHORT).show();
						Collections.sort(lista);
						txtBuscar.setText("");

						adapter.setListCopy(lista);
						adapter.setListReference(lista);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getActivity(),
								"¡El ingrediente ya existe!",
								Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {

					e.printStackTrace();
				}
		}
	};
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
			
			

			//adapter.setListCopy(lista);

			String texto = txtBuscar.getText().toString().trim();
			// Toast.makeText(getActivity(), texto,
			// Toast.LENGTH_SHORT).show();
			if (texto.equals("")) {
				// Llenar ingredientes
				adapter.setList(lista);
				adapter.notifyDataSetChanged();
				checkItems();
				// fillIngredients();
			} else {
				// Filtrar ingredientes
				setMatchedIngs(texto);

			}

		}
	};
	public void setMatchedIngs(String s) {
		ArrayList<Ingrediente> ings = new ArrayList<Ingrediente>();
		for (int i = 0; i < lista.size(); i++) {
			
			if (lista.get(i).getNombre().toLowerCase()
					.contains(s.toLowerCase()))
				ings.add(lista.get(i));

		}
		adapter.setList(ings);
		adapter.notifyDataSetChanged();
		checkItems();

	}
	/*
	 * private OnKeyListener txtBuscarOnKey = new OnKeyListener() {
	 * 
	 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
	 * 
	 * if (keyCode != KeyEvent.KEYCODE_BACK) { String texto =
	 * txtBuscar.getText().toString().trim(); Toast.makeText(getActivity(),
	 * texto, Toast.LENGTH_SHORT).show(); if (texto.equals("")) { //Llenar
	 * ingredientes adapter.setList(lista); adapter.notifyDataSetChanged();
	 * //fillIngredients(); } else { //Filtrar ingredientes
	 * adapter.removeIfNotMatch(texto);
	 * 
	 * } }
	 * 
	 * return false; } };
	 */
	public EditFragment(int mode, OnClickListener listener) {
		this.mode = mode;
		this.listener = listener;
		file = new FileXML();
		try {
			lista = (ArrayList<Ingrediente>) file.leerIngredientes();
			Collections.sort(lista);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EditFragment(int mode, OnClickListener listener,
			List<String> selectedIngs, String nombreLista) {
		this.mode = mode;

		this.nombreLista = nombreLista;
		
		
		this.listener = listener;
		file = new FileXML();
		try {
			lista = (ArrayList<Ingrediente>) file.leerIngredientes();
			Collections.sort(lista);
			if (mode == Mode.EDIT && selectedIngs != null)
				setItemsChecked(selectedIngs);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		try {
			if (mode == Mode.EDIT 
					&& ((ListasActivity) getActivity()).ismDualPane()
					&& !((ListasActivity) getActivity()).isFinishing() && nombreLista!= null) {
				crearDialogo(
						"¿Desea guardar los cambios en la lista \""
								+ nombreLista + "\"?", Mode.DIALOG_SAVE, -1)
						.show();
			}
			
			super.onDestroy();
		} catch (ClassCastException ex) {
			super.onDestroy();
		}
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		// As paiego pointed out, it's better to use density-aware measurements.
		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

		final GestureDetector gestureDetector = new GestureDetector(
				new MyGestureDetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		listViewIngs.setOnTouchListener(gestureListener);
		
		if(mode == Mode.EDIT)
			getActivity().setTitle("Recetas 2 - Editar \""+nombreLista+"\"");
		
		
		

		
	}
	static class ViewHolder {
		CheckedTextView checkbox;
		
		public ViewHolder(CheckedTextView checkbox){
			this.checkbox = checkbox;
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		// Obtenemos el layout y a partir de él personalizamos la Views que
		// contiene

		View content = inflater.inflate(R.layout.listacompra, container, false);

		clearButton = (Button) content.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(clearButtonOnClick);

		txtBuscar = (EditText) content.findViewById(R.id.txtBuscar);
		txtBuscar.addTextChangedListener(textWatcher);

		addButton = (Button) content
				.findViewById(R.id.addIngredienteButton);
		addButton.setOnClickListener(addIngredienteOnClick);

		listaButton = (Button) content.findViewById(R.id.crearListaButton);

		listaButton.setOnClickListener(listener);

		if (mode == Mode.EDIT) {
			listaButton.setText("Guardar Lista");
			listaButton.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.save, 0, 0, 0);

		}
		listViewIngs = (ListView) content
				.findViewById(R.id.ingredientesListView);
		listViewIngs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listViewIngs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View item, 
                    int position, long id) {
				// TODO Auto-generated method stub
				/*Ingrediente ing = (Ingrediente)adapter.getItem( position );
		        ing.setChecked(!ing.isChecked());
		        ViewHolder viewHolder = (ViewHolder) item.getTag();
		        viewHolder.checkbox.setChecked( ing.isChecked() );*/
				
				
				
				
				
				CheckedTextView v = (CheckedTextView) item;// Log.v("test", Boolean.toString(v.isChecked()));
				boolean check = !v.isChecked();
				//v.setChecked(check);
				for(int i=0; i<lista.size(); i++){
					if(v.getText().toString().equals(lista.get(i).getNombre())){
						lista.get(i).setChecked(check);
						break;
					}
				}
				
				
			}

		});

		adapter = new ListaCompraAdapter(getActivity(), lista);

		if (adapter != null)
			listViewIngs.setAdapter(adapter);


		return content;

	}
	@Override
	public void onResume(){
		super.onResume();
		checkItems();
		
		
	}
	
	public void checkItems(){
		for(int i = 0;i<adapter.getList().size(); i++){
			if(adapter.getList().get(i).isChecked()){
				listViewIngs.setItemChecked(i, true);
			} else {
				listViewIngs.setItemChecked(i, false);
			}
		}
	}
	public void setItemsChecked(List<String> selectedIngs) {
		for (int i = 0; i < selectedIngs.size(); i++)
			for (int j = 0; j < lista.size(); j++)
				if (lista.get(j).getNombre().equals(selectedIngs.get(i))) {
					Log.v("test", "Checked - "+lista.get(j).getNombre());
					lista.get(j).setChecked(true);
					
					break;
				}
	}
	public void setItemsCheckedInListView() {
		ArrayList<String> selectedIngs = getSelectedIngs();
		for (int i = 0; i < selectedIngs.size(); i++)
			for (int j = 0; j < lista.size(); j++)
				if (lista.get(j).getNombre().equals(selectedIngs.get(i))) {
					// Log.v("test", lista.get(j).getNombre());
					listViewIngs.setItemChecked(j, true);
					
					break;
				}
	}

	public AlertDialog crearDialogo(String mensaje, int mode, int pos) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final int position = pos;
		builder.setMessage(mensaje).setCancelable(false)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		switch (mode) {
		case Mode.DIALOG_SAVE:
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Guardar los cambios en la lista

							guardarCambios();

						}
					});
			break;
		case Mode.DIALOG_DELETE:
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Borrar ingrediente
							borrarIngrediente(position);

						}
					});
		
		}

		AlertDialog alert = builder.create();

		return alert;
	}

	public ArrayList<String> getSelectedIngs() {
		ArrayList<String> ings = new ArrayList<String>();
		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i).isChecked())
				ings.add(lista.get(i).getNombre());
		}
		return ings;
	}

	public void guardarCambios() {

		file.editarLista(getSelectedIngs(), nombreLista);
	}

	private void onLTRFling(int pos) {
		// Toast.makeText(getActivity(), ""+listViewIngs.getItemAtPosition(pos),
		// Toast.LENGTH_SHORT).show();

		crearDialogo(
				"¿Seguro que desea borrar \""
						+ listViewIngs.getItemAtPosition(pos) + "\"?",
				Mode.DIALOG_DELETE, pos).show();
	}

	private void onRTLFling(int pos) {
		// Toast.makeText(getActivity(), ""+listViewIngs.getItemAtPosition(pos),
		// Toast.LENGTH_SHORT).show();
		crearDialogo(
				"¿Seguro que desea borrar \""
						+ listViewIngs.getItemAtPosition(pos) + "\"?",
				Mode.DIALOG_DELETE, pos).show();
	}

	private void borrarIngrediente(int pos) {
		String ing = listViewIngs.getItemAtPosition(pos) + "";
		try {
			if (file.borrarIngrediente(ing)) {
				lista.remove(pos);
				adapter.setListCopy(lista);
				adapter.notifyDataSetChanged();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onRTLFling(listViewIngs.pointToPosition((int) e1.getX(),
						(int) e1.getY()));
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onLTRFling(listViewIngs.pointToPosition((int) e1.getX(),
						(int) e1.getY()));
			}
			return false;
		}

	}

}
