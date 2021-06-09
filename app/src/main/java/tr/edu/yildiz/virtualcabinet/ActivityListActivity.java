package tr.edu.yildiz.virtualcabinet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.adapter.ActivityRecyclerViewAdapter;
import tr.edu.yildiz.virtualcabinet.models.ActivityModel;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;

public class ActivityListActivity extends AppCompatActivity {
    ActivityRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ActivityModel> activityModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_activity);
        setTitle(getString(R.string.activities));

        activityModels = Database.getActivities(this, MODE_PRIVATE);
        if (activityModels == null)
            activityModels = new ArrayList<>();
        ConstraintLayout parentLayout = findViewById(R.id.activityListConstraintLayout);
        RecyclerView recyclerView = findViewById(R.id.activityListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new ActivityRecyclerViewAdapter(activityModels, parentLayout);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            ActivityRecyclerViewAdapter.RowHolder rowHolder = (ActivityRecyclerViewAdapter.RowHolder) viewHolder;
            if (direction == ItemTouchHelper.RIGHT) {
                rowHolder.setDeleteVisibility(View.INVISIBLE);
            } else if (direction == ItemTouchHelper.LEFT) {
                rowHolder.setDeleteVisibility(View.VISIBLE);
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Bitmap icon;
            Paint p = new Paint();
            ActivityRecyclerViewAdapter.RowHolder rowHolder = (ActivityRecyclerViewAdapter.RowHolder) viewHolder;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !rowHolder.isDeleteVisible()) {
                View itemView = viewHolder.itemView;
                if (dX < 0) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_button);
                    float width = icon.getWidth();
                    float mid = (float) (itemView.getBottom() - itemView.getTop()) / 2 + itemView.getTop();
                    p.setColor(getColor(R.color.red));
                    RectF background = new RectF((float) itemView.getRight() + Math.max(-8 * width / 4, dX / 6), (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    RectF icon_dest = new RectF((float) itemView.getRight() - width / 8 + Math.max(-width, dX / 6), mid - width / 2, (float) itemView.getRight() + width - width / 8 + Math.max(-width, dX / 6), mid + width / 2);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX / 6, dY, actionState, isCurrentlyActive);
        }
    };

    public void addActivity(View view) {
        Intent intent = new Intent(ActivityListActivity.this, AddActivityActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ActivityModel newActivity = data.getParcelableExtra("activity");
            boolean editing = data.getBooleanExtra("editing", false);
            if (editing) {
                int index = data.getIntExtra("index", -1);
                activityModels.set(index, newActivity);
            } else {
                activityModels.add(newActivity);
            }
            recyclerViewAdapter.notifyDataSetChanged();
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Combine combine = data.getParcelableExtra("combine");
            int index = data.getIntExtra("index", -1);
            activityModels.get(index).setCombineName(combine.getName());
            Database.updateActivityCombine(this, MODE_PRIVATE, activityModels.get(index).getName(), combine.getName());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}