package com.yoanaydavid.recetas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Ingrediente;
import com.yoanaydavid.recetas.java.Mode;
import com.yoanaydavid.recetas.java.Paso;
import com.yoanaydavid.recetas.java.Receta;
import com.yoanaydavid.recetas.java.RecetaAdvanced;
import com.yoanaydavid.recetas.java.RecetaSimple;

/**
 * This is the secondary fragment, displaying the details of a particular item.
 */
public class DetailsRecetasFragment extends Fragment {
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */

	Button deleteAndSaveButton;
	Button editButton;
	EditText textField;
	WebView webView;
	FileXML file;
	Receta receta = null;
	Context context;
	// Indica el modo en el que se encuentra la Activity (Show / Edit)
	int mode;
	int modeReceta;
	OnClickListener listener = null;



	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getModeReceta() {
		return modeReceta;
	}
	public void setModeReceta(int modeReceta) {
		this.modeReceta = modeReceta;
	}
	public Receta getReceta() {
		return receta;
	}
	public void setReceta(Receta receta) {
		this.receta = receta;
	}
	public DetailsRecetasFragment(OnClickListener listener, int mode, int modeReceta, Receta receta) {
		this.modeReceta = modeReceta;
		this.receta = receta;
		this.listener = listener;
		this.mode = mode;
		file = new FileXML();
	}
	public DetailsRecetasFragment(OnClickListener listener, int mode, int modeReceta) {
		this.modeReceta = modeReceta;
		this.listener = listener;
		this.mode = mode;
		file = new FileXML();
	}
	

	public String getNombreLista() {
		if (textField != null) {
			return textField.getText().toString().trim();
		}
		return null;
	}

	public void makeAll() {
		try {
			if (mode == Mode.SHOW)
				showButtons();
			else if (mode == Mode.NEW) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				textField.setText(file.encontrarNombreSiguiente(sdf
						.format(new Date())));

			}
			webView.loadDataWithBaseURL(null, generateWebContent(), "text/html", "utf-8", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void changeToShowMode() {
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// if(savedInstanceState == null){
		if(receta != null)
			makeAll();
		// }
		super.onActivityCreated(savedInstanceState);

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
		webView.setBackgroundColor(0x00000000); // Background transparent
		if (mode == Mode.SHOW) {
			// modo mostrar lista
			changeToShowMode();
			deleteAndSaveButton.setVisibility(View.GONE);
			editButton.setVisibility(View.GONE);
			if(receta == null)
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

	private String generateWebContent() throws IOException {
		StringBuffer webContent = new StringBuffer();
		webContent
				.append("<html><body style=\"background-color:black; color:white;\">");
		if (modeReceta == Mode.RECETA_SIMPLE_MODE) {
			this.modeReceta = Mode.RECETA_SIMPLE_MODE;
			webContent.append(generateSimpleMode());

		} else if (modeReceta == Mode.RECETA_ADVANCED_MODE) {
			this.modeReceta = Mode.RECETA_ADVANCED_MODE;
			webContent.append(generateAdvancedMode());

		} else if (modeReceta == Mode.RECETA_UNKNOWN_MODE) {
			// Venimos desde la lista de recetas, por tanto no conocemos el
			// modo.
			int mode = file.getRecetaMode(receta.getNombre());
			this.modeReceta = mode;
			if (mode == Mode.RECETA_SIMPLE_MODE)
				webContent.append(generateSimpleMode());
			else if (mode == Mode.RECETA_ADVANCED_MODE)
				webContent.append(generateAdvancedMode());
		}
		webContent.append("</body></html>");

		return webContent.toString();
	}

	private String generateAdvancedMode() throws IOException {
		RecetaAdvanced recetaAdv = (RecetaAdvanced) receta;
		StringBuffer webContent = new StringBuffer();
		if (recetaAdv != null) {
			webContent.append("<h3>Ingredientes</h3>");
			webContent.append("<ul>");
			List<Ingrediente> ings = recetaAdv.getIngredientes();
			for (int i = 0; i < ings.size(); i++) {

				if (ings.get(i).getCantidad().equals("")) {
					webContent.append("<li>" + ings.get(i).getNombre()
							+ "</li>");
				} else {
					webContent.append("<li>" + ings.get(i).getNombre() + " - "
							+ ings.get(i).getCantidad() + "</li>");
				}
			}
			webContent.append("</ul>");
			webContent.append("<h3>Elaboración</h3>");
			for (int i = 0; i < recetaAdv.getPasos().size(); i++) {
				Paso p = recetaAdv.getPasos().get(i);
				if (p.getPath() != null) {
					// El paso tiene imagen asociada
					webContent
							.append("<div style='border-bottom:3px solid white; margin-bottom:10px'>");
					if (isThumbnail(p.getPath()))
						webContent
								.append("<img src=\'file://"
										+ p.getPath()
										+ "' alt='Imagen receta' style='float:left;' />");
					else {
						Bitmap bm = BitmapFactory.decodeFile(p.getPath());
						webContent.append("<img src=\'file://" + p.getPath()
								+ "' alt='Imagen receta' width='"
								+ getScaledWidth(bm) + "' height='"
								+ getScaledHeight(bm)
								+ "' style='float:left;' />");
					}

					webContent.append("<p>" + p.getNumber() + ". "
							+ p.getDescripcion() + "</p>");
					webContent.append("<br style='clear:both' /></div>");
				} else {
					// El paso no tiene imagen asociada
					webContent
							.append("<div style='border-bottom:3px solid white; margin-bottom:10px'><p>");
					webContent.append(p.getNumber() + ". ");
					webContent.append(p.getDescripcion());
					webContent.append("</p></div>");
				}
			}

		} else {
			webContent.append("<h4>No se pudo recuperar la receta</h4>");
		}
		return webContent.toString();
	}

	private String generateSimpleMode() throws IOException {
		RecetaSimple recetaS = (RecetaSimple) receta;
		StringBuffer webContent = new StringBuffer();
		if (recetaS != null) {
			webContent.append("<h3>Ingredientes</h3>");
			webContent.append("<ul>");
			List<Ingrediente> ings = recetaS.getIngredientes();
			for (int i = 0; i < ings.size(); i++) {

				if (ings.get(i).getCantidad().equals("")) {
					webContent.append("<li>" + ings.get(i).getNombre()
							+ "</li>");
				} else {
					webContent.append("<li>" + ings.get(i).getNombre() + " - "
							+ ings.get(i).getCantidad() + "</li>");
				}
			}
			webContent.append("</ul>");
			webContent.append("<h3>Elaboración</h3>");
			webContent.append("<p>" + recetaS.getElab() + "</p>");
		} else {
			webContent.append("<h4>No se pudo recuperar la receta</h4>");
		}
		return webContent.toString();
	}

	private boolean isThumbnail(String path) {
		String[] partes = path.split("/");
		return partes[partes.length - 2].equals(".thumbnails");
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

}
