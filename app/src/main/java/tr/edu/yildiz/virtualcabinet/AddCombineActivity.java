package tr.edu.yildiz.virtualcabinet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class AddCombineActivity extends AppCompatActivity {
    String topHeadPath;
    String facePath;
    String upperBodyPath;
    String lowerBodyPath;
    String feetPath;
    Spinner topHeadSpinner;
    Spinner faceSpinner;
    Spinner upperBodySpinner;
    Spinner lowerBodySpinner;
    Spinner feetSpinner;
    ImageView topHeadImage;
    ImageView faceImage;
    ImageView upperBodyImage;
    ImageView lowerBodyImage;
    ImageView feetImage;
    ConstraintLayout parentLayout;
    Combine combine;
    boolean initialize;
    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_combine);
        setTitle(R.string.add_new_combine);

        Intent intent = getIntent();
        initialize = intent.getBooleanExtra("initialize",false);

        topHeadImage = findViewById(R.id.cabinTopHeadImage);
        faceImage = findViewById(R.id.cabinFaceImage);
        upperBodyImage = findViewById(R.id.cabinUpperBodyImage);
        lowerBodyImage = findViewById(R.id.cabinLowerBodyImage);
        feetImage = findViewById(R.id.cabinFeetImage);
        parentLayout = findViewById(R.id.cabinActivityConstraintLayout);
        if(initialize){
            index = intent.getIntExtra("index",-1);
            combine = intent.getParcelableExtra("combine");
            topHeadPath = combine.getTopHeadPath();
            facePath = combine.getFacePath();
            upperBodyPath = combine.getUpperBodyPath();
            lowerBodyPath = combine.getLowerBodyPath();
            feetPath = combine.getFeetPath();
            Tools.initializeImageView(combine.getTopHeadImage(this),topHeadImage);
            Tools.initializeImageView(combine.getFaceImage(this),faceImage);
            Tools.initializeImageView(combine.getUpperBodyImage(this),upperBodyImage);
            Tools.initializeImageView(combine.getLowerBodyImage(this),lowerBodyImage);
            Tools.initializeImageView(combine.getFeetImage(this),feetImage);
        }
        else {
            topHeadPath="";
            facePath="";
            upperBodyPath="";
            lowerBodyPath="";
            feetPath="";
        }
        ArrayList<String> drawerNames = Database.getDrawerNames(this,MODE_PRIVATE);
        if(drawerNames==null || drawerNames.size() == 0){
            finish();
        }
        topHeadSpinner = findViewById(R.id.spinnerCabinTopHead);
        topHeadSpinner.setAdapter(generateArrayAdapter(drawerNames));
        faceSpinner = findViewById(R.id.spinnerCabinFace);
        faceSpinner.setAdapter(generateArrayAdapter(drawerNames));
        upperBodySpinner = findViewById(R.id.spinnerCabinUpperBody);
        upperBodySpinner.setAdapter(generateArrayAdapter(drawerNames));
        lowerBodySpinner = findViewById(R.id.spinnerCabinLowerBody);
        lowerBodySpinner.setAdapter(generateArrayAdapter(drawerNames));
        feetSpinner = findViewById(R.id.spinnerCabinFeet);
        feetSpinner.setAdapter(generateArrayAdapter(drawerNames));
    }

    public ArrayAdapter<String> generateArrayAdapter(ArrayList<String> drawerNames){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drawerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }



    public void selectImage(View view) {
        int requestCode = 0;
        ArrayList<Clothes> clothes = new ArrayList<>();
        if (view.getId() == topHeadImage.getId()){
            clothes = Database.getClothes(this,MODE_PRIVATE,topHeadSpinner.getSelectedItem().toString(),getResources().getStringArray(R.array.wearing_place)[0]);
            requestCode = 1;
        }
        else if (view.getId() == faceImage.getId()){
            clothes = Database.getClothes(this,MODE_PRIVATE,faceSpinner.getSelectedItem().toString(),getResources().getStringArray(R.array.wearing_place)[1]);
            requestCode = 2;
        }
        else if (view.getId() == upperBodyImage.getId()){
            clothes = Database.getClothes(this,MODE_PRIVATE,upperBodySpinner.getSelectedItem().toString(),getResources().getStringArray(R.array.wearing_place)[2]);
            requestCode = 3;
        }
        else if (view.getId() == lowerBodyImage.getId()){
            clothes = Database.getClothes(this,MODE_PRIVATE,lowerBodySpinner.getSelectedItem().toString(),getResources().getStringArray(R.array.wearing_place)[3]);
            requestCode = 4;
        }
        else if (view.getId() == feetImage.getId()){
            clothes = Database.getClothes(this,MODE_PRIVATE,feetSpinner.getSelectedItem().toString(),getResources().getStringArray(R.array.wearing_place)[4]);
            requestCode = 5;
        }
        if(requestCode!=0){
            Intent intent = new Intent(AddCombineActivity.this, ClothesListActivity.class);
            intent.putExtra("clothes_list",clothes);
            intent.putExtra("clickable",true);
            intent.putExtra("addable",false);
            startActivityForResult(intent,requestCode);
        }
    }

    // sets user image in field
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == 6){
                Intent data2 = new Intent();
                Combine combine = data.getParcelableExtra("combine");
                data2.putExtra("initialize",initialize);
                data2.putExtra("combine",combine);
                data2.putExtra("index",index);
                setResult(RESULT_OK,data2);
                finish();
            }
            else {
                String attachmentPath = data.getStringExtra("clothes_path");
                Uri imageUri = Tools.getUriFromStringPath(attachmentPath,this);
                if (requestCode == 1) {
                    topHeadPath = attachmentPath;
                    Tools.initializeImageView(this, imageUri, topHeadImage);
                }
                else if(requestCode == 2) {
                    facePath = attachmentPath;
                    Tools.initializeImageView(this, imageUri, faceImage);
                }
                else if(requestCode == 3) {
                    upperBodyPath = attachmentPath;
                    Tools.initializeImageView(this, imageUri, upperBodyImage);
                }
                else if(requestCode == 4) {
                    lowerBodyPath = attachmentPath;
                    Tools.initializeImageView(this, imageUri, lowerBodyImage);
                }
                else if(requestCode == 5) {
                    feetPath = attachmentPath;
                    Tools.initializeImageView(this, imageUri, feetImage);
                }
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createCombine(View view){
        ArrayList<String> paths = new ArrayList<String>(){
            {
                add(topHeadPath);
                add(facePath);
                add(upperBodyPath);
                add(lowerBodyPath);
                add(feetPath);
            }
        };
        int i = 0;
        for(String path:paths){
            Uri uri = Tools.getUriFromStringPath(path,this);
            Bitmap bitmap = Tools.generateCombineBitmap(this,uri);
            if(bitmap==null)
                i+=1;
            else
                bitmap.recycle();
            if(i==4){
                Tools.showSnackBar(getString(R.string.you_must_select_at_least_2_items),parentLayout,this, Snackbar.LENGTH_SHORT);
                return;
            }
        }
        Intent intent = new Intent(AddCombineActivity.this, CombineActivity.class);
        intent.putExtra("combine",new Combine("",paths));
        if(initialize){
            intent.putExtra("initialize",true);
            intent.putExtra("combine_name",combine.getName());
        }
        startActivityForResult(intent,6);
    }

}