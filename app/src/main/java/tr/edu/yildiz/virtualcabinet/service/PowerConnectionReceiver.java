package tr.edu.yildiz.virtualcabinet.service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import tr.edu.yildiz.virtualcabinet.R;

import static android.content.Context.MODE_PRIVATE;

public class PowerConnectionReceiver extends BroadcastReceiver {
    ConstraintLayout layout;

    public PowerConnectionReceiver(ConstraintLayout layout) {
        this.layout = layout;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            View.OnClickListener clickFunction = v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.alert));
                builder.setMessage(context.getString(R.string.are_you_sure_backup));
                builder.setNegativeButton(context.getString(R.string.no), null);
                builder.setPositiveButton(context.getString(R.string.yes), (dialogInterface, i) -> {
                    FireBaseService.clearFirebase();
                    FireBaseService.uploadDataBase(context, MODE_PRIVATE);
                    FireBaseService.uploadAllImages(context, MODE_PRIVATE);
                    Tools.showSnackBar(context.getString(R.string.backup_started), layout, context, Snackbar.LENGTH_SHORT);
                });
                builder.show();
            };
            Tools.showSnackBar(context.getString(R.string.charger_connected), layout, context, Snackbar.LENGTH_LONG, clickFunction, context.getString(R.string.start_backup));
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Tools.showSnackBar(context.getString(R.string.charger_disconnected), layout, context, Snackbar.LENGTH_LONG);
        }
    }


}