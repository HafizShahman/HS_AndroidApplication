package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;
    /**
     * step 19
     */
    protected int REQUEST_CAMERA = 321;
    protected int SELECT_FILE = 123;
    protected int OPEN_FILE = 111;
    protected File destCamera;
    protected String filename;
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference storageReference;
    FirebaseStorage storage;
    AlertDialog loading_dialog;
    ImageView iv_profile;
    MaterialSpinner sp_title;
    EditText et_first_name, et_last_name, et_email, et_address, et_city, et_postcode,
            et_state;
    Button btn_save;
    /**
     * step 5
     */
    String[] arr_title = {"Mr.", "Ms.", "Mrs.", "Miss"};
    String selected_title = "";
    String holdPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("Profile");
        /**
         * step 29
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /**
         * step 4
         */
        iv_profile = findViewById(R.id.iv_profile);
        sp_title = findViewById(R.id.sp_title);
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        et_city = findViewById(R.id.et_city);
        et_postcode = findViewById(R.id.et_postcode);
        et_state = findViewById(R.id.et_state);
        btn_save = findViewById(R.id.btn_save);
        /**
         * step 6
         */
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptImageDialog();
            }
        });
        /**
         * step 20
         */
        sp_title.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                arr_title
        ));
        /**
         * step 21
         */
        sp_title.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object
                    item) {
                selected_title = view.getText().toString();
            }
        });
        /**
         * step 22
         */
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSave();
            }
        });

        /**
         * step 27
         */
        preFillEditText();
    }
    /**
     * step 28
     */
    private void preFillEditText() {

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("image_url").exists()) {
                        if
                        (!snapshot.child("image_url").getValue().toString().trim().equals("")) {
                            Glide.with(getApplicationContext())
                                    .load(snapshot.child("image_url").getValue().toString())
                                    .circleCrop()
                                    .into(iv_profile);
                        }
                    }
                    selected_title = snapshot.child("title").getValue().toString();
                    sp_title.setText(snapshot.child("title").getValue().toString());
                    et_first_name.setText(snapshot.child("first_name").getValue().toString());
                    et_last_name.setText(snapshot.child("last_name").getValue().toString());
                    et_email.setText(snapshot.child("email").getValue().toString());
                    et_address.setText(snapshot.child("address").getValue().toString());
                    et_city.setText(snapshot.child("city").getValue().toString());
                    et_postcode.setText(snapshot.child("postcode").getValue().toString());
                    et_state.setText(snapshot.child("state").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    /**
     * step 23
     */
    private void attemptSave() {
        String first_name = et_first_name.getText().toString().trim();
        String last_name = et_last_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String city = et_city.getText().toString().trim();
        String postcode = et_postcode.getText().toString().trim();
        String state = et_state.getText().toString().trim();

        if (selected_title.equals("") || first_name.equals("") || last_name.equals("") ||email.equals("") || address.equals("") || city.equals("") || postcode.equals("") || state.equals("")) {
            promptDialog("Ops!", "All fields cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait..");
            saveToFirebase(first_name, last_name, email, address, city, postcode, state);
        }
    }
    /**
     * step 24
     */
    private void saveToFirebase(final String first_name, final String last_name, final String
            email, final String address, final String city, final String postcode, final String state) {
        final Profile profile = new Profile();
        profile.setTitle(selected_title);
        profile.setFirst_name(first_name);
        profile.setLast_name(last_name);
        profile.setEmail(email);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setPostcode(postcode);
        profile.setState(state);
        if (!holdPath.equals("")) {
            try {
                InputStream stream = new FileInputStream(new File(holdPath));
                final StorageReference imageStorage = storageReference.child("users/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                UploadTask uploadTask = imageStorage.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(">>>>>>>", "failure");
                        loading_dialog.dismiss();
                        promptDialog("Saving information failed.", e.getMessage(), "Dismiss");
                        holdPath = "";
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(">>>>>>>>", "success!");
                        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(">>>>>>>", "image url : " +uri);
                                holdPath = "";

                                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("profile").child("image_url").setValue(uri.toString());
                                profile.setImage_url(uri.toString());

                                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("profile").setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            loading_dialog.dismiss();
                                            promptDialog("Ops!", "Cannot save information. Make sure you have an internet connection.", "OK");
                                        }
                                        loading_dialog.dismiss();
                                        AlertDialog.Builder builder;
                                        if (Build.VERSION.SDK_INT >=
                                                Build.VERSION_CODES.LOLLIPOP) {
                                            builder = new
                                                    AlertDialog.Builder(ProfileActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                        } else {
                                            builder = new
                                                    AlertDialog.Builder(ProfileActivity.this);
                                        }
                                        builder.setTitle("Success!")
                                                .setMessage("Information has been saved.")
                                                .setNegativeButton("OK", new
                                                        DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        })
                                                .show();
                                    }
                                });
                            }
                        });
                    }
                });
            } catch(FileNotFoundException e){
                e.printStackTrace();
                loading_dialog.dismiss();
                promptDialog("Saving information failed", e.getMessage(), "Dismiss");
                holdPath = "";
            }
        } else {
            mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("profile").setValue(profile
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        loading_dialog.dismiss();
                        promptDialog("Ops!", "Cannot save information. Make sure you have an internet connection.", "OK");
                    }
                    loading_dialog.dismiss();
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ProfileActivity.this,
                                android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ProfileActivity.this);
                    }
                    builder.setTitle("Success!")
                            .setMessage("Information has been saved.")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .show();
                }
            });
        }
    }
    /**
     * step 25
     */
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ProfileActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ProfileActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }
    /**
     * step 26
     */
    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ProfileActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ProfileActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * step 7
     */
    private void promptImageDialog() {
        final CharSequence[] items = { "Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Choose one..");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    boolean result = checkPermission(ProfileActivity.this,
                            Manifest.permission.CAMERA);
                    if(result) {
                        cameraIntent();
                    }
                } else if (items[item].equals("Gallery")) {
                    boolean result = checkPermission(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(result) {
                        galleryIntent();

                    }
                }
            }
        });
        builder.show();
    }
    /**
     * step 8
     */
    public boolean checkPermission(final Context context, String readExternalStorage)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (readExternalStorage == Manifest.permission.READ_EXTERNAL_STORAGE) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                            context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new
                                androidx.appcompat.app.AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Read external storage permission is necessary");
                                alertBuilder.setPositiveButton(android.R.string.yes, new
                                        DialogInterface.OnClickListener() {
                                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                            public void onClick(DialogInterface dialog, int which) {
                                                ActivityCompat.requestPermissions(ProfileActivity.this, new
                                                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
                                            }
                                        });
                        androidx.appcompat.app.AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new
                                String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else if (readExternalStorage == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                            context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new
                                androidx.appcompat.app.AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Write external storage permission is necessary");
                                alertBuilder.setPositiveButton(android.R.string.yes, new
                                        DialogInterface.OnClickListener() {
                                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                            public void onClick(DialogInterface dialog, int which) {
                                                ActivityCompat.requestPermissions(ProfileActivity.this, new
                                                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, OPEN_FILE);
                                            }
                                        });
                        androidx.appcompat.app.AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new
                                String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, OPEN_FILE);
                    }
                    return false;
                } else {
                    return true;
                }
            }else if (readExternalStorage == Manifest.permission.CAMERA) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                            context, Manifest.permission.CAMERA)) {
                        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new
                                androidx.appcompat.app.AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("Camera permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new
                                DialogInterface.OnClickListener() {
                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(ProfileActivity.this, new
                                                        String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CAMERA);
                                    }
                                });
                        androidx.appcompat.app.AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new
                                        String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CAMERA);
                    }
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
    /**
     * step 9
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(">>>>>>", "f requestCode : " +requestCode);
        Log.d(">>>>>>", "f permissions : " +permissions);
        Log.d(">>>>>>", "f grantResults : " +grantResults);
        if (requestCode == SELECT_FILE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            galleryIntent();
        } else if (requestCode == REQUEST_CAMERA && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            cameraIntent();
        } else if (requestCode == OPEN_FILE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ProfileActivity.this, "Permission granted. Please click the file again", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * step 10
     */
    private void cameraIntent() {
        filename = "MyCV_" + uploadDate() + rand() + ".jpg";
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator +
                "MyCV");
        destCamera = new File(folder.getAbsolutePath(), filename);
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            folder.delete();
        }
        String extStorageDirectory = getExternalFilesDir(null).getAbsolutePath();
        File outFile = new File(extStorageDirectory, "MyCV");
        createDirIfNotExists(outFile.getPath());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI;
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT))
            photoURI =
                    FileProvider.getUriForFile(ProfileActivity.this,BuildConfig.APPLICATION_ID +".provider",
                            destCamera);
        else
            photoURI = Uri.fromFile(destCamera);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    /**
     * step 11
     */
    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }
    /**
     * step 12
     */
    public boolean createDirIfNotExists(String path) {
        boolean ret = true;
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }
    /**
     * step 13
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onImageResult(data, SELECT_FILE);
            else if (requestCode == REQUEST_CAMERA)
                onImageResult(data, REQUEST_CAMERA);
        }
    }
    /**
     * step 14
     */
    private void onImageResult(Intent data, int type) {
        if(type == SELECT_FILE){
            filename = "MyCV_" + uploadDate() + rand() + ".jpg";
            Log.d("MPBF", filename);
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator
                    + "MyCV");
            File destination = new File(folder.getAbsolutePath(), filename);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if(success) {
                Uri selectedImageURI = data.getData();
                File imageFile = new File(getRealPathFromURI(selectedImageURI));
                try {
                    copyFile(imageFile, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
                Toast.makeText(ProfileActivity.this, "Folder cannot be created.",
                        Toast.LENGTH_SHORT).show();
        }else{
            holdPath = destCamera.getAbsolutePath();
            Glide.with(getApplicationContext())
                    .load(holdPath)
                    .circleCrop()
                    .into(iv_profile);
        }
    }
    /**
     * step 15
     */
    private String rand() {
        Random random = new Random();
        return String.format("%09d", random.nextInt(10000000));
    }
    /**
     * step 16
     */
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    /**
     * step 17
     */
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
            holdPath = destFile.getAbsolutePath();
            Glide.with(getApplicationContext())
                    .load(holdPath)
                    .circleCrop()
                    .into(iv_profile);
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
    /**
     * step 18
     */
    private String uploadDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}