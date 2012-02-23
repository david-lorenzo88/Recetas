package com.yoanaydavid.recetas;


import com.yoanaydavid.recetas.java.Mode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity {

	Button btnReceta;
	Button btnListaCompra;
	Button btnVerListasGuardadas;
	Main main;

	// Create an anonymous implementation of OnClickListener
	private OnClickListener recetaOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// do something when the button is clicked
			Intent intent = new Intent(main, RecetasActivity.class);
			intent.putExtra("mode", Mode.NEW);
			startActivity(intent);
		}
	};
	// Create an anonymous implementation of OnClickListener
	private OnClickListener listaCompraOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// do something when the button is clicked

			Intent intent = new Intent(main, ListasActivity.class);
			intent.putExtra("mode", Mode.NEW); // Modo listas
			startActivity(intent);

		}
	};
	// Create an anonymous implementation of OnClickListener
	private OnClickListener verListasGuardadasOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// do something when the button is clicked
			Intent intent = new Intent(main, ListasActivity.class);
			intent.putExtra("mode", Mode.SHOW); // Modo listas
			startActivity(intent);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Guardo en main el objeto actual para poder usarlo en los Listeners
		main = this;

		btnReceta = (Button) this.findViewById(R.id.RecetaButton);
		btnListaCompra = (Button) this.findViewById(R.id.ListaCompraButton);

		btnReceta.setOnClickListener(recetaOnClick);
		btnListaCompra.setOnClickListener(listaCompraOnClick);
		btnVerListasGuardadas = (Button) findViewById(R.id.VerListasButton);
		btnVerListasGuardadas.setOnClickListener(verListasGuardadasOnClick);

	}

	public void verRecetasGuardadasOnClick(View v) {
		Intent intent = new Intent(main, RecetasActivity.class);
		intent.putExtra("mode", Mode.SHOW);
		startActivity(intent);
	}

}