package scottm.cs378.example.intentexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class IntentExample extends Activity {

	private static final String TAG = "INTENT EXAMPLE";
	private static int TAKE_PICTURE_CODE = 526719;
	private Uri outputFileUri;
	private String fileName;
    private String extension;
    private int pictureNumber; // Should read from shared preferences so
                               // we don't restart from zero each time!
    private String pictureLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.d(TAG, "in onCreate");
	    setContentView(R.layout.activity_intent_example);
	    fileName = Environment.getExternalStorageDirectory() + "/intentExamplePhotos/test";
        extension = ".jpg";
        pictureNumber = 0; // should read from sharePreferences
	}
	
	@Override
	public void onPause() { 
	    super.onPause();
	    Log.d(TAG, "in onPause");
	}

	@Override
	public void onStop() {
	    super.onStop();
	    Log.d(TAG, "in onStop");
	}

	@Override
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent data){

	    ImageView img = (ImageView)this.findViewById(R.id.imageView1);         
       
		if (requestCode == TAKE_PICTURE_CODE && resultCode == RESULT_OK){
			// change picture in ImageView to image just taken

			// reduce size of image
			BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 4;	    
			Bitmap bmp = BitmapFactory.decodeFile(pictureLocation, options);
			img.setImageBitmap(bmp);
			
			Toast.makeText(this, "Photo saved to: " 
					+ outputFileUri.toString(), Toast.LENGTH_LONG).show();
			
			Log.d(TAG, "Photo saved to: " + outputFileUri.toString());

            // increment the picture number, so next picture saved as different file
            pictureNumber++;
		}
		else if(resultCode == RESULT_CANCELED) {
		    Bitmap onPictureImage 
		            = BitmapFactory.decodeResource(getResources(),
		                    R.drawable.no_picture);
		    img.setImageBitmap(onPictureImage);
		}
		
		Log.d(TAG, "request code: " + requestCode);
		Log.d(TAG, "result code: " + resultCode);
		
//		File file = new File(fileName);
//		if(file.exists())
//			Log.d(TAG, "file exists: " + file);
//		else
//			Log.d(TAG, "file does not exist: " + file);
 
	}
	
	
	public void takePhoto(View v) {
		// create directory if necessary
		File photoDir 
				= new File(Environment.getExternalStorageDirectory() 
				+ "/intentExamplePhotos/");
		
		if(photoDir.mkdirs())
			Log.d(TAG, "mkdirs returned true: " + photoDir);
		else
			Log.d(TAG, "mkdirs returned false: " + photoDir);
		
		// create Intent to take picture via camera and specify location
		// to store image so we can retrieve easily
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureLocation = fileName + pictureNumber + extension;
		File file = new File(pictureLocation);
		outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {

            Toast.makeText(this,
                    getString(R.string.taking_picture_toast_message),
                    Toast.LENGTH_LONG).show();

            startActivityForResult(intent, TAKE_PICTURE_CODE);
        }
        else {
            Toast.makeText(this,
                    getString(R.string.no_camera_toast_message),
                    Toast.LENGTH_LONG).show();
        }
	}

}