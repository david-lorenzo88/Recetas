package com.yoanaydavid.recetas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.ListasActivity.FragmentPagerAdapterListas;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Mode;

public class ShowListaActivity extends ActionBarActivity implements
		OnClickListener {
	FileXML file;
	private final static int LISTA_COMPRA_ACTIVITY_CODE = 0;
	private String listaActual;
	private int posicionListaActual;
	DetailsFragment fragmentToUpdate;
	FragmentPagerAdapterListas mAdapterListas;

	ViewPager mPager;
	
	List<String> listas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);
		file = new FileXML();
		/*
		 * if (getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_LANDSCAPE) { // If the screen is now in
		 * landscape mode, we can show the // dialog in-line with the list so we
		 * don't need this activity. finish(); return; }
		 */

		if (savedInstanceState == null) {
			//Recupero las listas almacenadas
			try {
				listas = file.leerElementos("listas.xml", "lista");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				if (extras.getInt("mode") == Mode.SHOW) {

					// Modo mostrar lista
					posicionListaActual = extras.getInt("position");
					listaActual = listas.get(posicionListaActual);
					setTitle(listaActual);
					
					
				} else if (extras.getInt("mode") == Mode.NEW) {
					// Modo guardar nueva lista
					DetailsFragment details = new DetailsFragment(this,
							Mode.NEW,
							extras.getStringArrayList("ingredientes"), null);

					getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, details).commit();

				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		
		mAdapterListas = new FragmentPagerAdapterListas(
				getSupportFragmentManager(), this, listas);

		mPager = (ViewPager) findViewById(R.id.pagerDetails);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				//Log.v("test", "Page Selected Position: " + position);

				posicionListaActual = position;
				listaActual = listas.get(position);
				
				
				

			}

		});
		mPager.setAdapter(mAdapterListas);
		mPager.setCurrentItem(posicionListaActual);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button b = (Button) v;

		switch (b.getId()) {
		case R.id.editButton:
			// Edit Button

			ArrayList<String> ingredientes = null;
			try {
				ingredientes = (ArrayList<String>) file
						.leerIngredientesLista(listaActual);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ingredientes != null) {

				Intent intent = new Intent(this, ListaCompraActivity.class);
				intent.putStringArrayListExtra("ingredientes", ingredientes);
				intent.putExtra("mode", Mode.EDIT);
				intent.putExtra("title", listaActual);
				// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent, LISTA_COMPRA_ACTIVITY_CODE);

			}

			// Toast.makeText(this, "Edit Button", Toast.LENGTH_SHORT).show();
			break;
		case R.id.saveButton:
			if (b.getText().equals("Borrar")) {
				// Delete button

				crearDialogo(
						"¿Seguro que desea borrar la lista \"" + listaActual
								+ "\"?", Mode.DIALOG_DELETE).show();

			} else {
				// Save button
				DetailsFragment details = (DetailsFragment) getSupportFragmentManager()
						.findFragmentById(android.R.id.content);
				
				listaActual = details.getNombreLista();
				if (listaActual.equals("")) {
					Toast.makeText(getBaseContext(),
							"¡Introduzca un nombre para la lista!",
							Toast.LENGTH_LONG).show();
					// return;
				} else {
					ArrayList<String> ings = details.getIngredientes();
					if (file.guardarLista(ings, listaActual)) {
						Toast.makeText(getBaseContext(),
								"¡Lista Guardada Correctamente!",
								Toast.LENGTH_LONG).show();
						listas.add(listaActual);
						mPager.setCurrentItem(listas.size()-1);
						
						getSupportFragmentManager().beginTransaction().remove(details).commit();

					} else {
						Toast.makeText(
								getBaseContext(),
								"¡Ha ocurrido un error! Pruebe con otro nombre",
								Toast.LENGTH_LONG).show();
					}
				}
				break;

			}
		}

	}

	public AlertDialog crearDialogo(String mensaje, int modo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(mensaje).setCancelable(false)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		switch (modo) {

		case Mode.DIALOG_DELETE:
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

							if (file.borrarListaCompra(listaActual,
									"listas.xml", "lista")) {

								Toast.makeText(getBaseContext(),
										"La lista se ha borrado correctamente",
										Toast.LENGTH_LONG).show();
								setResult(Mode.DELETE);
								finish();
							} else {
								Toast.makeText(getBaseContext(),
										"La lista no ha podido ser borrada",
										Toast.LENGTH_LONG).show();
							}
						}
					});
			break;

		}
		AlertDialog alert = builder.create();

		return alert;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == LISTA_COMPRA_ACTIVITY_CODE
				&& resultCode == Mode.SAVE) {
			// Venimos del modo editar y la lista ya se ha guardado, por lo
			// tanto la mostramos en modo SHOW
			if (data != null) {
				ArrayList<String> ingredientes = data
						.getStringArrayListExtra("ingredientes");
				if (ingredientes != null) {
					fragmentToUpdate = new DetailsFragment(this, Mode.SHOW, ingredientes, listaActual);
					mAdapterListas.notifyDataSetChanged();
				}
			}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			gestionarOnBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// do something on back.
		gestionarOnBack();
		return;
	}

	private void gestionarOnBack() {
		Intent intent = new Intent(this, ListasActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	class FragmentPagerAdapterListas extends FragmentStatePagerAdapter {
		Context context;
		List<String> listas;

		public FragmentPagerAdapterListas(FragmentManager fm, Context context,
				List<String> listas) {
			super(fm);
			this.context = context;
			this.listas = listas;
		}

		@Override
		public int getCount() {
			return listas.size();
		}

		@Override
		public int getItemPosition(Object object) {
			String memberName = fragmentToUpdate.nombre;
			String pagerName = ((DetailsFragment) object).nombre;
			Log.v("TAG", memberName);
			Log.v("TAG", pagerName);

			if (memberName.equals(pagerName))
				return POSITION_NONE;

			if (listas.get(0).equals(memberName))
				return POSITION_NONE;

			return POSITION_UNCHANGED;
		}

		@Override
		public Fragment getItem(int position) {
			Log.v("test", "Position: " + position);
			DetailsFragment df = null;

			/*
			 * ListasFragment lf = (ListasFragment) ListasActivity.this
			 * .getSupportFragmentManager().findFragmentById(
			 * R.id.fragmentListas); lf.getListView().setItemChecked(position,
			 * true);
			 */
			if (listas.size() > 0) {
				ArrayList<String> ings = null;
				try {
					ings = (ArrayList<String>) file
							.leerIngredientesLista(listas.get(position));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (ings != null) {
					df = new DetailsFragment(ShowListaActivity.this, Mode.SHOW,
							ings, listas.get(position));

				} else {
					df = new DetailsFragment(ShowListaActivity.this, Mode.SHOW);

				}
			} else {
				df = new DetailsFragment(ShowListaActivity.this, Mode.SHOW);
			}

			return df;
		}
	}
}
