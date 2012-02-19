package com.yoanaydavid.recetas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.FileXML;
import com.yoanaydavid.recetas.java.Mode;


public class NuevoPasoFragment extends Fragment {


	
	final static int IMAGES_ACTIVITY_CODE = 2;
	final static String JPEG_FILE_SUFFIX = ".jpg";

	
	private Button guardarPasoButton;
	private Button imagenButton;
	private EditText txtDescripcion;
	private ImageView imageViewNuevoPaso;
	private String mCurrentPhotoPath = null;
	private OnClickListener listener = null;
	


	public EditText getTxtDescripcion() {
		return txtDescripcion;
	}



	public void setTxtDescripcion(EditText txtDescripcion) {
		this.txtDescripcion = txtDescripcion;
	}



	public Button getImagenButton() {
		return imagenButton;
	}



	public void setImagenButton(Button imagenButton) {
		this.imagenButton = imagenButton;
	}



	public String getmCurrentPhotoPath() {
		return mCurrentPhotoPath;
	}



	public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
		this.mCurrentPhotoPath = mCurrentPhotoPath;
	}



	public NuevoPasoFragment(OnClickListener listener) {
		this.listener = listener;
	}

	
	
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public void dispatchTakePictureIntent(int actionCode) {

		String action = MediaStore.ACTION_IMAGE_CAPTURE;
		if (isIntentAvailable(getActivity(), action)) {
			File f = getFile();

			mCurrentPhotoPath = f.getAbsolutePath();

			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			//Log.i("test", Integer.toString(actionCode));
			startActivityForResult(takePictureIntent, actionCode);

		}

	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment

		// Obtenemos el layout y a partir de él personalizamos la Views que
		// contiene
		View content = inflater.inflate(R.layout.nuevo_paso, container,
				false);

		guardarPasoButton = (Button) content.findViewById(R.id.guardarPasoButton);
		imagenButton = (Button) content.findViewById(R.id.imagenButton);
		txtDescripcion = (EditText) content.findViewById(R.id.txtDescripcion);
		imageViewNuevoPaso = (ImageView) content.findViewById(R.id.imageViewNuevoPaso);


		// añadimos los listeners
		imagenButton.setOnClickListener(listener);
		guardarPasoButton.setOnClickListener(listener);

		return content;

	}
	/*public void setPic(String path) {

		Bitmap bitmap = BitmapFactory.decodeFile(path);
		imageViewNuevoPaso.setImageBitmap(bitmap);
		String thumbPath = saveThumbnail(bitmap, path);
		if(thumbPath != null)
			mCurrentPhotoPath = thumbPath;

	}*/
	
	public void setPic(Bitmap bm, String path) {

		mCurrentPhotoPath = path;
		imageViewNuevoPaso.setImageBitmap(bm);
		/*String thumbPath = saveThumbnail(bm, path);
		if(thumbPath != null)
			mCurrentPhotoPath = thumbPath;*/

	}

	private String getFileName() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "Receta_" + timeStamp;
		return imageFileName + JPEG_FILE_SUFFIX;
	}

	private File getFile() {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		path.mkdirs();
		return new File(path, getFileName());
	}

	private String saveThumbnail(Bitmap bm, String filePath) {
		//Obtenemos la ruta correcta
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/.thumbnails/");
		path.mkdirs();		
		
		String[] partes = filePath.split("/");
		File file = new File(path, partes[partes.length-1]);
		
		//Escalamos el bitmap
		bm = scaleBitmap(bm);
		OutputStream fOut = null;
		//txtDescripcion.setText(file.getAbsolutePath());
		
		try {
			fOut = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);

			fOut.flush();

			fOut.close();

			MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
					file.getAbsolutePath(), file.getName(), file.getName());
			
			return file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	private Bitmap scaleBitmap(Bitmap bm) {
		Display display = ((WindowManager) getActivity().getBaseContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Hacemos el ancho de la imagen el 25% del ancho de la pantalla
		int scaleWidth = (int) (display.getWidth() * 0.25);

		// Obtenemos el ratio de escalado del ancho
		int ratio = bm.getWidth() / scaleWidth;
		// Aplicamos el ratio al alto para mantener las proporciones
		int scaleHeight = bm.getHeight() / ratio;
		return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
	}

	

}
