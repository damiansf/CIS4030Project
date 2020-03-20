package com.example.infinity.ui.manage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.infinity.MainActivity;
import com.example.infinity.R;
import com.example.infinity.ui.transfer.TransferFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class ManageFragment extends Fragment {

    private ManageViewModel manageViewModel;
    private ArrayList<String> selectedFiles = new ArrayList<>();
    private String path;
    private HashMap<String, Integer> fileIdMap = new HashMap<>();
    private int count = 0;
    private String USER_FILE_DIR = "/quicklink-user-files";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        manageViewModel =
                ViewModelProviders.of(this).get(ManageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_manage, container, false);
        final TextView textView = root.findViewById(R.id.text_ManageBaseText);
        manageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        LinearLayout fileLayout = root.findViewById(R.id.fileLayout);
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(root.getContext(), null);
        File primaryExternalStorage = externalStorageVolumes[0];
        this.path = primaryExternalStorage.getPath();
        File[] files = new File(path + USER_FILE_DIR).listFiles();

        Button deleteButton = root.findViewById(R.id.btn_delete_manage);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                for (String fileName: selectedFiles) {
                    File deleteFile = null;
                    File[] files = new File(path + USER_FILE_DIR).listFiles();

                    for(File file: files) {
                        if (file.getName().equals(fileName)) {
                            deleteFile = file;
                            break;
                        }
                    }
                    if (deleteFile != null) {
                        ConstraintLayout grandParent = (ConstraintLayout)v.getParent();
                        LinearLayout parent = (LinearLayout) grandParent.getViewById(R.id.fileLayout);
                        CheckBox removeBox = (CheckBox)parent.findViewById(fileIdMap.get(deleteFile.getName()));
                        parent.removeView(removeBox);
                        deleteFile.delete();
                    }
                }
                selectedFiles.clear();
            }
        });

        Button transferButton = root.findViewById(R.id.btn_transfer);
        transferButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                // If no files selected for transfer, do nothing
                if (selectedFiles.size() < 1) {
                    Toast.makeText(getActivity().getApplicationContext(), "Need to select files to transfer", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Transition to the transfer fragment, passing the selected file paths as an argument
                TransferFragment fragment = new TransferFragment();
                Bundle arguments = new Bundle();
                arguments.putStringArrayList("filePaths", selectedFiles);
                arguments.putString("basePath", path);
                arguments.putString("userFileDir", USER_FILE_DIR);
                fragment.setArguments(arguments);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
            }
        });

        for (File file: files) {
            CheckBox newCheck = new CheckBox(root.getContext());
            newCheck.setText(file.getName());
            newCheck.setId(count);
            fileIdMap.put(file.getName(), count);
            count++;
            newCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        selectedFiles.add(buttonView.getText().toString());
                    } else {
                        selectedFiles.remove(buttonView.getText().toString());
                    }
                }
            });
            fileLayout.addView(newCheck);
        }

        return root;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.commit();
    }
}
