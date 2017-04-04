package com.YOUR.PACKAGE.NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.util.ArrayList;
import android.Manifest;
import android.widget.Toast;



/**
 * Created on 3/12/2017.
 */

public class MyGallery extends AppCompatActivity implements MyGalleryRecyclerViewAdapter.CallbackInterface {


    ProgressBar spinner;
    String user_id = "";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static int RESULT_LOAD_IMG = 1;
    ArrayList<String> files_id, fileTypes;
    int img_count = 0;
    private final Object mPauseWorkLock = new Object();
    protected boolean mPauseWork = false;
    Context c;
    public final String APP_TAG = "MyCustomApp";
    ArrayList<String> myMedia = new ArrayList<String>();
    RecyclerView rv;
    private RecyclerView.LayoutManager mLayoutManager;
    MyGalleryRecyclerViewAdapter arrayAdapter;
    public static Activity fa;
    private Bundle b;

    public void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_gallery);

        c = this;
        fa = this;
        b = new Bundle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gallery");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPermission();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setTitle("Gallery");

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

				//Handle results and send to your next intent
                Intent intent = new Intent(c, UploadMedia.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                b.putString("getMedia", result.getUri().toString());
                b.putString("getType", "IMAGE");
                String filename=result.getUri().toString().substring(result.getUri().toString().lastIndexOf("/")+1);
                b.putString("getImageName", filename);

                intent.putExtras(b);

                c.startActivity(intent);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onHandleSelection(int position, String text) {

	

        Uri imageUri = Uri.fromFile(new File(text));
		//Custom image crop library
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem settingItem = menu.findItem(R.id.action_settings);
        settingItem.setVisible(false);
        MenuItem checkBox =  menu.findItem(R.id.action_delete_all);
        checkBox.setVisible(false);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem settingItem = menu.findItem(R.id.action_settings);
        settingItem.setVisible(false);
        MenuItem checkBox =  menu.findItem(R.id.action_delete_all);
        checkBox.setVisible(false);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
  
        switch (item.getItemId()) {
            
            case android.R.id.home:
               
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPermission() {

		//Check permissions
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE);

		//If permission denied
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        View view = null;
        
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_PERMISSIONS);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
  
                    myMedia = getFileList();

                    rv = (RecyclerView) findViewById(R.id.galleryGrid);
                    
                    arrayAdapter = new MyGalleryRecyclerViewAdapter(c, myMedia, fileTypes, files_id);

                    rv.setHasFixedSize(true);
                    mLayoutManager = new GridLayoutManager(c, 3);

                    rv.setLayoutManager(mLayoutManager);

                    int spanCount = 3; // 3 columns
                    int spacing = 3; // 3px
                    boolean includeEdge = false;
                    rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

                    if(rv.getAdapter() == null)
                    {

                        rv.setAdapter(arrayAdapter);
                    }

                } else {
                    
                    Toast.makeText(c, "Photo Access Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

	//Get Images and Videos from storage
    private ArrayList<String> getFileList()
    {
       
        ArrayList<String> fileList = new ArrayList<String>();
        try
        {
   
            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.TITLE,
            };

            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            Cursor image_cursor = c.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection,
                    selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

            img_count = image_cursor.getCount();
            files_id = new ArrayList<String>(img_count);
            fileTypes = new ArrayList<String>(img_count);

            int actual_video_column_index = image_cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int actual_media_type_column_index = image_cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int actual_files_id_column_index = image_cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            for ( int i = 0 ; i < image_cursor.getCount() ; i++ )
            {
                image_cursor.moveToPosition(i);
                
                String fileName = image_cursor.getString(actual_video_column_index);
                String fileType = image_cursor.getString(actual_media_type_column_index);
                
                fileList.add(fileName);
                fileTypes.add(fileType);
                String file_id = image_cursor.getString(actual_files_id_column_index);
                files_id.add(file_id);

            }
            image_cursor.close();
            //files_id = proj[1];

            return fileList;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }


    }


}
