package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;
import com.yoanaydavid.recetas.java.Paso;
import com.yoanaydavid.recetas.java.Receta;
import com.yoanaydavid.recetas.java.RecetaAdvanced;
import com.yoanaydavid.recetas.java.RecetaSimple;
import com.yoanaydavid.recetas.java.RecetasMode;

public class EditRecetasFragment extends Fragment {
	final static int EDIT_ID = 0;
	final static int DELETE_ID = 1;
	final static int RECETAS_ACTIVITY_CODE = 4;
	final static int NUEVO_PASO_ACTIVITY_CODE = 5;
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	private FileXML file;
	TextView txt1;
	TextView txt2;
	WebView webViewDetalles;
	EditText txtRecetaElaboracionSimple;
	EditText txtNombreReceta;
	ListView listaIngredientes;
	ListView listaRecetaMode;
	ListView listaPasos;
	Button addIngrediente;
	Button saveIngrediente;
	Button saveList;
	Button crearPasoButton;
	AutoCompleteTextView txtIngrediente;
	EditText txtQty;
	TabHost tabHost;
	int positionSelected = -1;
	boolean mSaveState = false;
	Receta receta = null;

	private String nombreReceta;
	int mode = -1;
	int modeReceta = -1;
	OnClickListener listener;

	// Lista para alamcenar los pasos en el modo avanzado
	ArrayList<Paso> listPasos = new ArrayList<Paso>();

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<Ingrediente> listIngs = new ArrayList<Ingrediente>();

	// DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	IngredientesAdapter adapter;
	PasosAdapter adapterPasos;

	public boolean ismSaveState() {
		return mSaveState;
	}

	public void setmSaveState(boolean mSaveState) {
		this.mSaveState = mSaveState;
	}

	public EditText getTxtRecetaElaboracionSimple() {
		return txtRecetaElaboracionSimple;
	}

	public void setTxtRecetaElaboracionSimple(
			EditText txtRecetaElaboracionSimple) {
		this.txtRecetaElaboracionSimple = txtRecetaElaboracionSimple;
	}

	public EditText getTxtNombreReceta() {
		return txtNombreReceta;
	}

	public void setTxtNombreReceta(EditText txtNombreReceta) {
		this.txtNombreReceta = txtNombreReceta;
	}

	public int getModeReceta() {
		return modeReceta;
	}

	public void setModeReceta(int modeReceta) {
		this.modeReceta = modeReceta;
	}

	public ArrayList<Paso> getListPasos() {
		return listPasos;
	}

	public void setListPasos(ArrayList<Paso> listPasos) {
		this.listPasos = listPasos;
	}

	public ArrayList<Ingrediente> getListIngs() {
		return listIngs;
	}

	public void setListIngs(ArrayList<Ingrediente> listIngs) {
		this.listIngs = listIngs;
	}

	public IngredientesAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(IngredientesAdapter adapter) {
		this.adapter = adapter;
	}

	public PasosAdapter getAdapterPasos() {
		return adapterPasos;
	}

