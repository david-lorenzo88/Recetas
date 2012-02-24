package com.yoanaydavid.recetas;

import com.yoanaydavid.recetas.java.Mode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends ActionBarActivity {


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	public void verRecetasGuardadasOnClick(View v) {
		Intent intent = new Intent(this, RecetasActivity.class);
		intent.putExtra("mode", Mode.SHOW);
		startActivity(intent);
	}
	
	public void recetaOnClick(View v) {
		// do something when the button is clicked
		Intent intent = new Intent(this, RecetasActivity.class);
		intent.putExtra("mode", Mode.NEW);
		startActivity(intent);
	}

	public void listaCompraOnClick(View v) {
		// do something when the button is clicked

		Intent intent = new Intent(this, ListasActivity.class);
		intent.putExtra("mode", Mode.NEW); // Modo listas
		startActivity(intent);

	}

	public void verListasGuardadasOnClick(View v) {
		// do something when the button is clicked
		Intent intent = new Intent(this, ListasActivity.class);
		intent.putExtra("mode", Mode.SHOW); // Modo listas
		startActivity(intent);
	}

}