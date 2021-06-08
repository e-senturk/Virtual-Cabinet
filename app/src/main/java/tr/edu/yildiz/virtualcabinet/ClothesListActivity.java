package tr.edu.yildiz.virtualcabinet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.adapter.ClothesRecyclerViewAdapter;
import tr.edu.yildiz.virtualcabinet.models.Clothes;

public class ClothesListActivity extends AppCompatActivity {
    ArrayList<Clothes> clothes;
    ClothesRecyclerViewAdapter recyclerViewAdapter;
    FloatingActionButton addButton;
    Intent sent;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_list);
        setTitle(R.string.clothes);
        addButton = findViewById(R.id.addClothesFloatingActionButton);

        Intent intent = getIntent();
        clothes = intent.getParcelableArrayListExtra("clothes_list");
        boolean clickable = intent.getBooleanExtra("clickable",false);
        boolean removable = intent.getBooleanExtra("removable",false);
        boolean addable = intent.getBooleanExtra("addable",false);
        String drawerName = intent.getStringExtra("drawer_name");
        if (!addable)
            addButton.setVisibility(View.INVISIBLE);
        int drawerIndex = intent.getIntExtra("drawer_index",-1);
        sent = new Intent();
        sent.putExtra("drawer_index",drawerIndex);
        RecyclerView recyclerView = findViewById(R.id.clothesListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConstraintLayout parentLayout = findViewById(R.id.clothesListConstraintLayout);
        recyclerViewAdapter = new ClothesRecyclerViewAdapter(clothes,drawerName,clickable,removable,sent,parentLayout);
        if(removable)
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void addClothes(View view){
        Intent intent = new Intent(ClothesListActivity.this, AddClothesActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Clothes newClothes = data.getParcelableExtra("new_clothes");
            clothes.add(newClothes);
            recyclerViewAdapter.notifyDataSetChanged();
            sent.putExtra("clothes_list",clothes);
            setResult(RESULT_OK,sent);
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Clothes newClothes = data.getParcelableExtra("new_clothes");
            int index = data.getIntExtra("index",-1);
            clothes.set(index,newClothes);
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
            ClothesRecyclerViewAdapter.RowHolder rowHolder = (ClothesRecyclerViewAdapter.RowHolder) viewHolder;
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
            ClothesRecyclerViewAdapter.RowHolder rowHolder = (ClothesRecyclerViewAdapter.RowHolder) viewHolder;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && !rowHolder.isDeleteVisible()) {
                View itemView = viewHolder.itemView;
                if (dX < 0) {
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_button);
                    float width = icon.getWidth();
                    System.out.println(width);
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