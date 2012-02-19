package com.yoanaydavid.recetas;

import java.io.File;
import java.io.FileNotFoundException;

import com.yoanaydavid.recetas.java.Paso;

import android.app.Activity;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

public class NuevoPasoActivity extends FragmentActivity implements
		OnClickListener {
	final static int NUEVO_PASO_ACTIVITY_CODE = 5;
	final static int SELECT_PHOTO = 6;
	final CharSequence[] items = { "Desde archivo", "Desde cámara" };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NuevoPasoFragment npf = new NuevoPasoFragment(this);
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, npf).commit();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		NuevoPasoFragment npf = (NuevoPasoFragment) getSupportFragmentManager()
				.findFragmentById(android.R.id.content);
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
				ex.printStackTrace();
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
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		Button b = (Button) v;
		switch (b.getId()) {
		case R.id.imagenButton:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Selecciona una fuente");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					NuevoPasoFragment npf = (NuevoPasoFragment) getSupportFragmentManager()
							.findFragmentById(android.R.id.content);
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
		case R.id.guardarPasoButton:
			// Comprobamos que tenemos una descripcion del paso
			NuevoPasoFragment np = (NuevoPasoFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
			String desc = np.getTxtDescripcion().getText().toString().trim();
			if (!desc.equals("")) {
				// Ocultamos el teclado
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(np.getTxtDescripcion()
						.getWindowToken(), 0);

				
				
				// Guardamos los resultados y finalizamos
				
				Intent intent = new Intent();
				intent.putExtra("descripcion", desc);
				
				if (np.getmCurrentPhotoPath() != null) {

					intent.putExtra("path", np.getmCurrentPhotoPath());
				}
				
				setResult(RESULT_OK, intent);
				
				finish();

				

			} else {
				Toast.makeText(getBaseContext(),
						"¡Falta la descripción del paso!", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}
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

		final int REQUIRED_SIZE = (int) (display.getWidth() * 0.25);

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

}
