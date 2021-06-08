package tr.edu.yildiz.virtualcabinet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.adapter.DrawerRecyclerViewAdapter;
import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.models.Drawer;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class DrawerListActivity extends AppCompatActivity {
    ArrayList<Drawer> drawers;

    DrawerRecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_list);
        setTitle(R.string.drawers);
        drawers = Database.getDrawers(this,MODE_PRIVATE);
        if(drawers == null){
            drawers = new ArrayList<>();
        }
        RecyclerView recyclerView = findViewById(R.id.drawerListRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new DrawerRecyclerViewAdapter(drawers,findViewById(R.id.drawerListConstraintLayout));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void addDrawer(View view){
        Intent intent = new Intent(DrawerListActivity.this, AddDrawerActivity.class);
        intent.putExtra("drawers",drawers);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK  && data != null) {
            Drawer new_drawer = (Drawer)data.getParcelableExtra("new_drawer");
            drawers.add(new_drawer);
            recyclerViewAdapter.notifyDataSetChanged();
            Tools.showSnackBar(new_drawer.getName() +" "+ getString(R.string.added_successfully),findViewById(R.id.drawerListConstraintLayout),this, Snackbar.LENGTH_SHORT);
        }
        if (requestCode == 7 && resultCode == RESULT_OK && data!=null) {
            ArrayList<Clothes> clothes = data.getParcelableArrayListExtra("clothes_list");
            int index = data.getIntExtra("drawer_index",-1);
            drawers.get(index).setClothesList(clothes);
            Database.updateDrawer(this,MODE_PRIVATE,drawers.get(index).getName(),clothes);
            recyclerViewAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            DrawerRecyclerViewAdapter.RowHolder rowHolder = (DrawerRecyclerViewAdapter.RowHolder) viewHolder;
            rowHolder.lostFocusName();
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
            DrawerRecyclerViewAdapter.RowHolder rowHolder = (DrawerRecyclerViewAdapter.RowHolder) viewHolder;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !rowHolder.isDeleteVisible()) {
                View itemView = viewHolder.itemView;
                if (dX < 0) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_button);
                    float width = icon.getWidth();
                    float mid = (float)(itemView.getBottom() - itemView.getTop())/2 + itemView.getTop();
                    p.setColor(getColor(R.color.red));
                    RectF background = new RectF((float) itemView.getRight() + Math.max(-8*width/4,dX/6), (float) ((float) itemView.getTop()+width/6.1), (float) itemView.getRight(), (float) ((float) itemView.getBottom()-width/6.1));
                    c.drawRect(background, p);
                    RectF icon_dest = new RectF((float) itemView.getRight() -width/8+Math.max(-width,dX/6), mid - width/2, (float) itemView.getRight()+width-width/8+Math.max(-width,dX/6), mid + width/2);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX / 6, dY, actionState, isCurrentlyActive);
        }
    };
}