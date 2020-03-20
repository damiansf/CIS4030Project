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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.infinity.MainActivity;
import com.example.infinity.R;

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
        File[] files = new File(path + "/quicklink-user-files").listFiles();

        Button deleteButton = root.findViewById(R.id.btn_delete_manage);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                for (String fileName: selectedFiles) {
                    File deleteFile = null;
                    File[] files = new File(path + "/quicklink-user-files").listFiles();

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
}
