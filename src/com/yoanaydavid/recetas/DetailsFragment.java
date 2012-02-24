package com.yoanaydavid.recetas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Mode;

/**
 * This is the secondary fragment, displaying the details of a particular item.
 */
public class DetailsFragment extends Fragment {
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */

	Button deleteAndSaveButton;
	Button editButton;
	EditText textField;
	WebView webView;
	FileXML file;
	ArrayList<String> ing = null;
	Context context;
	String nombre ="nada";
	// Indica el modo en el que se encuentra la Activity (Show / Edit)
	int mode;
	OnClickListener listener = null;


	public DetailsFragment(OnClickListener listener, int mode, ArrayList<String> ings, String nombre) {

		this.ing = ings;
		this.listener = listener;
		this.nombre = nombre;
		this.mode = mode;
		file = new FileXML();
	}
	public DetailsFragment(OnClickListener listener, int mode) {

		this.listener = listener;
		this.mode = mode;
		file = new FileXML();
	}
	
	
	
	public ArrayList<String> getIngredientes(){
		return ing;
	}
	
	public void setIngredientes(ArrayList<String> ing){
		this.ing = ing;
	}
	
	public String getNombreLista(){
		if(textField != null){
			return textField.getText().toString().trim();
		}
		return null;
	}

	public void makeAll() {
		
		if (mode == Mode.SHOW)
			showButtons();
		else if (mode == Mode.NEW){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				textField.setText(file.encontrarNombreSiguiente(sdf
						.format(new Date())));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		llenarTextView();
	}
	
	public void changeToShowMode(){
		
		mode = Mode.SHOW;
		textField.setText("");
		textField.setVisibility(View.GONE);
		
		
		deleteAndSaveButton.setText("Borrar");
		deleteAndSaveButton.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.erase, 0, 0, 0);
		
		deleteAndSaveButton.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 50));
		editButton.setVisibility(View.VISIBLE);
		editButton.setOnClickListener(listener);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		//if(savedInstanceState == null){
			
		//}
		super.onActivityCreated(savedInstanceState);
		if(ing!=null)
			makeAll();
		
		
		getActivity().setTitle("Recetas 2");
			
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		// Obtenemos el layout y a partir de él personalizamos la Views que
		// contiene
		View content = inflater.inflate(R.layout.mostrarlistacompra, container,
				false);

		textField = (EditText) content.findViewById(R.id.nombreListaText);

		deleteAndSaveButton = (Button) content.findViewById(R.id.saveButton);

		deleteAndSaveButton.setOnClickListener(listener);
		
		editButton = (Button) content.findViewById(R.id.editButton);
		
		webView = (WebView) content.findViewById(R.id.webView);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setBackgroundColor(0x00000000); //Background transparent
		if (mode == Mode.SHOW) {
			// modo mostrar lista
			changeToShowMode();
			deleteAndSaveButton.setVisibility(View.GONE);	
			editButton.setVisibility(View.GONE);
			webView.loadUrl("file:///android_asset/default.html");
		} 
		

		return content;

	}

	public void setDefaultValues() {
		hideButtons();
		webView.loadUrl("file:///android_asset/default.html");

	}

	private void showButtons() {
		deleteAndSaveButton.setVisibility(View.VISIBLE);
		editButton.setVisibility(View.VISIBLE);
	}

	private void hideButtons() {
		deleteAndSaveButton.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
	}

	public void llenarTextView() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body style=\"background-color:black; color:white;\"><h4>\""+((nombre!=null)? nombre : "Sin nombre")+"\"</h4>");
		buffer.append("<ul>");

		Iterator<String> it = ing.iterator();
		while (it.hasNext()) {
			buffer.append("<li>" + it.next() + "</li>");
		}
		buffer.append("</ul>");

		buffer.append("</body></html>");
		webView.loadDataWithBaseURL(null, buffer.toString(), "text/html",
				"utf-8", null);
	}

}
