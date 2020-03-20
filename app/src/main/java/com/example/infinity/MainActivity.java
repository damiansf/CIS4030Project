package com.example.infinity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.infinity.ui.ImportFileFragment;
import com.example.infinity.ui.home.HomeFragment;
import com.example.infinity.ui.manage.ManageFragment;
import com.example.infinity.ui.receive.ReceiveFragment;
import com.example.infinity.ui.transfer.TransferFragment;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ImportFileFragment.OnFragmentInteractionListener {

    private DrawerLayout drawer;

    private Set<String> filePaths = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.i("No permission", "");

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast toast = Toast.makeText(getApplicationContext(),"Permission to read external storage required to import files.", Toast.LENGTH_SHORT);
                toast.show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        42069);
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        42069);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return;
        }

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

        String userFilesReceivedStoragePath = primaryExternalStorage.getPath() + "/quicklink-received-files";
        File userFilesReceivedStorage = new File(userFilesReceivedStoragePath);
        if(!userFilesReceivedStorage.exists()) {
            if (!userFilesReceivedStorage.mkdir()) {
                Log.i("Not Created", userFilesReceivedStoragePath + " can't be created.");
            } else {
                Log.i("Created", userFilesReceivedStoragePath + " can be created.");
            }
        }
        else {
            Log.i("Already Exists", userFilesReceivedStoragePath +" already exits.");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_manage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageFragment()).commit();
                break;
            case R.id.nav_receive:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReceiveFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            Log.i("test", newFileName);

            //copy the selected file to the user files directory with the new name
            String newFilePath = userFilesStoragePath + "/" + newFileName;
            Log.i("newFilePath", newFilePath);
            try {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    Log.i("No permission", "");

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Toast toast = Toast.makeText(getApplicationContext(),"Permission to read external storage required to import files.", Toast.LENGTH_SHORT);
                        toast.show();
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                42069);
                    } else {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                42069);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }

                    return;
                }

                Log.i("Permission", "asdf");
                File newFile = copy(file_uri, newFilePath);
                Log.i("Copy Success", "Successfully copied " + file.getPath() + " to " + newFile.getPath());
                filePaths.add(newFilePath);
            }
            catch (IOException e) {
                Toast toast = Toast.makeText(getApplicationContext(),"Failed to import file.", Toast.LENGTH_SHORT);
                toast.show();
                Log.i("Copy Fail", "Failed to copy " + file.getPath() + " to " + newFilePath);
                Log.i("IO Exception", e.toString());
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        file_uri = null; //reset uri for next imported file
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 42069: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void importFile(View view) {
        Fragment importFileFragment = new ImportFileFragment();
        replaceFragment(importFileFragment);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
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
