package com.yoanaydavid.recetas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yoanaydavid.recetas.ListasActivity.FragmentPagerAdapterListas;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;
import com.yoanaydavid.recetas.java.Paso;
import com.yoanaydavid.recetas.java.Receta;
import com.yoanaydavid.recetas.java.RecetaAdvanced;
import com.yoanaydavid.recetas.java.RecetaSimple;

public class RecetasActivity extends ActionBarActivity implements
		ListasListener, OnClickListener {
	final static int EDIT_ID = 0;
	final static int DELETE_ID = 1;
	final static int NUEVO_PASO_ACTIVITY_CODE = 5;
	final static int RECETA_ACTIVITY_CODE = 4;
	final static int SELECT_PHOTO = 6;
	FileXML file = new FileXML();
	final CharSequence[] items = { "Desde archivo", "Desde cámara" };
	String recetaActual = null;
	String recetaAnterior = null;
	int mode = -1;

	DetailsRecetasFragment fragmentToUpdate;
	FragmentPagerAdapterListas mAdapterListas;

	ViewPager mPager;

	private boolean mDualPane = false;

	public boolean ismDualPane() {
		return mDualPane;
	}

	public void setmDualPane(boolean mDualPane) {
		this.mDualPane = mDualPane;
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_recetas);

		RecetasFragment lista = (RecetasFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentRecetas);

		lista.setListasListener(this);

		// Fragment
		// f=getSupportFragmentManager().findFragmentById(R.id.details);
		View details = findViewById(R.id.detailsRecetas);
		mDualPane = details != null && details.getVisibility() == View.VISIBLE;
		/*
		 * Fragment details = getSupportFragmentManager().findFragmentById(
		 * R.id.details);
		 * 
		 * mDualPane = details != null;
		 */
		// Log.v("test", Boolean.toString(mDualPane));
		// Si no estamos en doble panel y el panel 'details' != null, lo
		// ocultamos
		Bundle extras = getIntent().getExtras();

		if (mDualPane) {
			// Dual panel

			if (details != null) {
				if (extras != null) {
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();

					switch (extras.getInt("mode")) {
					case Mode.SHOW:
						// Modo ver listas guardadas
						mode = Mode.SHOW;
						DetailsRecetasFragment df = new DetailsRecetasFragment(
								this, Mode.SHOW, Mode.RECETA_UNKNOWN_MODE);

						ft.replace(R.id.detailsRecetas, df);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();
						break;
					case Mode.NEW:
						// Modo nueva receta
						mode = Mode.NEW;
						EditRecetasFragment ef = new EditRecetasFragment(
								Mode.NEW, Mode.RECETA_UNKNOWN_MODE, this);

						ft.replace(R.id.detailsRecetas, ef);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();
						break;

					}
				}

			}

		} else {
			// Single panel

			if (details != null) {
				details.setVisibility(View.GONE);
			}
			if (extras != null && extras.getInt("mode") == Mode.NEW) { // Estamos
																		// en
																		// modo
																		// nueva
				// receta, lanzamos la activity
				Intent intent = new Intent(this, NuevaRecetaActivity.class);

				intent.putExtra("mode", Mode.NEW);

				startActivity(intent);

			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDualPane) {
			RecetasFragment rf = (RecetasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentRecetas);
			mAdapterListas = new FragmentPagerAdapterListas(
					getSupportFragmentManager(), this, rf.elementos);

			mPager = (ViewPager) findViewById(R.id.pagerDetailsRecetas);
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
					// Log.v("test", "Page Selected Position: " + position);

					RecetasFragment rf = (RecetasFragment) RecetasActivity.this
							.getSupportFragmentManager().findFragmentById(
									R.id.fragmentRecetas);
					rf.getListView().setItemChecked(position, true);
					rf.mCurCheckPosition = position;
					recetaAnterior = recetaActual;
					recetaActual = rf.elementos.get(position);
					// Log.v("test", "Lista anterior: " + recetaAnterior);
					// Log.v("test", "Lista actual: " + recetaActual);

				}

			});
			mPager.setAdapter(mAdapterListas);
			// mPager.setCurrentItem(-1, true);
			if (getIntent().getExtras().getInt("mode") == Mode.SHOW) {
				switchPagerFragment(Mode.PAGER);
				if (rf.elementos.size() > 0) {
					// Hay alguna lista
					rf.getListView().setItemChecked(0, true);
					recetaActual = rf.elementos.get(0);
				}

			}
		}
	}

	public void switchPagerFragment(int mode) {
		switch (mode) {
		case Mode.PAGER:
			// Pager visible, fragment gone

			findViewById(R.id.detailsRecetas).setVisibility(View.GONE);
			mPager.setVisibility(View.VISIBLE);
			break;
		case Mode.FRAGMENT:
			findViewById(R.id.detailsRecetas).setVisibility(View.VISIBLE);
			mPager.setVisibility(View.GONE);
			break;

		}
	}

	@Override
	public void onListaSelected(int index, List<String> elementos) {
		// TODO Auto-generated method stub
		// Log.v("test", Boolean.toString(mDualPane));

		// recetaAnterior = recetaActual;
		// recetaActual = elementos.get(index);

		// Log.v("test", recetaActual);

		// mode = Mode.SHOW;

		if (mDualPane) {

			if (mode == Mode.NEW) {

				crearDialogo(
						"¿Seguro que desea salir? Se perderán los cambios no guardados.",
						Mode.DIALOG_FINISH, index, elementos).show();

			} else {
				if (mode == Mode.EDIT) {

					getSupportFragmentManager()
							.beginTransaction()
							.remove(getSupportFragmentManager()
									.findFragmentById(R.id.detailsRecetas))
							.commit();
				}

				// Log.v("test", "index: " + index);

				switchPagerFragment(Mode.PAGER);
				mPager.setCurrentItem(index);

				if (recetaActual == null) {
					recetaActual = elementos.get(index);
				}

				mode = Mode.SHOW;
			}

		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			recetaActual = elementos.get(index);

			Intent intent = new Intent(this, ShowRecetaActivity.class);

			intent.putExtra("mode", Mode.SHOW);
			intent.putExtra("position", index);
			startActivityForResult(intent, RECETA_ACTIVITY_CODE);

		}

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); crearMenu(menu); return true; }
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Button b = (Button) v;

		switch (b.getId()) {
		case R.id.editButton:
			// Edit Button
			Receta receta = null;
			try {
				receta = file.leerReceta(recetaActual);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (receta != null) {
				if (mDualPane) {
					switchPagerFragment(Mode.FRAGMENT);
					EditRecetasFragment editFragment = new EditRecetasFragment(
							Mode.EDIT, file.getRecetaMode(recetaActual), this,
							receta);
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.detailsRecetas, editFragment);
					transaction
							.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					transaction.commit();

					mode = Mode.EDIT;
				} else {
					// Start activity
				}
			}

			break;
		case R.id.saveButton:
			if (b.getText().equals("Borrar")) {
				// Delete button
				crearDialogo(
						"¿Seguro que desea borrar la receta \"" + recetaActual
								+ "\"?", Mode.DIALOG_DELETE).show();

			}
			break;
		case R.id.btnSaveList:
			EditRecetasFragment ef = (EditRecetasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.detailsRecetas);
			String nombre = ef.getTxtNombreReceta().getText().toString().trim();
			// Compruebo si hay ingredientes
			if (!(ef.getListIngs().size() > 0)) {
				// No hay ingredientes
				Toast.makeText(getBaseContext(),
						"¡Debe rellenar los ingredientes!", Toast.LENGTH_SHORT)
						.show();

			} else if (ef.getModeReceta() == Mode.RECETA_SIMPLE_MODE) {
				// Modo Simple
				String elab = ef.getTxtRecetaElaboracionSimple().getText()
						.toString().trim();
				if (elab.equals("")) {
					// No hay elaboracion
					Toast.makeText(getBaseContext(),
							"¡Debe introducir una elaboración!",
							Toast.LENGTH_SHORT).show();

				} else if (nombre.equals("")) {
					// No hay un nombre para la receta
					Toast.makeText(getBaseContext(),
							"¡Debe introducir un nombre para la receta!",
							Toast.LENGTH_SHORT).show();
				} else {
					// Tenemos nombre, ingredientes y elaboracion: Guardamos
					// la
					// receta

					if (ef.getMode() == Mode.EDIT) {
						// Estamos en modo editar receta

						if (file.borrarListaCompra(recetaActual, "recetas.xml",
								"receta"))
							if (file.guardarRecetaModoSimple(ef.getListIngs(),
									nombre, elab)) {
								// Guardamos
								Toast.makeText(getBaseContext(),
										"¡Receta Guardada!", Toast.LENGTH_LONG)
										.show();

								RecetasFragment rf = (RecetasFragment) getSupportFragmentManager()
										.findFragmentById(R.id.fragmentRecetas);
								// Volvemos a modo SHOW
								ef.setMode(Mode.SHOW);

								fragmentToUpdate = new DetailsRecetasFragment(
										this, Mode.SHOW,
										Mode.RECETA_SIMPLE_MODE,
										new RecetaSimple(nombre,
												ef.getListIngs(), elab));

								mAdapterListas.notifyDataSetChanged();

								switchPagerFragment(Mode.PAGER);
								mPager.setCurrentItem(rf.mCurCheckPosition);

								mode = Mode.SHOW;

							} else {
								// El nombre ya existe
								Toast.makeText(getBaseContext(),
										"¡El nombre ya existe!",
										Toast.LENGTH_LONG).show();
							}
						else
							Toast.makeText(getBaseContext(),
									"¡La receta no ha podido ser modificada!",
									Toast.LENGTH_LONG).show();
					} else {
						// Estamos en modo nueva receta
						if (file.guardarRecetaModoSimple(ef.getListIngs(),
								nombre, elab)) {
							// Guardamos
							Toast.makeText(getBaseContext(),
									"¡Receta Guardada!", Toast.LENGTH_LONG)
									.show();
							// Actualizamos la lista de recetas
							RecetasFragment rf = (RecetasFragment) getSupportFragmentManager()
									.findFragmentById(R.id.fragmentRecetas);
							int sizeBeforeAdd = rf.getListView().getAdapter()
									.getCount();
							rf.addItemToList(nombre);
							rf.updateList();
							rf.mCurCheckPosition = (sizeBeforeAdd > 0) ? sizeBeforeAdd + 1
									: 0;

							if (sizeBeforeAdd <= 0) {
								rf.setItemChecked(0);
							}

							switchPagerFragment(Mode.PAGER);
							mPager.setCurrentItem(rf.mCurCheckPosition);

							getSupportFragmentManager()
									.beginTransaction()
									.remove(getSupportFragmentManager()
											.findFragmentById(
													R.id.detailsRecetas))
									.commit();

							// Iniciar detailsrecetasfragment
							/*
							 * ef.setMode(Mode.SHOW); mode = Mode.SHOW;
							 * DetailsRecetasFragment df = new
							 * DetailsRecetasFragment( this, Mode.SHOW,
							 * Mode.RECETA_SIMPLE_MODE, new RecetaSimple(nombre,
							 * ef.getListIngs(), elab));
							 * 
							 * // Iniciamos la transaccion FragmentTransaction
							 * ft = getSupportFragmentManager()
							 * .beginTransaction();
							 * ft.replace(R.id.detailsRecetas, df);
							 * ft.setTransition
							 * (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							 * ft.commit();
							 */

						} else {
							// El nombre ya existe
							Toast.makeText(getBaseContext(),
									"¡El nombre ya existe!", Toast.LENGTH_LONG)
									.show();
						}
					}

				}

			} else if (ef.getModeReceta() == -1
					|| ef.getModeReceta() == Mode.RECETA_UNKNOWN_MODE) {
				// No se ha elegido el modo con lo cual no se ha introducido
				// una
				// elaboracion
				Toast.makeText(getBaseContext(),
						"¡Debe introducir una elaboración!", Toast.LENGTH_SHORT)
						.show();
			} else if (ef.getModeReceta() == Mode.RECETA_ADVANCED_MODE) {
				// Modo avanzado

				if (ef.getListPasos().size() <= 0) {
					// No hay elaboracion
					Toast.makeText(getBaseContext(),
							"¡Debe introducir los pasos de la elaboración!",
							Toast.LENGTH_SHORT).show();

				} else if (nombre.equals("")) {
					// No hay un nombre para la receta
					Toast.makeText(getBaseContext(),
							"¡Debe introducir un nombre para la receta!",
							Toast.LENGTH_SHORT).show();
				} else {
					// Tenemos nombre, ingredientes y elaboracion: Guardamos
					// la
					// receta

					if (ef.getMode() == Mode.EDIT) {
						// Estamos en modo editar receta

						if (file.borrarListaCompra(recetaActual, "recetas.xml",
								"receta"))
							if (file.guardarRecetaModoAvanzado(
									ef.getListIngs(), ef.getListPasos(), nombre)) {
								// Guardamos

								Toast.makeText(getBaseContext(),
										"¡Receta Guardada!", Toast.LENGTH_LONG)
										.show();
								RecetasFragment rf = (RecetasFragment) getSupportFragmentManager()
										.findFragmentById(R.id.fragmentRecetas);
								// Volvemos a modo SHOW
								ef.setMode(Mode.SHOW);
								mode = Mode.SHOW;
								fragmentToUpdate = new DetailsRecetasFragment(
										this, Mode.SHOW,
										Mode.RECETA_ADVANCED_MODE,
										new RecetaAdvanced(nombre,
												ef.getListIngs(),
												ef.getListPasos()));

								mAdapterListas.notifyDataSetChanged();

								switchPagerFragment(Mode.PAGER);
								mPager.setCurrentItem(rf.mCurCheckPosition);

								mode = Mode.SHOW;

							} else {
								// El nombre ya existe
								Toast.makeText(getBaseContext(),
										"¡El nombre ya existe!",
										Toast.LENGTH_LONG).show();
							}
						else
							Toast.makeText(getBaseContext(),
									"¡La receta no ha podido ser modificada!",
									Toast.LENGTH_LONG).show();
					} else {
						// Estamos en modo nueva receta
						if (file.guardarRecetaModoAvanzado(ef.getListIngs(),
								ef.getListPasos(), nombre)) {
							// Guardamos

							Toast.makeText(getBaseContext(),
									"¡Receta Guardada!", Toast.LENGTH_LONG)
									.show();

							ef.clearAllTextFields();

							// Actualizamos la lista de recetas
							RecetasFragment rf = (RecetasFragment) getSupportFragmentManager()
									.findFragmentById(R.id.fragmentRecetas);
							int sizeBeforeAdd = rf.getListView().getAdapter()
									.getCount();
							rf.addItemToList(nombre);
							rf.updateList();
							rf.mCurCheckPosition = (sizeBeforeAdd > 0) ? sizeBeforeAdd + 1
									: 0;

							if (sizeBeforeAdd <= 0) {
								rf.setItemChecked(0);
							}

							switchPagerFragment(Mode.PAGER);
							mPager.setCurrentItem(rf.mCurCheckPosition);

							getSupportFragmentManager()
									.beginTransaction()
									.remove(getSupportFragmentManager()
											.findFragmentById(
													R.id.detailsRecetas))
									.commit();

							// Iniciar detailsrecetasfragment
							/*
							 * ef.setMode(Mode.SHOW); mode = Mode.SHOW;
							 * DetailsRecetasFragment df = new
							 * DetailsRecetasFragment( this, Mode.SHOW,
							 * Mode.RECETA_ADVANCED_MODE, new
							 * RecetaAdvanced(nombre, (ArrayList<Ingrediente>)
							 * ef .getListIngs().clone(), (ArrayList<Paso>) ef
							 * .getListPasos().clone()));
							 * 
							 * // Iniciamos la transaccion FragmentTransaction
							 * ft = getSupportFragmentManager()
							 * .beginTransaction();
							 * ft.replace(R.id.detailsRecetas, df);
							 * ft.setTransition
							 * (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							 * ft.commit(); // Actualizamos la lista de recetas
							 * RecetasFragment rf = (RecetasFragment)
							 * getSupportFragmentManager()
							 * .findFragmentById(R.id.fragmentRecetas);
							 * rf.addItemToList(nombre); rf.updateList();
							 * 
							 * ef.getListIngs().clear();
							 * ef.getAdapter().notifyDataSetChanged();
							 * 
							 * ef.getListPasos().clear();
							 * ef.getAdapterPasos().notifyDataSetChanged();
							 */

						} else {
							// El nombre ya existe
							Toast.makeText(getBaseContext(),
									"¡El nombre ya existe!", Toast.LENGTH_LONG)
									.show();
						}
					}

				}
			}

			break;
		case R.id.imagenButton:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Selecciona una fuente");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					NuevoPasoFragment npf = (NuevoPasoFragment) getSupportFragmentManager()
							.findFragmentById(R.id.detailsRecetas);
					switch (item) {
					case 0:
						Intent photoPickerIntent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, SELECT_PHOTO);

						// Desde archivo (Fragment)
						// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
						/*
						 * ImagesBgFragment bgF = new ImagesBgFragment(
						 * RecetasActivity.this); FragmentTransaction ft =
						 * getSupportFragmentManager() .beginTransaction();
						 * ft.replace(R.id.detailsRecetas, bgF);
						 * ft.setTransition
						 * (FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						 * ft.addToBackStack(null); ft.commit();
						 */
						/*
						 * Intent intent = new Intent(getBaseContext(),
						 * ImagesBgActivity.class);
						 * startActivityForResult(intent,
						 * NUEVO_PASO_ACTIVITY_CODE);
						 */
						break;
					case 1:
						// Desde camara (Intent)
						npf.dispatchTakePictureIntent(NUEVO_PASO_ACTIVITY_CODE);
						break;
					}
				}
			});
			AlertDialog alert = builder.create();

			alert.show();
			break;
		case R.id.btnCrearPaso:
			EditRecetasFragment er = (EditRecetasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.detailsRecetas);
			er.setmSaveState(true);
			NuevoPasoFragment npf = new NuevoPasoFragment(this);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.detailsRecetas, npf);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();
			break;
		case R.id.guardarPasoButton:
			// Comprobamos que tenemos una descripcion del paso
			NuevoPasoFragment np = (NuevoPasoFragment) getSupportFragmentManager()
					.findFragmentById(R.id.detailsRecetas);
			String desc = np.getTxtDescripcion().getText().toString().trim();
			if (!desc.equals("")) {
				// Ocultamos el teclado
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(np.getTxtDescripcion()
						.getWindowToken(), 0);

				getSupportFragmentManager().popBackStack();
				getSupportFragmentManager().executePendingTransactions();
				EditRecetasFragment erf = (EditRecetasFragment) getSupportFragmentManager()
						.findFragmentById(R.id.detailsRecetas);
				// erf.setmSaveState(false);
				erf.changeMode(1);
				// Guardamos los resultados y finalizamos
				Paso paso = new Paso(desc);
				paso.setNumber(Integer.toString(erf.getListPasos().size() + 1));
				if (np.getmCurrentPhotoPath() != null) {

					paso.setPath(np.getmCurrentPhotoPath());
				}

				erf.getListPasos().add(paso);
				erf.getAdapterPasos().notifyDataSetChanged();

			} else {
				Toast.makeText(getBaseContext(),
						"¡Falta la descripción del paso!", Toast.LENGTH_LONG)
						.show();
			}
			break;
		case R.id.btnEditarIngrediente:
			// Editar ingrediente de la lista de
			// ingredientes de la receta
			// Obtengo el RelativeLayout de la fila
			EditRecetasFragment erf = (EditRecetasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.detailsRecetas);
			RelativeLayout vwParentRow = (RelativeLayout) v.getParent();
			// Obtengo la posicion en la lista
			int pos = ((ListView) vwParentRow.getParent())
					.getPositionForView(vwParentRow);
			// Nombre y cantidad
			String nombreIng = ((TextView) vwParentRow
					.findViewById(R.id.txtRecetasIngrediente)).getText()
					.toString();
			String cantidad = ((TextView) vwParentRow
					.findViewById(R.id.txtRecetasCantidad)).getText()
					.toString();

			erf.addIngrediente.setVisibility(View.INVISIBLE);
			erf.saveIngrediente.setVisibility(View.VISIBLE);

			// Añado la posicion a la variable miembro y me sirve para borrar o
			// actualizar
			erf.positionSelected = pos;

			erf.txtIngrediente.setText(nombreIng);
			erf.txtQty.setText(cantidad);
			break;

		}

	}

	/**
	 * When an image is clicked, load that image as a puzzle.
	 */
	/*
	 * @Override public void onItemClick(AdapterView<?> parent, View v, int
	 * position, long id) { int columnIndex = 0; String[] projection = {
	 * MediaColumns.DATA }; Cursor cursor = managedQuery(
	 * MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null,
	 * null, null); if (cursor != null) { columnIndex =
	 * cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	 * cursor.moveToPosition(position); String imagePath =
	 * cursor.getString(columnIndex);
	 * 
	 * getSupportFragmentManager().popBackStack();
	 * getSupportFragmentManager().executePendingTransactions();
	 * NuevoPasoFragment npf = (NuevoPasoFragment) getSupportFragmentManager()
	 * .findFragmentById(R.id.detailsRecetas); npf.setPic(imagePath);
	 * 
	 * npf.getImagenButton().setText("Cambiar Imagen");
	 * 
	 * // Toast.makeText(getBaseContext(), imagePath, //
	 * Toast.LENGTH_LONG).show(); /* Intent resultIntent = new Intent();
	 * resultIntent.putExtra("path", imagePath); setResult(IMAGES_ACTIVITY_CODE,
	 * resultIntent); finish();
	 */

	/*
	 * } }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		NuevoPasoFragment npf = (NuevoPasoFragment) getSupportFragmentManager()
				.findFragmentById(R.id.detailsRecetas);
		Log.i("test", "Request code: " + Integer.toString(requestCode));
		Log.i("test", "Result code: " + Integer.toString(resultCode));

		if (requestCode == RECETA_ACTIVITY_CODE) {
			RecetasFragment lista = (RecetasFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentRecetas);
			switch (resultCode) {
			case Mode.DELETE:
				// Hemos borrado una receta y la eliminamos de la lista

				lista.removeItemFromList(lista.elementos.get(data.getExtras()
						.getInt("position")));
				lista.updateList();
				break;
			case Mode.SAVE:
				if (data != null && data.getExtras() != null) {

					recetaAnterior = recetaActual;
					recetaActual = data.getExtras().getString("nombre");
					lista.renameItemFromList(recetaAnterior, recetaActual);
					lista.updateList();
				}
			}

		}

		if (requestCode != SELECT_PHOTO && resultCode == RESULT_OK) {
			try {
				// La camara devuelve -1 si foto OK y 0 si pulsamos back
				// mCurrentPhotoPath ya tiene el path de la foto
				Uri selectedImage = Uri.fromFile(new File(npf
						.getmCurrentPhotoPath()));

				Bitmap bm = decodeUri(selectedImage);
				npf.setPic(bm, selectedImage.getPath());

				npf.getImagenButton().setText("Cambiar Imagen");
			} catch (FileNotFoundException ex) {

			}
			/*
			 * npf.setPic(npf.getmCurrentPhotoPath());
			 * 
			 * npf.getImagenButton().setText("Cambiar Imagen");
			 */
		}

		else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
			try {
				Uri selectedImage = data.getData();
				Bitmap bm = decodeUri(selectedImage);
				npf.setPic(bm, selectedImage.getPath());

				npf.setmCurrentPhotoPath(getRealPathFromURI(selectedImage));
				Log.i("test", npf.getmCurrentPhotoPath());
				npf.getImagenButton().setText("Cambiar Imagen");
				/*
				 * InputStream imageStream =
				 * getContentResolver().openInputStream( selectedImage); Bitmap
				 * yourSelectedImage = BitmapFactory .decodeStream(imageStream);
				 */
			} catch (FileNotFoundException ex) {

			}
		}

		/*
		 * if (requestCode == NUEVO_PASO_ACTIVITY_CODE && resultCode ==
		 * IMAGES_ACTIVITY_CODE) {
		 * 
		 * Bundle extras = data.getExtras(); mCurrentPhotoPath =
		 * extras.getString("path"); setPic(mCurrentPhotoPath);
		 * imagenButton.setText("Cambiar Imagen"); }
		 */

	}

	// And to convert the image URI to the direct file system path of the image
	// file
	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		Display display = ((WindowManager) getBaseContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to

		final int REQUIRED_SIZE = (mDualPane) ? ((int) (display.getWidth() * 0.15))
				: ((int) (display.getWidth() * 0.25));

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);

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
								RecetasFragment lista = (RecetasFragment) getSupportFragmentManager()
										.findFragmentById(R.id.fragmentRecetas);
								lista.removeItemFromList(recetaActual);
								lista.updateList();

								if (lista.elementos.size() > 0) {
									Receta receta = null;
									try {
										receta = file.leerReceta(recetaActual);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									if (receta != null) {
										int mode = (receta instanceof RecetaSimple) ? Mode.RECETA_SIMPLE_MODE
												: Mode.RECETA_ADVANCED_MODE;
										fragmentToUpdate = new DetailsRecetasFragment(
												RecetasActivity.this,
												Mode.SHOW, mode, receta);
										mAdapterListas.notifyDataSetChanged();
										mPager.setCurrentItem(0);

										Toast.makeText(
												getBaseContext(),
												"La receta se ha borrado correctamente",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(
												getBaseContext(),
												"La lista no ha podido ser borrada",
												Toast.LENGTH_SHORT).show();
									}

								}

								// finish();
							} else {
								Toast.makeText(getBaseContext(),
										"La lista no ha podido ser borrada",
										Toast.LENGTH_SHORT).show();
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
						RecetasFragment rf = (RecetasFragment) RecetasActivity.this
								.getSupportFragmentManager().findFragmentById(
										R.id.fragmentRecetas);
						rf.getListView().setItemChecked(pos, false);
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

							if (recetaActual == null) {
								recetaActual = listas.get(pos);
							}

							mode = Mode.SHOW;

						}
					});
			break;

		}
		AlertDialog alert = builder.create();

		return alert;
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
					df = new DetailsRecetasFragment(RecetasActivity.this,
							Mode.SHOW, modeReceta, receta);

				} else {
					df = new DetailsRecetasFragment(RecetasActivity.this,
							Mode.SHOW, Mode.RECETA_SIMPLE_MODE);

				}
			} else {
				df = new DetailsRecetasFragment(RecetasActivity.this,
						Mode.SHOW, Mode.RECETA_SIMPLE_MODE);
			}

			return df;
		}
	}

}
