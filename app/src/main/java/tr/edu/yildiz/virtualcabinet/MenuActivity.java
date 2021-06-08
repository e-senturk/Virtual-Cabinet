package tr.edu.yildiz.virtualcabinet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    ImageView drawerImage;
    ImageView cabinImage;
    ImageView activityImage;

    public static Bitmap generateMenuImage(Activity context, int id, int topBarSize) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        float width = bmp.getWidth();
        float height = bmp.getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = (displayMetrics.heightPixels - topBarSize) / 3;
        int cropWidth;
        int cropHeight;
        int x = 0;
        int y = 0;
        if (width / screenWidth > height / screenHeight) {
            cropHeight = (int) height;
            cropWidth = (int) height * screenWidth / screenHeight;
        } else {
            cropWidth = (int) width;
            cropHeight = (int) width * screenHeight / screenWidth;
        }
        return Bitmap.createBitmap(bmp, x, y, cropWidth, cropHeight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        int supportBarSize = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = (displayMetrics.heightPixels - supportBarSize) / 3;
        drawerImage = findViewById(R.id.menuDrawerImage);
        drawerImage.setMaxHeight(screenHeight);
        drawerImage.setMaxWidth(screenWidth);
        drawerImage.setImageBitmap(generateMenuImage(this, R.drawable.drawers, supportBarSize));

        cabinImage = findViewById(R.id.menuCabinImage);
        cabinImage.setImageBitmap(generateMenuImage(this, R.drawable.dressing_room, supportBarSize));
        cabinImage.setMaxHeight(screenHeight);
        cabinImage.setMaxWidth(screenWidth);
        activityImage = findViewById(R.id.menuActivityImage);
        activityImage.setImageBitmap(generateMenuImage(this, R.drawable.activity_menu, supportBarSize));
        activityImage.setMaxHeight(screenHeight);
        activityImage.setMaxWidth(screenWidth);

    }

    public void createDrawer(View view) {
        Intent intent = new Intent(MenuActivity.this, DrawerListActivity.class);
        startActivity(intent);
    }

    public void openCabinRoom(View view) {
        Intent intent = new Intent(MenuActivity.this, CombineListActivity.class);
        startActivity(intent);
    }

    public void createActivity(View view) {
        Intent intent = new Intent(MenuActivity.this, ActivityListActivity.class);
        startActivity(intent);
    }
}