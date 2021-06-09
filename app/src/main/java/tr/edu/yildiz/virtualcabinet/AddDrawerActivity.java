package tr.edu.yildiz.virtualcabinet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.models.Drawer;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;


public class AddDrawerActivity extends AppCompatActivity {
    TextView drawerNameText;
    Drawer newDrawer;
    ConstraintLayout fullLayout;
    String backgroundHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drawer);
        setTitle(R.string.add_new_drawer);
        drawerNameText = findViewById(R.id.addDrawerName);
        fullLayout = findViewById(R.id.addDrawerConstraintLayout);
        backgroundHex = "#" + Integer.toHexString(getColor(R.color.brown));
        newDrawer = new Drawer("", backgroundHex);
    }

    public void saveDrawer(View view) {
        if (drawerNameText.getText().toString().equals("")) {
            Tools.showSnackBar(getString(R.string.please_enter_a_drawer_name), fullLayout, this, Snackbar.LENGTH_SHORT);
            return;
        }
        newDrawer.setName(drawerNameText.getText().toString());
        newDrawer.setColor(backgroundHex);
        Database.addDrawer(this, MODE_PRIVATE, newDrawer);
        for (Clothes clothes : newDrawer.getClothesList()) {
            clothes.setDrawerName(newDrawer.getName());
            Database.addClothes(this, MODE_PRIVATE, clothes);
        }
        Intent data = new Intent();
        data.putExtra("new_drawer", newDrawer);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Clothes> newClothes = data.getParcelableArrayListExtra("clothes_list");
            newDrawer.setClothesList(newClothes);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void listClothes(View view) {
        Intent intent = new Intent(AddDrawerActivity.this, ClothesListActivity.class);
        intent.putExtra("clothes_list", newDrawer.getClothesList());
        intent.putExtra("clickable", false);
        intent.putExtra("addable", true);
        startActivityForResult(intent, 1);
    }

    public void selectColor(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle(getString(R.string.title_color_picker))
                .setPreferenceName(getString(R.string.title_color_picker))
                .setPositiveButton(getString(R.string.confirm),
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            fullLayout.setBackgroundColor(envelope.getColor());
                            backgroundHex = "#" + envelope.getHexCode();
                        })
                .setNegativeButton(getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                .show();
    }
}