package com.example.infinity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;

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

    @Override
    public void onFragmentInteraction(String fileName, String uniqueID, String fileType) {
        String newFileName = fileName + "-" + fileType + "-" + uniqueID + ".pdf";
        Log.i("test", newFileName);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStack();
//        }
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

    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }


    public void deleteFile() {

    }

    public void transferFilesViaNFC() {

    }

    public void exportFiles() {

    }
}