	public void setAdapterPasos(PasosAdapter adapterPasos) {
		this.adapterPasos = adapterPasos;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	OnClickListener btnAddOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String ing = txtIngrediente.getText().toString().trim();
			String qty = txtQty.getText().toString().trim();
			if (!ing.equals("")) {
				addIngredient(ing, qty);
				adapter.notifyDataSetChanged();
				clearIngsTextFields();
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtIngrediente.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(txtQty.getWindowToken(), 0);
			} else {
				Toast.makeText(getActivity().getBaseContext(),
						"¡Debe rellenar el ingrediente!", Toast.LENGTH_LONG)
						.show();
			}

		}

	};

	public void setTabColor() {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget()
					.getChildAt(i)
					.setBackgroundDrawable(
							getResources().getDrawable(R.drawable.tab)); // unselected

		}
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
				.setBackgroundColor(Color.parseColor("#232f40")); // selected

	}

	OnTabChangeListener tabHostOnTabChange = new OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabId) {
			// setTabColor();
			if (tabId.equals("guardar")) {
				// actualizamos el TextView
				if (!(listIngs.size() > 0)) {
					tabHost.setCurrentTabByTag("ingredientes");
					Toast.makeText(getActivity().getBaseContext(),
							"¡Debe introducir los ingredientes!",
							Toast.LENGTH_SHORT).show();

				} else if (modeReceta == -1
						|| modeReceta == Mode.RECETA_UNKNOWN_MODE) {
					tabHost.setCurrentTabByTag("elaboracion");
					Toast.makeText(
							getActivity().getBaseContext(),
							"¡Debe seleccionar un modo e introducir una elaboración!",
							Toast.LENGTH_SHORT).show();
				} else if (modeReceta == Mode.RECETA_SIMPLE_MODE) {
					String elab = txtRecetaElaboracionSimple.getText()
							.toString().trim();
					if (elab.equals("")) {
						tabHost.setCurrentTabByTag("elaboracion");
						Toast.makeText(getActivity().getBaseContext(),
								"¡Debe introducir una elaboración!",
								Toast.LENGTH_SHORT).show();
					} else {

						StringBuffer resumen = new StringBuffer();
						resumen.append("<html><body style=\"background-color:black; color:white;\"><h4>Ingredientes</h4>");
						resumen.append("<ul>");
						for (int i = 0; i < listIngs.size(); i++) {
							Ingrediente current = listIngs.get(i);
							if (current.getCantidad().equals(""))
								resumen.append("<li>" + current.getNombre()
										+ "</li>");
							else
								resumen.append("<li>" + current.getNombre()
										+ " - " + current.getCantidad()
										+ "</li>");
						}
						resumen.append("</ul>");
						resumen.append("<h4>Elaboración</h4>");
						resumen.append("<p>" + elab + "</p>");
						resumen.append("</body></html>");

						webViewDetalles.loadDataWithBaseURL(null,
								resumen.toString(), "text/html", "utf-8", null);
					}
				} else if (modeReceta == Mode.RECETA_ADVANCED_MODE) {
					if (listPasos.size() <= 0) {
						// No hay pasos
						tabHost.setCurrentTabByTag("elaboracion");
						Toast.makeText(getActivity().getBaseContext(),
								"¡Debe introducir los pasos de la receta!",
								Toast.LENGTH_SHORT).show();
					} else {
						// Hay algun paso, guardamos
						String content = generateWebContentForAdvancedMode();
						webViewDetalles.loadDataWithBaseURL(null, content,
								"text/html", "utf-8", null);
					}
				}
			}
		}
	};
	OnItemClickListener listaIngsOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			positionSelected = position;
			getActivity().openOptionsMenu();

		}
	};

	OnItemClickListener listaRecetasModeOnItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			changeMode(position);

		}
	};

	public void changeMode(int mode) {
		switch (mode) {
		case 0: // Simple mode
			listaRecetaMode.setVisibility(View.GONE);
			txtRecetaElaboracionSimple.setVisibility(View.VISIBLE);
			modeReceta = Mode.RECETA_SIMPLE_MODE;
			break;
		case 1: // Advanced mode
			listaRecetaMode.setVisibility(View.GONE);
			crearPasoButton.setVisibility(View.VISIBLE);
			listaPasos.setVisibility(View.VISIBLE);
			modeReceta = Mode.RECETA_ADVANCED_MODE;

			adapterPasos = new PasosAdapter(getActivity(), listPasos);
			listaPasos.setAdapter(adapterPasos);

			break;
		}
	}

	public void hideModeSelectionList() {
		listaRecetaMode.setVisibility(View.GONE);
		crearPasoButton.setVisibility(View.VISIBLE);
		listaPasos.setVisibility(View.VISIBLE);
	}

	OnClickListener btnSaveOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			String ing = txtIngrediente.getText().toString().trim();
			String qty = txtQty.getText().toString().trim();
			qty = (qty.equals("")) ? "-" : qty;
			if (!ing.equals("")) {
				listIngs.set(positionSelected, new Ingrediente(ing, qty));
				adapter.notifyDataSetChanged();
				Toast.makeText(getActivity().getBaseContext(),
						"¡Ingrediente modificado!", Toast.LENGTH_LONG).show();
				positionSelected = -1;
				clearIngsTextFields();
				addIngrediente.setVisibility(View.VISIBLE);
				saveIngrediente.setVisibility(View.INVISIBLE);
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				}
			} else {
				Toast.makeText(getActivity().getBaseContext(),
						"¡Debe rellenar el ingrediente!", Toast.LENGTH_LONG)
						.show();
			}

		}

	};

	// Constructor para nueva receta
	public EditRecetasFragment(int mode, int modeReceta,
			OnClickListener listener) {
		this.mode = mode;
		this.modeReceta = modeReceta;
		this.listener = listener;

		file = new FileXML();

	}

	// Constructor para editar receta
	public EditRecetasFragment(int mode, int modeReceta,
			OnClickListener listener, Receta receta) {
		this.mode = mode;
		this.modeReceta = modeReceta;
		this.receta = receta;
		this.listener = listener;

		file = new FileXML();

		modeReceta = file.getRecetaMode(receta.getNombre());

	}

	@Override
	public void onDestroy() {
		try {
			if (mode == Mode.EDIT
					&& ((RecetasActivity) getActivity()).ismDualPane()
					&& !((RecetasActivity) getActivity()).isFinishing()) {
				crearDialogo(
						"¿Desea guardar los cambios en la receta \""
								+ nombreReceta + "\"?", Mode.DIALOG_SAVE, -1)
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

		// Gesture para la lista de ingredientes
		final GestureDetector gestureDetectorIng = new GestureDetector(
				new MyGestureDetector(Mode.GESTURE_INGREDIENTE));
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetectorIng.onTouchEvent(event);
			}
		};
		listaIngredientes.setOnTouchListener(gestureListener);

		// GEsture para la lista de pasos
		final GestureDetector gestureDetectorPaso = new GestureDetector(
				new MyGestureDetector(Mode.GESTURE_PASO));
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetectorPaso.onTouchEvent(event);
			}
		};
		listaPasos.setOnTouchListener(gestureListener);
		if (mode == Mode.EDIT) {
			nombreReceta = receta.getNombre();
			if (modeReceta == Mode.RECETA_SIMPLE_MODE) {
				// Modo simple
				// Recuperamos la receta y Rellenamos los campos
				RecetaSimple receta = (RecetaSimple) this.receta;
				if (receta != null) {
					List<Ingrediente> ings = receta.getIngredientes();
					for (int i = 0; i < ings.size(); i++) {
						addIngredient(ings.get(i).getNombre(), ings.get(i)
								.getCantidad());
					}
					adapter.notifyDataSetChanged();
					txtRecetaElaboracionSimple.setText(receta.getElab());
					txtNombreReceta.setText(receta.getNombre());
					// Cambiamos la elaboracion a modo simple
					listaRecetaMode.setVisibility(View.GONE);
					txtRecetaElaboracionSimple.setVisibility(View.VISIBLE);
					// mode = Mode.RECETA_SIMPLE_MODE;
				} else {
					Toast.makeText(getActivity().getBaseContext(),
							"¡Hubo un fallo al recuperar la receta!",
							Toast.LENGTH_LONG).show();
					// Volver atras !! finish();
				}
			} else if (modeReceta == Mode.RECETA_ADVANCED_MODE) {
				// Modo avanzado
				RecetaAdvanced receta = (RecetaAdvanced) this.receta;
				if (receta != null) {
					List<Ingrediente> ings = receta.getIngredientes();
					for (int i = 0; i < ings.size(); i++) {
						addIngredient(ings.get(i).getNombre(), ings.get(i)
								.getCantidad());
					}
					adapter.notifyDataSetChanged();

					listPasos = new ArrayList<Paso>();
					adapterPasos = new PasosAdapter(getActivity(), listPasos);
					listaPasos.setAdapter(adapterPasos);
					List<Paso> pasos = receta.getPasos();
					for (int i = 0; i < pasos.size(); i++) {
						adapterPasos.addPaso(pasos.get(i));
					}
					adapterPasos.notifyDataSetChanged();

					txtNombreReceta.setText(receta.getNombre());

					listaRecetaMode.setVisibility(View.GONE);
					crearPasoButton.setVisibility(View.VISIBLE);
					listaPasos.setVisibility(View.VISIBLE);

					// mode = Mode.RECETA_ADVANCED_MODE;
				} else {
					Toast.makeText(getActivity().getBaseContext(),
							"¡Hubo un fallo al recuperar la receta!",
							Toast.LENGTH_LONG).show();
					// volver atras !! finish();
				}

			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		// Obtenemos el layout y a partir de él personalizamos la Views que
		// contiene

		View content = inflater.inflate(R.layout.nuevareceta, container, false);
		ArrayAdapter<Ingrediente> adapterIng;
		txtRecetaElaboracionSimple = (EditText) content
				.findViewById(R.id.txtRecetaElaboracionSimple);
		txtIngrediente = (AutoCompleteTextView) content
				.findViewById(R.id.txtIngredient);
		txtNombreReceta = (EditText) content.findViewById(R.id.txtNombreReceta);
		webViewDetalles = (WebView) content.findViewById(R.id.webViewDetalles);
		webViewDetalles.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webViewDetalles.setBackgroundColor(0x00000000);
		listaIngredientes = (ListView) content
				.findViewById(R.id.listIngredientes);
		listaRecetaMode = (ListView) content.findViewById(R.id.listRecetaModes);
		addIngrediente = (Button) content.findViewById(R.id.btnAdd);
		addIngrediente.setOnClickListener(btnAddOnClick);
		saveIngrediente = (Button) content.findViewById(R.id.btnSave);
		saveIngrediente.setOnClickListener(btnSaveOnClick);
		saveList = (Button) content.findViewById(R.id.btnSaveList);
		saveList.setOnClickListener(listener);
		crearPasoButton = (Button) content.findViewById(R.id.btnCrearPaso);
		crearPasoButton.setOnClickListener(listener);
		txtQty = (EditText) content.findViewById(R.id.txtQuantity);
		adapter = new IngredientesAdapter(getActivity(), listIngs, listener);
		listaPasos = (ListView) content.findViewById(R.id.listPasos);
		listaIngredientes.setAdapter(adapter);
		listaIngredientes.setOnItemClickListener(listaIngsOnItemClick);
		RecetasModeAdapter adapterMode = new RecetasModeAdapter(getActivity(),
				crearModos());
		listaRecetaMode.setAdapter(adapterMode);
		listaRecetaMode.setOnItemClickListener(listaRecetasModeOnItemClick);
		try {
			adapterIng = new ArrayAdapter<Ingrediente>(getActivity(),
					android.R.layout.simple_dropdown_item_1line,
					file.leerIngredientes());
			txtIngrediente.setAdapter(adapterIng);
			txtIngrediente.setThreshold(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tabHost = (TabHost) content.findViewById(android.R.id.tabhost); // The
																		// activity
																		// TabHost
		tabHost.setup(); // Must call this if load tabhost from "findViewById",
							// if loaded from getTabHost() is not necessary
		TabHost.TabSpec spec; // Reusable TabSpec for each tab

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("ingredientes")
				.setIndicator(
						"1. Ingredientes",
						getResources().getDrawable(
								R.drawable.ic_tab_ingredientes))
				.setContent(R.id.ingredientesLayout);

		tabHost.addTab(spec);

		// Do the same for the other tabs

		spec = tabHost
				.newTabSpec("elaboracion")
				.setIndicator(
						"2. Elaboración",
						getResources().getDrawable(
								R.drawable.ic_tab_elaboracion))
				.setContent(R.id.recetasModeLayout);

		tabHost.addTab(spec);

		spec = tabHost
				.newTabSpec("guardar")
				.setIndicator("3. Guardar",
						getResources().getDrawable(R.drawable.ic_tab_save))
				.setContent(R.id.guardarLayout);

		tabHost.addTab(spec);
		tabHost.setOnTabChangedListener(tabHostOnTabChange);
		tabHost.setCurrentTab(0);
		return content;

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
							// Guardar los cambios en la receta

							guardarCambios();

						}
					});
			break;
		case (Mode.DIALOG_DELETE + Mode.GESTURE_INGREDIENTE):
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Borrar los cambios en la lista

							listIngs.remove(position);

							adapter.notifyDataSetChanged();

						}
					});
			break;
		case (Mode.DIALOG_DELETE + Mode.GESTURE_PASO):
			builder.setPositiveButton("Sí",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Borrar los cambios en la lista

							listPasos.remove(position);

							adapterPasos.notifyDataSetChanged();

						}
					});
			break;
		}

		AlertDialog alert = builder.create();

		return alert;
	}

	public boolean guardarCambios() {

		if (file.borrarListaCompra(nombreReceta, "recetas.xml", "receta")) {
			if (modeReceta == Mode.RECETA_SIMPLE_MODE)
				return file.guardarRecetaModoSimple(getListIngs(),
						nombreReceta, txtRecetaElaboracionSimple.getText()
								.toString().trim());
			else if (modeReceta == Mode.RECETA_ADVANCED_MODE)
				return file.guardarRecetaModoAvanzado(getListIngs(),
						getListPasos(), nombreReceta);
		}
		return false;

	}

	private List<RecetasMode> crearModos() {
		List<RecetasMode> list = new ArrayList<RecetasMode>();
		String title = new String("Modo simple");
		String text = new String(
				"Con el modo simple podrás crear la elaboración de tu receta escribiendo la explicación en un simple campo de texto");
		RecetasMode mode1 = new RecetasMode(title, text);
		title = new String("Modo avanzado");
		text = new String(
				"Con el modo avanzado podrás crear la elaboración de tu receta escribiendo paso a paso. "
						+ "En cada paso podrás incluir texto explicativo e imágenes capturadas desde la cámara o importadas desde la memoria");
		RecetasMode mode2 = new RecetasMode(title, text);
		list.add(mode1);
		list.add(mode2);
		return list;
	}

	private void addIngredient(String name, String qty) {
		if (!qty.equals(""))
			listIngs.add(new Ingrediente(name, qty));
		else
			listIngs.add(new Ingrediente(name));

	}

	private void clearIngsTextFields() {
		txtIngrediente.setText("");
		txtQty.setText("");
	}

	public void clearAllTextFields() {
		clearIngsTextFields();
		txtRecetaElaboracionSimple.setText("");
		txtNombreReceta.setText("");

	}

	private String generateWebContentForAdvancedMode() {

		StringBuffer b = new StringBuffer();
		b.append("<html><body style=\"background-color:black; color:white;\"><h4>Ingredientes</h4>");
		b.append("<ul>");
		for (int i = 0; i < listIngs.size(); i++) {
			Ingrediente current = listIngs.get(i);
			if (current.getCantidad().equals(""))
				b.append("<li>" + current.getNombre() + "</li>");
			else
				b.append("<li>" + current.getNombre() + " - "
						+ current.getCantidad() + "</li>");
		}
		b.append("</ul>");
		b.append("<h4>Elaboración</h4>");

		for (int i = 0; i < listPasos.size(); i++) {
			Paso p = listPasos.get(i);
			if (p.getPath() != null) {
				// El paso tiene imagen asociada
				b.append("<div style='border-bottom:3px solid white; margin-bottom:10px'>");
				if (isThumbnail(p.getPath()))
					b.append("<img src=\'file://" + p.getPath()
							+ "' alt='Imagen receta' style='float:left;' />");
				else {
					Bitmap bm = BitmapFactory.decodeFile(p.getPath());
					b.append("<img src=\'file://" + p.getPath()
							+ "' alt='Imagen receta' width='"
							+ getScaledWidth(bm) + "' height='"
							+ getScaledHeight(bm) + "' style='float:left;' />");
				}

				b.append("<p>" + p.getNumber() + ". " + p.getDescripcion()
						+ "</p>");
				b.append("<br style='clear:both' /></div>");
			} else {
				// El paso no tiene imagen asociada
				b.append("<div style='border-bottom:3px solid white; margin-bottom:10px'><p>");
				b.append(p.getNumber() + ". ");
				b.append(p.getDescripcion());
				b.append("</p></div>");
			}
		}

		b.append("</body></html>");

		return b.toString();
	}

	private String getScaledWidth(Bitmap bm) {
		Display display = ((WindowManager) getActivity().getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// El ancho de la imagen seria el 25% de la pantalla
		return "" + ((int) (display.getWidth() * 0.25));

	}

	private String getScaledHeight(Bitmap bm) {
		Display display = ((WindowManager) getActivity().getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Hacemos el ancho de la imagen el 25% del ancho de la pantalla
		int scaleWidth = (int) (display.getWidth() * 0.25);

		// Obtenemos el ratio de escalado del ancho
		int ratio = bm.getWidth() / scaleWidth;
		// Retornamos el alto escalado
		return "" + (bm.getHeight() / ratio);

	}

	private boolean isThumbnail(String path) {
		String[] partes = path.split("/");
		return partes[partes.length - 2].equals(".thumbnails");
	}

	private boolean isAnyData() {
		// Comprueba si hay algun dato en algun campo de texto o lista
		if (txtIngrediente.getText().toString().trim().equals("")
				&& txtQty.getText().toString().trim().equals("")
				&& listIngs.size() <= 0
				&& txtRecetaElaboracionSimple.getText().toString().trim()
						.equals("")) {

			if (listPasos == null)

				return false;
			else
				// si la lista no es null pero no tiene datos => false;
				return listPasos.size() > 0;

		}

		return true;

	}

	private void onLTRFling(int pos, int mode) {
		// Toast.makeText(getActivity(), ""+listViewIngs.getItemAtPosition(pos),
		// Toast.LENGTH_SHORT).show();
		if (pos != ListView.INVALID_POSITION) {
			onRTLFling(pos, mode);
		}
	}

	private void onRTLFling(int pos, int mode) {
		// Toast.makeText(getActivity(), ""+listViewIngs.getItemAtPosition(pos),
		// Toast.LENGTH_SHORT).show();
		if (pos != ListView.INVALID_POSITION) {
			if (mode == Mode.GESTURE_INGREDIENTE) {
				Ingrediente ing = listIngs.get(pos);
				crearDialogo(
						"¿Seguro que desea borrar \"" + ing.getNombre() + "\"?",
						Mode.DIALOG_DELETE + Mode.GESTURE_INGREDIENTE, pos)
						.show();
			} else if (mode == Mode.GESTURE_PASO) {
				Paso paso = listPasos.get(pos);
				crearDialogo(
						"¿Seguro que desea borrar el paso " + paso.getNumber()
								+ " ?", Mode.DIALOG_DELETE + Mode.GESTURE_PASO,
						pos).show();
			}
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		private int mode;

		public MyGestureDetector(int mode) {
			this.mode = mode;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			ListView lista = (mode == Mode.GESTURE_INGREDIENTE) ? listaIngredientes
					: listaPasos;

			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

				onRTLFling(
						lista.pointToPosition((int) e1.getX(), (int) e1.getY()),
						mode);
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

				onLTRFling(
						lista.pointToPosition((int) e1.getX(), (int) e1.getY()),
						mode);

			}
			return false;
		}

	}

}
