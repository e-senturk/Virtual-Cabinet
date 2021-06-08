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

import tr.edu.yildiz.virtualcabinet.adapter.CombineRecyclerViewAdapter;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;

public class CombineListActivity extends AppCompatActivity {
    ArrayList<Combine> combines;
    CombineRecyclerViewAdapter recyclerViewAdapter;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            CombineRecyclerViewAdapter.RowHolder rowHolder = (CombineRecyclerViewAdapter.RowHolder) viewHolder;
            if (direction == ItemTouchHelper.DOWN) {
                rowHolder.showDelete();
            } else if (direction == ItemTouchHelper.UP) {
                rowHolder.hideDelete();
            }
            recyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Bitmap icon;
            Paint p = new Paint();
            CombineRecyclerViewAdapter.RowHolder rowHolder = (CombineRecyclerViewAdapter.RowHolder) viewHolder;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !rowHolder.isDeleteVisible()) {
                View itemView = viewHolder.itemView;

                if (dY > 0) {
                    icon = BitmapFactory.decodeResource(recyclerView.getResources(), R.drawable.delete_button);
                    float width = icon.getWidth();
                    float mid = (float) (itemView.getRight() - itemView.getLeft()) / 2;
                    p.setColor(getColor(R.color.red));
                    RectF background = new RectF((float) itemView.getLeft() + 8, (float) itemView.getTop(), (float) itemView.getRight() - 8, Math.min(dY / 8, (float) (width * 1.32)));
                    c.drawRect(background, p);

                    RectF icon_dest = new RectF((float) itemView.getLeft() + mid - width / 2, (float) itemView.getTop() + Math.min(dY / 8, width) - width, (float) itemView.getLeft() + mid + width / 2, (float) itemView.getTop() + Math.min(dY / 8, width));
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY / 8, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_list);
        setTitle(R.string.combines);
        Intent intent = getIntent();
        boolean selectMode = intent.getBooleanExtra("select_mode", false);


        combines = Database.getCombines(this, MODE_PRIVATE);
        RecyclerView recyclerView = findViewById(R.id.combineRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        if (combines == null)
            combines = new ArrayList<>();
        ConstraintLayout parentLayout = findViewById(R.id.combineListingConstraintLayout);
        recyclerViewAdapter = new CombineRecyclerViewAdapter(combines, selectMode, parentLayout);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                boolean initialize = data.getBooleanExtra("initialize", false);
                int index = data.getIntExtra("index", -1);
                Combine combine = data.getParcelableExtra("combine");
                System.out.println(initialize);
                if (initialize) {
                    combines.set(index, combine);
                } else {
                    combines.add(combine);
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createCombine(View view) {
        Intent intent = new Intent(CombineListActivity.this, AddCombineActivity.class);
        startActivityForResult(intent, 1);
    }
}