package tr.edu.yildiz.virtualcabinet;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class CombineActivity extends AppCompatActivity {
    ImageView combineImage;
    TextView combineNameText;
    Combine combine;
    boolean initialize;
    int index;
    String initializeName;
    ConstraintLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine);
        setTitle(R.string.add_new_combine);
        combineImage = findViewById(R.id.combineImageView);
        combineNameText = findViewById(R.id.combineNameText);
        parentLayout = findViewById(R.id.combineActivityConstraintLayout);

        Intent intent = getIntent();
        combine = intent.getParcelableExtra("combine");
        initialize = intent.getBooleanExtra("initialize", false);
        if (initialize) {
            initializeName = combine.getName();
            combineNameText.setText(initializeName);
        }
        index = intent.getIntExtra("index", -1);
        if (combine != null) {
            combineImage.setImageBitmap(combine.createMergedImage(this));
            ArrayList<String> paths = combine.generateArrayList();
            int i = 0;
            for (String path : paths) {
                Uri uri = Tools.getUriFromStringPath(path, this);
                Bitmap bitmap = Tools.generateCombineBitmap(this, uri);
                if (bitmap == null) {
                    i++;
                } else {
                    bitmap.recycle();
                }
                if (i == 4) {
                    Database.removeCombine(this, MODE_PRIVATE, combine.getName());
                    finish();
                }
            }
        }


    }


    public void saveCombine(View view) {
        combine.setName(combineNameText.getText().toString());
        boolean result;
        if (!initialize)
            result = Database.addCombine(this, MODE_PRIVATE, combine);
        else {
            if (initializeName.equals(combine.getName())) {
                Database.updateCombine(this, MODE_PRIVATE, combine);
                result = true;
            } else {
                Database.removeCombine(this, MODE_PRIVATE, initializeName);
                result = Database.addCombine(this, MODE_PRIVATE, combine);
            }
        }
        if (!result) {
            Tools.showSnackBar(getString(R.string.this_combine_name_already_exists), parentLayout, this, Snackbar.LENGTH_SHORT);
            return;
        }
        Intent data = new Intent();
        data.putExtra("combine", combine);
        if (index != -1) {
            data.putExtra("index", index);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    public void shareCombine(View view) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        Bitmap image = combine.createMergedImage(this);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), image, "Title", null);
        System.out.println(path);
        Uri imageUri = Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, "Here is text");
        startActivity(Intent.createChooser(share, "Share via"));
    }
}