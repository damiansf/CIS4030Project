package com.example.infinity.ui.transfer;

import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.infinity.R;

import java.io.File;
import java.util.ArrayList;

public class TransferFragment extends Fragment {

    private TransferViewModel galleryViewModel;
    private ArrayList<String> filesToTransfer = new ArrayList<>();

    private NfcAdapter nfc = null;

    private String baseDir;
    private String userFileDir;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(TransferViewModel.class);
        View root = inflater.inflate(R.layout.fragment_transfer, container, false);
        final TextView textView = root.findViewById(R.id.text_TransferBaseText);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Bundle arguments = getArguments();
        filesToTransfer = arguments.getStringArrayList("filePaths");
        baseDir = arguments.getString("basePath");
        userFileDir = arguments.getString("userFileDir");

        // Set up the message to display files being transferred
        String outStr = "Ready to transfer; touch your phone to another to transfer the following documents:\n";
        for (String fileName: filesToTransfer) {
            outStr += fileName + "\n";
        }
        final TextView outTxt = root.findViewById(R.id.txt_out);

        // Set up the nfc adapter
        nfc = NfcAdapter.getDefaultAdapter(getContext());
        nfc.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {
            @Override
            public void onNdefPushComplete(NfcEvent event) {
                // Tell the user the transfer has been initiated
                outTxt.setText("Transfer has been started.  Tap your phone to another phone to begin additional transfers, or press the back button to return to the previous screen");
            }
        }, getActivity());
        Uri[] fileUris = new Uri[filesToTransfer.size()];
        for(int i = 0; i < filesToTransfer.size(); i++) {
            String fileStr = "file:/" + new File(baseDir + userFileDir + "/" + filesToTransfer.get(i)).toString();
            Uri temp = Uri.parse(fileStr);
            fileUris[i] = temp;
        }
        nfc.setBeamPushUris(fileUris, getActivity());

        outTxt.setText(outStr);

        return root;
    }
}
