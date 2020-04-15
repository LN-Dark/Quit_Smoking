package com.lua.quitsmoking.ui.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lua.quitsmoking.R;

public class InfoFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_info, container, false);
        ImageView btn_telegram = root.findViewById(R.id.btn_telegram);
        ImageView btn_paypal = root.findViewById(R.id.btn_paypal);
        ImageView btn_github = root.findViewById(R.id.btn_github);
        btn_github.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LN-Dark"));
            startActivity(browserIntent);
        });
        btn_telegram.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/LN_DarK"));
            startActivity(browserIntent);
        });
        btn_paypal.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(root.getContext());
            LinearLayout layout = new LinearLayout(root.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            builder.setIcon(root.getContext().getDrawable(R.mipmap.ic_launcher));
            builder.setTitle(getString(R.string.doar));
            layout.setGravity(Gravity.CENTER);
            final TextView espaco4 = new TextView(root.getContext());
            espaco4.setText(R.string.muitoobrigado);
            espaco4.setTextSize(19);
            espaco4.setGravity(Gravity.CENTER);
            layout.addView(espaco4);
            final TextView espaco2 = new TextView(root.getContext());
            espaco2.setText("\n");
            espaco2.setTextSize(25);
            espaco2.setGravity(Gravity.CENTER);
            layout.addView(espaco2);
            builder.setView(layout);
            builder.setPositiveButton(getString(R.string.doar), (dialog, which) -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/pedrocruz77"));
                startActivity(browserIntent);
            });
            builder.setNeutralButton(getString(R.string.cancelar), (dialog, which) -> dialog.dismiss());
            builder.show();
        });
        return root;
    }
}
