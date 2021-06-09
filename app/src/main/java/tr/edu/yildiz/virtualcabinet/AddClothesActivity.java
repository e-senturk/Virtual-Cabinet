package tr.edu.yildiz.virtualcabinet;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class AddClothesActivity extends AppCompatActivity {
    Bitmap clothesBitmap;
    ImageView clothesImage;
    Uri clothesUri;
    Spinner clothesWearingLocationSpinner;
    TextView clothesTypeText;
    TextView clothesColorText;
    TextView clothesPurchaseDateText;
    DatePickerDialog datePickerDialog;
    TextView clothesPatternText;
    TextView clothesPriceText;
    String colorHex;
    ConstraintLayout parentLayout;
    boolean initialize;
    int oldIndex;
    Clothes oldClothes;
    String drawerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothes);
        setTitle(R.string.add_new_clothes);
        clothesUri = null;
        clothesBitmap = null;
        clothesImage = findViewById(R.id.addClothesImage);
        clothesWearingLocationSpinner = findViewById(R.id.addClothesWearingLocation);
        clothesTypeText = findViewById(R.id.addClothesType);
        clothesColorText = findViewById(R.id.addClothesColor);
        clothesPurchaseDateText = findViewById(R.id.addClothesPurchaseDate);
        clothesPatternText = findViewById(R.id.addClothesPattern);
        clothesPriceText = findViewById(R.id.addClothesPrice);
        parentLayout = findViewById(R.id.addClothesConstraintLayout);
        Intent intent = getIntent();
        initialize = intent.getBooleanExtra("initialize", false);
        oldIndex = intent.getIntExtra("index", -1);
        drawerName = "";
        if (initialize) {
            oldClothes = intent.getParcelableExtra("clothes");
            drawerName = intent.getStringExtra("drawer_name");
            clothesUri = Tools.getUriFromStringPath(oldClothes.getAttachmentPath(), this);
            clothesBitmap = Tools.initializeImageView(this, clothesUri, clothesImage);
            colorHex = oldClothes.getColor();
            clothesColorText.setBackgroundColor(Color.parseColor(colorHex));
            clothesPurchaseDateText.setText(oldClothes.getPurchaseDate());
            clothesPatternText.setText(oldClothes.getClothesPattern());
            clothesPriceText.setText(String.valueOf(oldClothes.getPrice()));
            ArrayList<String> values = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wearing_place)));
            clothesWearingLocationSpinner.setSelection(values.indexOf(oldClothes.getWearingLocation()));
        } else {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.DAY_OF_WEEK);
            int year = calendar.get(Calendar.YEAR);
            clothesPurchaseDateText.setText(String.format(getString(R.string.date), day, (month + 1), year));
        }
    }

    public void saveClothes(View view) {
        String path;
        if (clothesUri != null)
            path = Tools.storeAndGetPath(this, clothesUri, null);
        else {
            Tools.showSnackBar(getString(R.string.you_must_select_at_least_2_items), parentLayout, this, Snackbar.LENGTH_SHORT);
            return;
        }
        String wearingLocation = clothesWearingLocationSpinner.getSelectedItem().toString();
        String type = clothesTypeText.getText().toString();
        String color = colorHex;
        String purchaseDate = clothesPurchaseDateText.getText().toString();
        String pattern = clothesPatternText.getText().toString();
        double price;
        try {
            price = Double.parseDouble(clothesPriceText.getText().toString());
            price = Double.parseDouble(String.format(Locale.getDefault(), "%.2f", price).replace(",", "."));
        } catch (Exception e) {
            System.err.println("Price isn't valid set to 0");
            price = 0.00;
        }
        Clothes newClothes = new Clothes(path, wearingLocation, type, color, purchaseDate, pattern, price, drawerName);
        Intent data = new Intent();
        data.putExtra("new_clothes", newClothes);
        if (initialize) {
            data.putExtra("index", oldIndex);
            Database.updateClothes(this, MODE_PRIVATE, newClothes, oldClothes.getAttachmentPath());
        }
        setResult(RESULT_OK, data);
        finish();
    }

    public void selectClothesImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    // opens gallery directly after permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // sets user image in field
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            clothesBitmap = Tools.initializeImageView(this, data.getData(), clothesImage);
            if (clothesBitmap != null) {
                clothesUri = Tools.getImageUri(this, clothesBitmap);
                Color dominant = Tools.getDominantColor(clothesBitmap);
                clothesColorText.setBackgroundColor(dominant.toArgb());
                colorHex = "#" + Integer.toHexString(dominant.toArgb());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void pickDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int monthCurrent = calendar.get(Calendar.DAY_OF_WEEK);
        int yearCurrent = calendar.get(Calendar.YEAR);
        System.out.println(yearCurrent);
        datePickerDialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) ->
                clothesPurchaseDateText.setText(String.format(getString(R.string.date), dayOfMonth, (month + 1), year)),
                yearCurrent, monthCurrent, dayCurrent);
        datePickerDialog.show();
    }

    public void selectColor(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle(getString(R.string.title_color_picker))
                .setPreferenceName(getString(R.string.title_color_picker))
                .setPositiveButton(getString(R.string.confirm),
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            clothesColorText.setBackgroundColor(envelope.getColor());
                            colorHex = "#" + envelope.getHexCode();
                        })
                .setNegativeButton(getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true) // default is true. If false, do not show the AlphaSlideBar.
                .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                .show();
    }
}