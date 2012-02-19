package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.ArrayList;

import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;
import com.yoanaydavid.recetas.java.Paso;
import com.yoanaydavid.recetas.java.Receta;
import com.yoanaydavid.recetas.java.RecetaAdvanced;
import com.yoanaydavid.recetas.java.RecetaSimple;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NuevaRecetaActivity extends FragmentActivity implements
		OnClickListener {
	final static int NUEVA_RECETA_ACTIVITY_CODE = 1;
	FileXML file;
	String nombreReceta;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme);
		file = new FileXML();
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				if (extras.getInt("mode") == Mode.EDIT) {
					// Get attributes
					try {
						nombreReceta = extras.getString("nombre");

						Receta receta = file.leerReceta(nombreReceta);

						int modeReceta = file.getRecetaMode(nombreReceta);
						// Create Fragment
						EditRecetasFragment erf = new EditRecetasFragment(
								Mode.EDIT, modeReceta, this, receta);

						// Commit
						getSupportFragmentManager().beginTransaction()
								.add(android.R.id.content, erf).commit();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (extras.getInt("mode") == Mode.NEW) {
					EditRecetasFragment erf = new EditRecetasFragment(Mode.NEW,
							Mode.RECETA_UNKNOWN_MODE, this);

					// Commit
					getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, erf).commit();
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EditRecetasFragment ef = (EditRecetasFragment) getSupportFragmentManager()
				.findFragmentById(android.R.id.content);

		Button b = (Button) v;

		switch (b.getId()) {
		case R.id.btnSaveList:

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
					// Tenemos nombre, ingredientes y elaboracion: Guardamos la
					// receta

					if (ef.getMode() == Mode.EDIT) {
						// Estamos en modo editar receta

						if (file.borrarListaCompra(nombreReceta, "recetas.xml",
								"receta"))
							if (file.guardarRecetaModoSimple(ef.getListIngs(),
									nombre, elab)) {
								// Guardamos
								Toast.makeText(getBaseContext(),
										"¡Receta Guardada!", Toast.LENGTH_LONG)
										.show();

								// Volvemos a modo SHOW
								ef.setMode(Mode.SHOW);
								setResult(Mode.SAVE,
										new Intent().putExtra("nombre", nombre));
								finish();

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

							Intent intent = new Intent(this,
									ShowRecetaActivity.class);

							intent.putExtra("mode", Mode.SHOW);
							intent.putExtra("nombre", nombre);
							startActivity(intent);

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
				// No se ha elegido el modo con lo cual no se ha introducido una
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
					// Tenemos nombre, ingredientes y elaboracion: Guardamos la
					// receta

					if (ef.getMode() == Mode.EDIT) {
						// Estamos en modo editar receta

						if (file.borrarListaCompra(nombreReceta, "recetas.xml",
								"receta"))
							if (file.guardarRecetaModoAvanzado(
									ef.getListIngs(), ef.getListPasos(), nombre)) {
								// Guardamos

								Toast.makeText(getBaseContext(),
										"¡Receta Guardada!", Toast.LENGTH_LONG)
										.show();

								// Volvemos a modo SHOW
								ef.setMode(Mode.SHOW);
								setResult(Mode.SAVE,
										new Intent().putExtra("nombre", nombre));
								finish();

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

							// Iniciar detailsrecetasfragment
							Intent intent = new Intent(this,
									ShowRecetaActivity.class);

							intent.putExtra("mode", Mode.SHOW);
							intent.putExtra("nombre", nombre);
							startActivity(intent);

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

		case R.id.btnCrearPaso:

			Intent intent = new Intent(this, NuevoPasoActivity.class);
			startActivityForResult(intent, NUEVA_RECETA_ACTIVITY_CODE);

			break;
		case R.id.btnEditarIngrediente:
			// Editar ingrediente de la lista de ingredientes de la receta
			// Obtengo el RelativeLayout de la fila
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

			ef.addIngrediente.setVisibility(View.INVISIBLE);
			ef.saveIngrediente.setVisibility(View.VISIBLE);

			// Añado la posicion a la variable miembro y me sirve para borrar o
			// actualizar
			ef.positionSelected = pos;

			ef.txtIngrediente.setText(nombreIng);
			ef.txtQty.setText(cantidad);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditRecetasFragment erf = (EditRecetasFragment) getSupportFragmentManager()
				.findFragmentById(android.R.id.content);
		if (requestCode == NUEVA_RECETA_ACTIVITY_CODE
				&& resultCode == RESULT_OK) {
			// Gestionar el nuevo paso
			if (data != null && data.getExtras() != null) {
				Paso paso = new Paso(data.getExtras().getString("descripcion"));
				if (data.getExtras().getString("path") != null) {
					paso.setPath(data.getExtras().getString("path"));

				}
				paso.setNumber((erf.getListPasos().size() + 1) + "");
				erf.getListPasos().add(paso);
				erf.getAdapterPasos().notifyDataSetChanged();

			}
		}
	}

}
