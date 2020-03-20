package com.example.infinity.ui.receive;

import android.app.admin.FreezePeriod;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.infinity.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceiveFragment extends Fragment {
    private ReceiveViewModel receiveViewModel;
    private ArrayList<String> selectedFiles = new ArrayList<>();
    private String path;
    private HashMap<String, Integer> fileIdMap = new HashMap<>();
    private int count = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        receiveViewModel = ViewModelProviders.of(this).get(ReceiveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_receive, container, false);
        final TextView textView = root.findViewById(R.id.text_ReceiveBaseText);
        receiveViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });

        LinearLayout fileLayout = root.findViewById(R.id.fileLayoutReceive);
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(root.getContext(), null);
        File primaryExternalStorage = externalStorageVolumes[0];
        this.path = primaryExternalStorage.getPath();
        File[] files = new File(path + "/quicklink-user-files").listFiles(); //TODO change this path to the transfer path

        Button deleteButton = root.findViewById(R.id.btn_delete_receive);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                for (String fileName: selectedFiles) {
                    File deleteFile = null;
                    File[] files = new File(path + "/quicklink-user-files").listFiles(); //TODO change this path to the transfer path

                    for(File file: files) {
                        if (file.getName().equals(fileName)) {
                            deleteFile = file;
                            break;
                        }
                    }
                    if (deleteFile != null) {
                        ConstraintLayout grandParent = (ConstraintLayout)v.getParent();
                        LinearLayout parent = (LinearLayout) grandParent.getViewById(R.id.fileLayoutReceive);
                        CheckBox removeBox = (CheckBox)parent.findViewById(fileIdMap.get(deleteFile.getName()));
                        parent.removeView(removeBox);
                        deleteFile.delete();
                    }

                }
                selectedFiles.clear();
            }
        });

        Button saveButton = root.findViewById(R.id.btn_save_as);
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                //TODO
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
