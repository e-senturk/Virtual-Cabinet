package tr.edu.yildiz.virtualcabinet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tr.edu.yildiz.virtualcabinet.models.ActivityModel;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;

public class AddActivityActivity extends AppCompatActivity {
    DatePickerDialog datePickerDialog;
    TextView activityDateText;
    Combine combine;
    TextView activityNameText;
    TextView activityTypeText;
    TextView combineNameText;
    TextView activityLocationText;
    Address address;
    boolean initialize;
    ActivityModel oldActivity;
    int oldActivityPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);
        setTitle(getString(R.string.add_new_activity));
        activityNameText = findViewById(R.id.activityNameText);
        activityDateText = findViewById(R.id.activityDateText);
        combineNameText = findViewById(R.id.activityCombineField);
        activityLocationText = findViewById(R.id.activityLocationText);
        activityTypeText = findViewById(R.id.activityTypeField);
        address = null;
        combine = null;
        Intent intent = getIntent();
        initialize = intent.getBooleanExtra("initialize", false);
        oldActivityPosition = intent.getIntExtra("index",-1);
        if (initialize) {
            oldActivity = intent.getParcelableExtra("activity");
            activityNameText.setText(oldActivity.getName());
            activityDateText.setText(oldActivity.getDate());
            activityTypeText.setText(oldActivity.getType());
            activityLocationText.setText(oldActivity.getAddress());
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(oldActivity.getLatitude(), oldActivity.getLongitude(), 1);
                address = addresses.get(0);
            } catch (IOException e) {
                e.printStackTrace();
                address = null;
            }
            combine = Database.getSingleCombine(this, MODE_PRIVATE, oldActivity.getCombineName());
            combineNameText.setText(oldActivity.getCombineName());
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        activityDateText.setText(String.format(getString(R.string.date), day, (month + 1), year));
    }

    public void pickDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int monthCurrent = calendar.get(Calendar.DAY_OF_WEEK);
        int yearCurrent = calendar.get(Calendar.YEAR);
        System.out.println(yearCurrent);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                activityDateText.setText(String.format(getString(R.string.date), dayOfMonth, (month + 1), year));
            }
        }, yearCurrent, monthCurrent, dayCurrent);
        datePickerDialog.show();
    }

    public void pickCombine(View view) {
        Intent intent = new Intent(AddActivityActivity.this, CombineListActivity.class);
        intent.putExtra("select_mode", true);
        startActivityForResult(intent, 1);
    }

    public void pickLocation(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("address", address);
        startActivityForResult(intent, 2);
    }

    public void saveActivity(View view) {
        Intent data = new Intent();
        double latitude = 0.0;
        double longitude = 0.0;
        if (address != null) {
            latitude = address.getLatitude();
            longitude = address.getLongitude();
        }
        ActivityModel activityModel = new ActivityModel(activityNameText.getText().toString(),
                activityTypeText.getText().toString(), activityDateText.getText().toString(),
                latitude, longitude, activityLocationText.getText().toString(), combineNameText.getText().toString());
        if (initialize){
            Database.removeActivity(this, MODE_PRIVATE, oldActivity.getName());
            data.putExtra("index",oldActivityPosition);
            data.putExtra("editing",true);
        }
        Database.addActivity(this, MODE_PRIVATE, activityModel);
        data.putExtra("activity", activityModel);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            combine = data.getParcelableExtra("combine");
            combineNameText.setText(combine.getName());
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            address = data.getParcelableExtra("address");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                String line = address.getAddressLine(i);
                if (line != null) {
                    sb.append(line);
                }
            }
            activityLocationText.setText(sb.toString().replace(", Turkey", "").replace(", Türkiye", ""));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}