package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.ShowListaActivity.FragmentPagerAdapterListas;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;
import com.yoanaydavid.recetas.java.Paso;
import com.yoanaydavid.recetas.java.Receta;
import com.yoanaydavid.recetas.java.RecetaAdvanced;
import com.yoanaydavid.recetas.java.RecetaSimple;

public class ShowRecetaActivity extends FragmentActivity implements
		OnClickListener {
	

	private final static int SHOW_RECETA_ACTIVITY_CODE = 0;
	FileXML file;
	String recetaAnterior;
	String recetaActual;
	private int posicionRecetaActual;
	DetailsRecetasFragment fragmentToUpdate;
	FragmentPagerAdapterListas mAdapterListas;

	ViewPager mPager;
	
	List<String> recetas;

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
			//Recupero las recetas almacenadas
			try {
				recetas = file.leerElementos("recetas.xml", "receta");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				if (extras.getInt("mode") == Mode.SHOW) {

					// Modo mostrar receta
					posicionRecetaActual = extras.getInt("position", -1);
					
					if(posicionRecetaActual == -1){
						//Venimos de nueva receta, seleccionamos la ultima posicion
						posicionRecetaActual = recetas.size()-1;
					}
					recetaActual = recetas.get(posicionRecetaActual);
					
					
					setTitle(recetaActual);
					

				}

			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		
		mAdapterListas = new FragmentPagerAdapterListas(
				getSupportFragmentManager(), this, recetas);

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

				posicionRecetaActual = position;
				recetaActual = recetas.get(position);
				setTitle("Receta \""+recetaActual+"\"");
				
				

			}

		});
		mPager.setAdapter(mAdapterListas);
		mPager.setCurrentItem(posicionRecetaActual);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button b = (Button) v;

		switch (b.getId()) {
		case R.id.editButton:
			// Edit Button

			Intent intent = new Intent(this, NuevaRecetaActivity.class);

			intent.putExtra("mode", Mode.EDIT);
			intent.putExtra("nombre", recetaActual);
			// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, SHOW_RECETA_ACTIVITY_CODE);

			// Toast.makeText(this, "Edit Button", Toast.LENGTH_SHORT).show();
			break;
		case R.id.saveButton:
			if (b.getText().equals("Borrar")) {
				// Delete button

				crearDialogo(
						"¿Seguro que desea borrar la receta \"" + recetaActual
								+ "\"?", Mode.DIALOG_DELETE).show();

			}
			break;

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

							if (file.borrarListaCompra(recetaActual,
									"recetas.xml", "receta")) {

								Toast.makeText(getBaseContext(),
										"La lista se ha borrado correctamente",
										Toast.LENGTH_LONG).show();
								setResult(Mode.DELETE, new Intent().putExtra("position", posicionRecetaActual));
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

		if (requestCode == SHOW_RECETA_ACTIVITY_CODE && resultCode == Mode.SAVE) {
			// Venimos del modo editar y la receta ya se ha guardado, por lo
			// tanto la mosstramos en modo SHOW
			if (data != null) {
				recetaAnterior = recetaActual;
				recetaActual = data.getExtras().getString("nombre");
				try {
					setTitle(recetaActual);
					int modeReceta;
					Receta receta = file.leerReceta(recetaActual);
					receta.setNombre(recetaAnterior);
					if(receta instanceof RecetaSimple)
						modeReceta = Mode.RECETA_SIMPLE_MODE;
					else
						modeReceta = Mode.RECETA_ADVANCED_MODE;
					recetas.set(posicionRecetaActual, recetaActual);
					fragmentToUpdate = new DetailsRecetasFragment(this, Mode.SHOW, modeReceta, receta);
					mAdapterListas.notifyDataSetChanged();
					

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

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
	
	private void gestionarOnBack(){
		Intent intent = new Intent(this, RecetasActivity.class);
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
			String memberName = fragmentToUpdate.getReceta().getNombre();
			String pagerName = ((DetailsRecetasFragment) object).getReceta()
					.getNombre();
			// Log.v("TAG", memberName);
			// Log.v("TAG", pagerName);
	
			if (memberName.equals(pagerName))
				return POSITION_NONE;
	
			if (listas.get(0).equals(memberName))
				return POSITION_NONE;
	
			return POSITION_UNCHANGED;
		}
	
		@Override
		public Fragment getItem(int position) {
			Log.v("test", "Position: " + position);
			DetailsRecetasFragment df = null;
	
			/*
			 * ListasFragment lf = (ListasFragment) ListasActivity.this
			 * .getSupportFragmentManager().findFragmentById(
			 * R.id.fragmentListas); lf.getListView().setItemChecked(position,
			 * true);
			 */
			int modeReceta = -1;
			if (listas.size() > 0) {
				Receta receta = null;
				try {
					receta = file.leerReceta(listas.get(position));
					if (receta instanceof RecetaSimple)
						modeReceta = Mode.RECETA_SIMPLE_MODE;
					else
						modeReceta = Mode.RECETA_ADVANCED_MODE;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (receta != null) {
					df = new DetailsRecetasFragment(ShowRecetaActivity.this,
							Mode.SHOW, modeReceta, receta);
	
				} else {
					df = new DetailsRecetasFragment(ShowRecetaActivity.this,
							Mode.SHOW, Mode.RECETA_SIMPLE_MODE);
	
				}
			} else {
				df = new DetailsRecetasFragment(ShowRecetaActivity.this,
						Mode.SHOW, Mode.RECETA_SIMPLE_MODE);
			}
	
			return df;
		}
	}
}
