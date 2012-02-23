package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;

public class ListaCompraActivity extends ActionBarActivity implements
		OnClickListener {
	private String nombreLista = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				if (extras.getInt("mode") == Mode.EDIT) {
					nombreLista = extras.getString("title");
					EditFragment details = new EditFragment(Mode.EDIT, this,
							extras.getStringArrayList("ingredientes"),
							nombreLista);

					getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, details).commit();
				} else if (extras.getInt("mode") == Mode.NEW){
					EditFragment ef = new EditFragment(Mode.NEW, this);
					
					getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, ef).commit();
				}
			}
		}

	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button b = (Button) v;
		switch (b.getId()) {
		case R.id.crearListaButton:
			EditFragment editFragment = (EditFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
			ArrayList<String> ings = (ArrayList<String>) editFragment
					.getSelectedIngs();
			if (editFragment.getMode() == Mode.EDIT) {
				// Modo editar, boton guardar lista

				editFragment.guardarCambios();
				editFragment.setMode(Mode.SHOW);

				setResult(Mode.SAVE, new Intent().putStringArrayListExtra(
						"ingredientes", ings));

				finish();
				
			} else {
				// Modo nueva lista, boton crear lista
				Intent intent = new Intent(this, ShowListaActivity.class);
				intent.putStringArrayListExtra("ingredientes", ings);
				intent.putExtra("mode", Mode.NEW);
				startActivity(intent);

			}
			break;
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("test", Integer.toString(item.getItemId()));
		// return true;

		switch (item.getItemId()) {
		case 16908332: // android.R.id.home
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, Main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_new:
            Toast.makeText(this, "Tapped new", Toast.LENGTH_SHORT).show();
            
            return true;

        case R.id.menu_search:
            Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
            return true;

        case R.id.menu_share:
            Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
