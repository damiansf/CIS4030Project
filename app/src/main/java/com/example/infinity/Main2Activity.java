package com.example.infinity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.infinity.ui.ImportFileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main2Activity extends AppCompatActivity implements ImportFileFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public File getPrimaryExternalStorage() {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
        File primaryExternalStorage = externalStorageVolumes[0];
        return primaryExternalStorage;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        int cut = result.lastIndexOf('.');
        if (cut != -1) {
            result = result.substring(0, cut);
        }
        return result;
    }

    public File copy(Uri src, String dst) throws IOException {
        InputStream in =  getContentResolver().openInputStream(src);
        File newFile = new File(dst);
        OutputStream out = new FileOutputStream(newFile);
        byte[] buf = new byte[1024];
        int len;
        while((len=in.read(buf))>0){
            out.write(buf,0,len);
        }
        out.close();
        in.close();
        return newFile;
    }

    @Override
    public void onFragmentInteraction(String fileName, String uniqueID, String fileType) {
        if (file_uri != null) {
            //create new file name
            File file = new File(file_uri.getPath());
            Log.i("File path", file_uri.getPath());
            fileName = getFileName(file_uri);
            String newFileName = fileName + "-" + fileType + "-" + uniqueID + ".pdf";

            //create user files directory if it doesn't exist
            File primaryExternalStorage = getPrimaryExternalStorage();
            String userFilesStoragePath = primaryExternalStorage.getPath() + "/quicklink-user-files";
            File userFilesStorage = new File(userFilesStoragePath);
            if(!userFilesStorage.exists()) {
                if (!userFilesStorage.mkdir()) {
                    Log.i("Not Created", userFilesStoragePath + " can't be created.");
                } else {
                    Log.i("Created", userFilesStoragePath + " can be created.");
                }
            }
            else {
                Log.i("Already Exists", userFilesStoragePath +" already exits.");
            }
            Log.i("test", newFileName);

            //copy the selected file to the user files directory with the new name
            String newFilePath = userFilesStoragePath + "/" + newFileName;
            try {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    Toast toast = Toast.makeText(getApplicationContext(),"Permission to read external storage required.", Toast.LENGTH_SHORT);
                    return;
                }

                File newFile = copy(file_uri, newFilePath);
                Log.i("Copy Success", "Successfully copied " + file.getPath() + " to " + newFile.getPath());
            }
            catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),"Failed to import file.", Toast.LENGTH_SHORT);
                toast.show();
                Log.i("Copy Fail", "Failed to copy " + file.getPath() + " to " + newFilePath);
                Log.i("IO Exception", e.toString());
            }
        }

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStack();
//        }
        file_uri = null; //reset uri for next imported file
    }

    public void importFile(View view) {
        Fragment importFileFragment = new ImportFileFragment();
        replaceFragment(importFileFragment);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;
    private Uri file_uri = null;

    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode ==PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            if (resultData != null) {
                file_uri = resultData.getData();
                // Perform operations on the document using its URI.
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }


    public void deleteFile() {

    }

    public void transferFilesViaNFC() {

    }

    public void exportFiles() {

    }
}
