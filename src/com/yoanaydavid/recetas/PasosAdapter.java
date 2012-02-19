package com.yoanaydavid.recetas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.Paso;

public class PasosAdapter extends BaseAdapter {
	private Context context;
	private Bitmap bmDefault;
	private List<Paso> pasos;
	private BackgroundLoadListView task = null;
	private HashMap<String, Bitmap> bmImagenes = new HashMap<String, Bitmap>();
	private ArrayList<String> bmImagenesPos = new ArrayList<String>();

	private final ImageDownloader imageDownloader;

	public PasosAdapter(Context context, List<Paso> pasos) {
		this.context = context;
		this.pasos = pasos;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		bmDefault = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ingredientes, options);
		imageDownloader = new ImageDownloader(context);
	}

	@Override
	public int getCount() {

		return pasos.size();
	}

	public void addPaso(Paso paso) {
		pasos.add(paso);
	}

	@Override
	public Object getItem(int position) {

		return pasos.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Paso entry = pasos.get(position);
		ViewHolder holder;
		if (convertView == null) {

			convertView = View.inflate(context, R.layout.pasos_list, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.imagePaso);
			holder.number = (TextView) convertView
					.findViewById(R.id.txtNumeroPaso);
			holder.description = (TextView) convertView
					.findViewById(R.id.txtDescripcionPaso);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (entry != null) {
			if (entry.getPath() == null) {
				// Poner imagen x defecto
				// Numero
				holder.number.setText(Integer.toString((position + 1)));
				// Desc
				holder.description.setText(entry.getDescripcion());
				// Imagen
				holder.image.setImageBitmap(bmDefault);
				holder.image.setVisibility(View.VISIBLE);
				

			} else {
				// El paso tiene imagen asignada

				// Numero
				holder.number.setText(Integer.toString((position + 1)));
				// Desc
				holder.description.setText(entry.getDescripcion());
				// Imagen

				imageDownloader.download(entry.getPath(), holder.image);

			}

		}
		showHeapParameters();
		return convertView;
	}

	/*private Bitmap getPic(String path) {

		Bitmap bm = null;
		// Compruebo si el bitmap esta escalado, si lo esta no es necesario
		// escalar de nuevo
		if (isThumbnail(path))
			bm = BitmapFactory.decodeFile(path);
		else {
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();

			int ratio = (int) Math
					.ceil((double) (display.getWidth() / bmDefault.getWidth()));

			BitmapFactory.Options options = new BitmapFactory.Options();
			Log.i("ratio", Integer.toString(ratio));
			options.inSampleSize = ratio;
			options.inDither = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bm = BitmapFactory.decodeFile(path, options);

		}

		return bm;
	}*/

	/*
	 * private Bitmap scaleBitmap(String path) { /*Display display =
	 * ((WindowManager) context
	 * .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	 */
	/*
	 * BitmapFactory.Options options = new BitmapFactory.Options();
	 * options.inSampleSize = 8; return BitmapFactory.decodeFile(path, options);
	 * // Hacemos el ancho de la imagen el 25% del ancho de la pantalla /*int
	 * scaleWidth = (int) (display.getWidth() * 0.25);
	 * 
	 * // Obtenemos el ratio de escalado del ancho int ratio = bm.getWidth() /
	 * scaleWidth; // Aplicamos el ratio al alto para mantener las proporciones
	 * int scaleHeight = bm.getHeight() / ratio; return
	 * Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true); }
	 */

	private boolean isThumbnail(String path) {
		String[] partes = path.split("/");
		return partes[partes.length - 2].equals(".thumbnails");
	}

	static class ViewHolder {
		ImageView image;
		TextView number;
		TextView description;
	}

	public class BackgroundLoadListView extends AsyncTask<Void, Void, Boolean> {
		ViewHolder holder;
		Paso paso;
		int pos;

		public BackgroundLoadListView(ViewHolder holder, Paso paso, int pos) {
			this.holder = holder;
			this.paso = paso;
			this.pos = pos;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			/*
			 * setListAdapter(new MyCustomAdapter(AndroidList.this,
			 * R.layout.row, month)); Toast.makeText(AndroidList.this,
			 * "onPostExecute \n: setListAdapter after bitmap preloaded",
			 * Toast.LENGTH_LONG).show();
			 */
			/*
			 * adapterPasos = new PasosAdapter(RecetasActivity.this, listPasos);
			 * listaPasos.setAdapter(adapterPasos);
			 * adapterPasos.notifyDataSetChanged();
			 */
			if (result.booleanValue())
				holder.image.setImageBitmap(bmImagenes.get(Integer
						.toString(pos)));
			else
				holder.image.setImageBitmap(bmDefault);

			// Numero
			holder.number.setText(paso.getNumber());
			// Desc
			holder.description.setText(paso.getDescripcion());

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				if (!bmImagenes.containsKey(Integer.toString(pos))) {

					bmImagenes.put(Integer.toString(pos),
							decodeUri(Uri.fromFile(new File(paso.getPath()))));
					bmImagenesPos.add(Integer.toString(pos));
					return new Boolean(true);

				}
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				return new Boolean(false);

			}
			return new Boolean(false);

		}
	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(selectedImage), null, o);

		// The new size we want to scale to

		final int REQUIRED_SIZE = (int) Math.ceil(display.getWidth() * 0.15);

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;

			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(selectedImage), null, o2);

	}

	public void showHeapParameters() {
		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this
		// size.
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will
		// increase
		// after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();

		Log.i("test", "Heap size: " + Long.toString(heapSize / 1000000)
				+ " MBytes");
		Log.i("test", "Heap max size: " + Long.toString(heapMaxSize / 1000000)
				+ " MBytes");
		Log.i("test",
				"Heap free size: " + Long.toString(heapFreeSize / 1000000)
						+ " MBytes");
	}

	public boolean isHeapHalfFully() {
		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this
		// size.
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will
		// increase
		// after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		Log.v("test", "Heap -> "
				+ ((heapSize / 1000000) - (heapFreeSize / 1000000)) + " > "
				+ (heapMaxSize / 2000000));
		return ((heapSize / 1000000) - (heapFreeSize / 1000000)) > (heapMaxSize / 2000000);
	}

}