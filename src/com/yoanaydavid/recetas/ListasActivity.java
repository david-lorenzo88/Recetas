package com.yoanaydavid.recetas;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Mode;

/**
 * Demonstration of using fragments to implement different activity layouts.
 * This sample provides a different layout (and activity flow) when run in
 * landscape.
 */
public class ListasActivity extends ActionBarActivity implements ListasListener,
		OnClickListener {
	private boolean mDualPane = false;

	FileXML file = new FileXML();
	private final static int SHOW_LISTA_ACTIVITY_CODE = 0;
	private final static int SHOW_RECETA_ACTIVITY_CODE = 3;
	private final static int LISTA_COMPRA_ACTIVITY_CODE = 0;
	String listaActual = null;
	String listaAnterior = null;
	int mode = -1;
	DetailsFragment fragmentToUpdate;
	FragmentPagerAdapterListas mAdapterListas;

	ViewPager mPager;

	public boolean ismDualPane() {
		return mDualPane;
	}

	public void setmDualPane(boolean mDualPane) {
		this.mDualPane = mDualPane;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.v("test", "Hasta aqui");
		setContentView(R.layout.fragment_listas);

		ListasFragment lista = (ListasFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentListas);

		lista.setListasListener(this);

		View details = findViewById(R.id.details);
		mDualPane = details != null && details.getVisibility() == View.VISIBLE;

		// Si no estamos en doble panel y el panel 'details' != null, lo
		// ocultamos
		Bundle extras = getIntent().getExtras();

		if (mDualPane) {
			// Dual panel

			if (details != null) {
				if (extras != null) {

					switch (extras.getInt("mode")) {
					case Mode.SHOW:
						// Modo ver listas guardadas

						mode = Mode.SHOW;
						break;
					case Mode.NEW:
						// Modo nueva lista de la compra
						EditFragment ef = new EditFragment(Mode.NEW, this);
						getSupportFragmentManager()
								.beginTransaction()
								.replace(R.id.details, ef)
								.setTransition(
										FragmentTransaction.TRANSIT_FRAGMENT_FADE)
								.commit();

						mode = Mode.NEW;
						break;

					}
				}

			}

		} else {
			// Single panel
			if (details != null) {
				details.setVisibility(View.GONE);
			}

			if (extras != null && extras.getInt("mode") == Mode.NEW) {
				// Estamos en modo nueva receta, lanzamos la activity
				Intent intent = new Intent(this, ListaCompraActivity.class);

				intent.putExtra("mode", Mode.NEW);

				startActivity(intent);

			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDualPane) {
			ListasFragment lf = (ListasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentListas);
			mAdapterListas = new FragmentPagerAdapterListas(
					getSupportFragmentManager(), this, lf.elementos);

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
					Log.v("test", "Page Selected Position: " + position);

					ListasFragment lf = (ListasFragment) ListasActivity.this
							.getSupportFragmentManager().findFragmentById(
									R.id.fragmentListas);
					lf.getListView().setItemChecked(position, true);
					lf.mCurCheckPosition = position;
					listaAnterior = listaActual;
					listaActual = lf.elementos.get(position);
					Log.v("test", "Lista anterior: " + listaAnterior);
					Log.v("test", "Lista actual: " + listaActual);

				}

			});
			mPager.setAdapter(mAdapterListas);
			// mPager.setCurrentItem(-1, true);
			if (getIntent().getExtras().getInt("mode") == Mode.SHOW) {
				switchPagerFragment(Mode.PAGER);
				if (lf.elementos.size() > 0) {
					// Hay alguna lista
					lf.getListView().setItemChecked(0, true);
					listaActual = lf.elementos.get(0);
				}

			}
		}
	}

	public void switchPagerFragment(int mode) {
		switch (mode) {
		case Mode.PAGER:
			// Pager visible, fragment gone

			findViewById(R.id.details).setVisibility(View.GONE);
			mPager.setVisibility(View.VISIBLE);
			break;
		case Mode.FRAGMENT:
			findViewById(R.id.details).setVisibility(View.VISIBLE);
			mPager.setVisibility(View.GONE);
			break;

		}
	}

	@Override
	public void onListaSelected(int index, List<String> elementos) {
		// TODO Auto-generated method stub

		Log.v("test", "Mode: " + mode);

		try {

			if (mDualPane) {

				if (mode == Mode.NEW) {

					crearDialogo(
							"¿Seguro que desea salir? Se perderán los cambios no guardados.",
							Mode.DIALOG_FINISH, index, elementos).show();

				} else {

					if (mode == Mode.EDIT) {
						EditFragment ef = (EditFragment) getSupportFragmentManager()
								.findFragmentById(R.id.details);
						getSupportFragmentManager().beginTransaction()
								.remove(ef).commit();
					}

					Log.v("test", "index: " + index);

					switchPagerFragment(Mode.PAGER);
					mPager.setCurrentItem(index);

					if (listaActual == null) {
						listaActual = elementos.get(index);
					}

					mode = Mode.SHOW;
				}

			} else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				listaActual = elementos.get(index);
				ArrayList<String> ingredientes = (ArrayList<String>) file
						.leerIngredientesLista(listaActual);
				Intent intent = new Intent(this, ShowListaActivity.class);
				intent.putStringArrayListExtra("ingredientes", ingredientes);
				intent.putExtra("mode", Mode.SHOW);
				intent.putExtra("position", index);
				startActivityForResult(intent, SHOW_LISTA_ACTIVITY_CODE);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				// Log.v("test", "Lista Actual File: "+listaActual);
				ingredientes = (ArrayList<String>) file
						.leerIngredientesLista(listaActual);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ingredientes != null) {
				if (mDualPane) {
					switchPagerFragment(Mode.FRAGMENT);
					EditFragment editFragment = new EditFragment(Mode.EDIT,
							this, ingredientes, listaActual);

					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.details, editFragment);
					transaction
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					transaction.commit();

					mode = Mode.EDIT;
				} else {
					Intent intent = new Intent(this, ListaCompraActivity.class);
					intent.putStringArrayListExtra("ingredientes", ingredientes);
					intent.putExtra("mode", Mode.EDIT);
					intent.putExtra("title", listaActual);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivityForResult(intent, LISTA_COMPRA_ACTIVITY_CODE);
				}
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
						.findFragmentById(R.id.details);

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

						ListasFragment lf = (ListasFragment) getSupportFragmentManager()
								.findFragmentById(R.id.fragmentListas);
						int sizeBeforeAdd = lf.getListView().getAdapter()
								.getCount();
						lf.addItemToList(listaActual);
						lf.updateList();
						lf.mCurCheckPosition = (sizeBeforeAdd > 0) ? sizeBeforeAdd + 1
								: 0;

						if (sizeBeforeAdd <= 0) {
							lf.setItemChecked(0);
						}
						// details.changeToShowMode();

						// Quito de la pila el fragment anterior (nueva lista)
						// getSupportFragmentManager().popBackStack();

						// Volvemos a crear el fragment Details con la nueva
						// lista en modo SHOW
						details = new DetailsFragment(this, Mode.SHOW, ings,
								listaActual);

						switchPagerFragment(Mode.PAGER);
						mPager.setCurrentItem(lf.mCurCheckPosition);

						getSupportFragmentManager()
								.beginTransaction()
								.remove(getSupportFragmentManager()
										.findFragmentById(R.id.details))
								.commit();
						// Execute a transaction, replacing any existing
						// fragment
						// with this one inside the frame.
						/*
						 * FragmentTransaction ft = getSupportFragmentManager()
						 * .beginTransaction();
						 * 
						 * ft.replace(R.id.details, details);
						 * ft.setTransition(FragmentTransaction
						 * .TRANSIT_FRAGMENT_FADE); ft.commit();
						 */
						mode = Mode.SHOW;

						// getSupportFragmentManager().executePendingTransactions();

						// details.makeAll(ings);

					} else {
						Toast.makeText(
								getBaseContext(),
								"¡Ha ocurrido un error! Pruebe con otro nombre",
								Toast.LENGTH_LONG).show();
					}
				}
				// Toast.makeText(this, "Save Button",
				// Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.crearListaButton:
			EditFragment editFragment = (EditFragment) getSupportFragmentManager()
					.findFragmentById(R.id.details);
			ArrayList<String> ings = (ArrayList<String>) editFragment
					.getSelectedIngs();
			String nombre = editFragment.getNombreLista();
			if (editFragment.getMode() == Mode.EDIT) {
				// Modo editar, boton guardar lista

				editFragment.guardarCambios();
				editFragment.setMode(Mode.SHOW);

				ListasFragment lf = (ListasFragment) getSupportFragmentManager()
						.findFragmentById(R.id.fragmentListas);
				// fragmentToUpdate =
				// mAdapterListas.getItem(lf.mCurCheckPosition);
				Log.v("test", "mCurCheckPosition: " + lf.mCurCheckPosition);
				fragmentToUpdate = (DetailsFragment) mAdapterListas
						.getItem(lf.mCurCheckPosition);
				mAdapterListas.notifyDataSetChanged();

				switchPagerFragment(Mode.PAGER);
				mPager.setCurrentItem(lf.mCurCheckPosition);

				// Cambiamos a modo Show
				mode = Mode.SHOW;
				// Execute a transaction, replacing any existing
				// fragment
				// with this one inside the frame.

				/*
				 * FragmentTransaction ft = getSupportFragmentManager()
				 * .beginTransaction(); ft.replace(R.id.details, details);
				 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				 * ft.commit();
				 */

				// getSupportFragmentManager().executePendingTransactions();

				// details.makeAll(ings);
			} else {
				// Modo nueva lista, boton crear lista
				DetailsFragment details = new DetailsFragment(this, Mode.NEW,
						ings, null);

				// Execute a transaction, replacing any existing
				// fragment
				// with this one inside the frame.
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.replace(R.id.details, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.addToBackStack(null);

				ft.commit();

				// getSupportFragmentManager().executePendingTransactions();

				// details.makeAll(ings);

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

							if (file.borrarListaCompra(listaActual,
									"listas.xml", "lista")) {

								ListasFragment lista = (ListasFragment) getSupportFragmentManager()
										.findFragmentById(R.id.fragmentListas);

								lista.removeItemFromList(listaActual);

								lista.updateList();
								// mAdapterListas.notifyDataSetChanged();

								// Recupero los ingredientes de la posicion 0
								// actual
								if (lista.elementos.size() > 0) {
									ArrayList<String> ings = null;
									try {
										ings = (ArrayList<String>) file
												.leerIngredientesLista(lista.elementos
														.get(0));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									fragmentToUpdate = new DetailsFragment(
											ListasActivity.this, Mode.SHOW,
											ings, lista.elementos.get(0));
									mAdapterListas.notifyDataSetChanged();
									mPager.setCurrentItem(0);
								}
								Toast.makeText(getBaseContext(),
										"La lista se ha borrado correctamente",
										Toast.LENGTH_LONG).show();

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

	public AlertDialog crearDialogo(String mensaje, int modo, int index,
			List<String> elementos) {
		final List<String> listas = elementos;
		final int pos = index;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(mensaje).setCancelable(false)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						ListasFragment lf = (ListasFragment) ListasActivity.this
								.getSupportFragmentManager().findFragmentById(
										R.id.fragmentListas);
						lf.getListView().setItemChecked(pos, false);
					}
				});
		switch (modo) {

		case Mode.DIALOG_FINISH:
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							switchPagerFragment(Mode.PAGER);
							mPager.setCurrentItem(pos);

							if (listaActual == null) {
								listaActual = listas.get(pos);
							}

							mode = Mode.SHOW;

						}
					});
			break;

		}
		AlertDialog alert = builder.create();

		return alert;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Mode.DELETE) {
			ListasFragment lista = (ListasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentListas);
			lista.removeItemFromList(listaActual);
			lista.updateList();

		}
		/*
		 * if(resultCode == Mode.SAVE){ try { elementos =
		 * file.leerElementos("recetas.xml", "receta"); setListAdapter(new
		 * ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
		 * elementos)); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * 
		 * }
		 */

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
					df = new DetailsFragment(ListasActivity.this, Mode.SHOW,
							ings, listas.get(position));

				} else {
					df = new DetailsFragment(ListasActivity.this, Mode.SHOW);

				}
			} else {
				df = new DetailsFragment(ListasActivity.this, Mode.SHOW);
			}

			return df;
		}
	}

}