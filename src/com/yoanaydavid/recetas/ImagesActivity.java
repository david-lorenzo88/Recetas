package com.yoanaydavid.recetas;

import com.yoanaydavid.recetas.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Displays images from an SD card.
 */
public class ImagesActivity extends Activity {
	final static int IMAGES_ACTIVITY_CODE = 2;
    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_grid);

        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {BaseColumns._ID};
        // Create the cursor pointing to the SDCard
        
        
        cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);
        // Get the column index of the Thumbnails Image ID
        columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);

        GridView sdcardImages = (GridView) findViewById(R.id.sdcard);
        sdcardImages.setAdapter(new ImageAdapter(this));

        // Set up a click listener
        sdcardImages.setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Get the data location of the image
                String[] projection = {MediaColumns.DATA};
                cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, // Which columns to return
                        null,       // Return all rows
                        null,
                        null);
                columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                cursor.moveToPosition(position);
                // Get image filename
                String imagePath = cursor.getString(columnIndex);
                // Use this path to do further processing, i.e. full screen display
                //Toast.makeText(getBaseContext(), imagePath, Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("path", imagePath);
                setResult(IMAGES_ACTIVITY_CODE, resultIntent);
                finish();
            }
        });
    }
    
    

    /**
     * Adapter for our image files.
     */
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        @Override
		public int getCount() {
            return cursor.getCount();
        }
        @Override
		public Object getItem(int position) {
            return position;
        }
        @Override
		public long getItemId(int position) {
            return position;
        }
        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
            /*ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                // Move cursor to current position
                cursor.moveToPosition(position);
                // Get the current value for the requested column
                int imageID = cursor.getInt(columnIndex);
                
                Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                		imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // Set the content of the image based on the provided URI
                //picturesView.setImageURI(Uri.withAppendedPath(
                //        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
                picturesView.setImageBitmap(bm);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setPadding(4, 4, 4, 4);
                picturesView.setLayoutParams(new GridView.LayoutParams(100, 100));
            }
            else {
                picturesView = (ImageView)convertView;
            }
            return picturesView;*/
            
            cursor.moveToPosition(position);
            int imageID = cursor.getInt(columnIndex);

            ImageView picturesView = convertView==null ? new ImageView(context):(ImageView) convertView;

            Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
            		imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);

            picturesView.setImageBitmap(bm);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            picturesView.setPadding(4, 4, 4, 4);
            picturesView.setLayoutParams(new GridView.LayoutParams(100, 100));

            return picturesView;
        }
    }
}
