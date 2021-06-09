package tr.edu.yildiz.virtualcabinet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.FireBaseService;
import tr.edu.yildiz.virtualcabinet.service.PowerConnectionReceiver;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class MenuActivity extends AppCompatActivity {
    ImageView drawerImage;
    ImageView cabinImage;
    ImageView activityImage;
    PowerConnectionReceiver receiver;
    ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        layout = findViewById(R.id.menuActivityContraintLayout);
        FireBaseService.signIn(this);
        int supportBarSize = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = (displayMetrics.heightPixels - supportBarSize) / 3;
        drawerImage = findViewById(R.id.menuDrawerImage);
        drawerImage.setMaxHeight(screenHeight);
        drawerImage.setMaxWidth(screenWidth);
        drawerImage.setImageBitmap(Tools.generateMenuImage(this, R.drawable.drawers, supportBarSize));

        cabinImage = findViewById(R.id.menuCabinImage);
        cabinImage.setImageBitmap(Tools.generateMenuImage(this, R.drawable.dressing_room, supportBarSize));
        cabinImage.setMaxHeight(screenHeight);
        cabinImage.setMaxWidth(screenWidth);
        activityImage = findViewById(R.id.menuActivityImage);
        activityImage.setImageBitmap(Tools.generateMenuImage(this, R.drawable.activity_menu, supportBarSize));
        activityImage.setMaxHeight(screenHeight);
        activityImage.setMaxWidth(screenWidth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new PowerConnectionReceiver(layout);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.backupMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getString(R.string.are_you_sure_backup));
            builder.setNegativeButton(getString(R.string.no), null);
            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                FireBaseService.update(this,MODE_PRIVATE);
                Tools.showSnackBar(getString(R.string.backup_started), layout, this, Snackbar.LENGTH_SHORT);
            });
            builder.show();

        } else if (item.getItemId() == R.id.restoreMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getString(R.string.are_you_sure_restore));
            builder.setNegativeButton(getString(R.string.no), null);
            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                Database.clearAll(this, MODE_PRIVATE);
                FireBaseService.restore(this, MODE_PRIVATE);
                Tools.showSnackBar(getString(R.string.restore_started), layout, this, Snackbar.LENGTH_SHORT);
            });
            builder.show();
        } else if (item.getItemId() == R.id.deleteMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert));
            builder.setMessage(getString(R.string.are_you_sure_clear));
            builder.setNegativeButton(getString(R.string.no), null);
            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                Database.clearAll(this, MODE_PRIVATE);
                Tools.showSnackBar(getString(R.string.cleaning_started), layout, this, Snackbar.LENGTH_SHORT);
            });
            builder.show();
            Database.clearAll(this, MODE_PRIVATE);
        }
        return true;
    }

}