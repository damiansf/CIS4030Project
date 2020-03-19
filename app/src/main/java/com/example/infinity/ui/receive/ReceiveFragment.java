package com.example.infinity.ui.receive;

import android.app.admin.FreezePeriod;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.infinity.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;

public class ReceiveFragment extends Fragment {
    private ReceiveViewModel receiveViewModel;

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
        File[] files = new File(primaryExternalStorage.getPath() + "/quicklink-user-files").listFiles();

        for (File file: files) {
            CheckBox newCheck = new CheckBox(root.getContext());
            newCheck.setText(file.getName());
            fileLayout.addView(newCheck);
        }

        return root;
    }
}
