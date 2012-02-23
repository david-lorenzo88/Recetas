package com.yoanaydavid.recetas;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yoanaydavid.recetas.ListasFragment.MyGestureDetector;
import com.yoanaydavid.recetas.java.FileXML;

public class RecetasFragment extends ListFragment {
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	boolean mDualPane;
	int mCurCheckPosition = 0;
	FileXML file = new FileXML();
	List<String> elementos;
	ArrayAdapter<String> adapter = null;
	ListasListener listener = null;
	static private final String STATE_CHECKED = "com.yoanaydavid.recetas.STATE_CHECKED";

	public ArrayAdapter<String> getAdapter() {
		return adapter;
	}

	public void setItemChecked(int pos) {
		getListView().setItemChecked(pos, true);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

		final GestureDetector gestureDetector = new GestureDetector(
				new MyGestureDetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		getListView().setOnTouchListener(gestureListener);
		try {

			elementos = file.leerElementos("recetas.xml", "receta");
			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_single_choice, elementos);
			setListAdapter(adapter);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// getListView().setSelector(android.R.color.darker_gray);

			/*
			 * if (state != null) { int position = state.getInt(STATE_CHECKED,
			 * -1);
			 * 
			 * if (position > -1) { getListView().setItemChecked(position,
			 * true); } }
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * @Override public void onSaveInstanceState(Bundle state) {
	 * super.onSaveInstanceState(state);
	 * 
	 * state.putInt(STATE_CHECKED, getListView().getCheckedItemPosition()); }
	 * public void enablePersistentSelection() {
	 * getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); }
	 */

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		gestionarOnItemClick(l, position);

	}

	private void gestionarOnItemClick(ListView l, int position) {
		if (!(((RecetasActivity) getActivity()).ismDualPane())
				|| position != mCurCheckPosition
				|| ((((RecetasActivity) getActivity()).ismDualPane() && position == 0))) {
			// Si no estamos en dual panel o si position != mCurCheckPosition
			// mCurCheckPosition = position;

			// l.setItemChecked(position, true);
			if (listener != null) {
				listener.onListaSelected(position, elementos);
			}
		}
	}

	public void unselectItems() {
		getListView().setItemChecked(mCurCheckPosition, false);
		mCurCheckPosition = 0;
	}

	public void setListasListener(ListasListener listener) {
		this.listener = listener;
	}

	public void removeItemFromList(String item) {
		elementos.remove(item);
	}

	public void renameItemFromList(String oldName, String newName) {
		elementos.remove(oldName);
		elementos.add(newName);

	}

	public void addItemToList(String item) {
		elementos.add(item);
	}

	public void updateList() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	public AlertDialog crearDialogo(String mensaje, int pos) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final int position = pos;
		builder.setMessage(mensaje).setCancelable(false)
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Borrar lista
				String nombre = elementos.get(position);
				if (file.borrarListaCompra(nombre, "recetas.xml", "receta")) {
					elementos.remove(position);
					adapter.notifyDataSetChanged();
					Toast.makeText(getActivity(), "Receta borrada",
							Toast.LENGTH_SHORT).show();

					if (((RecetasActivity) getActivity()).ismDualPane())
						((RecetasActivity) getActivity()).mPager
								.setCurrentItem(0);

					/*
					 * DetailsRecetasFragment details = (DetailsRecetasFragment)
					 * ((RecetasActivity) getActivity())
					 * .getSupportFragmentManager().findFragmentById(
					 * R.id.detailsRecetas); details.setDefaultValues();
					 */
				}

			}
		});

		AlertDialog alert = builder.create();

		return alert;
	}

	private void onLTRFling(int pos) {
		// Abrir lista
		if (pos != ListView.INVALID_POSITION) {
			gestionarOnItemClick(getListView(), pos);
		}

	}

	private void onRTLFling(int pos) {
		// Borrar
		if (pos != ListView.INVALID_POSITION) {
			crearDialogo(
					"¿Seguro que desea borrar la receta \""
							+ elementos.get(pos) + "\"?", pos).show();
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
				return false;
			if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onRTLFling(getListView().pointToPosition((int) e1.getX(),
						(int) e1.getY()));
			} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
				onLTRFling(getListView().pointToPosition((int) e1.getX(),
						(int) e1.getY()));
			}
			return false;
		}

	}
}
