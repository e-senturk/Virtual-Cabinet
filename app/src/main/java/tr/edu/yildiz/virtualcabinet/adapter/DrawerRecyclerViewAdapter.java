package tr.edu.yildiz.virtualcabinet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.ClothesListActivity;
import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.Drawer;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

import static android.content.Context.MODE_PRIVATE;

public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.RowHolder> {
    private final ArrayList<Drawer> drawers;
    private final ConstraintLayout parentLayout;

    public DrawerRecyclerViewAdapter(ArrayList<Drawer> drawers,ConstraintLayout parentLayout) {
        this.drawers = drawers;
        this.parentLayout = parentLayout;
    }


    @NonNull
    @Override
    public DrawerRecyclerViewAdapter.RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view =layoutInflater.inflate(R.layout.adapter_drawer_recycler, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DrawerRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(drawers,position,this);
    }

    @Override
    public int getItemCount() {
        return drawers.size();
    }
    public static class RowHolder extends RecyclerView.ViewHolder {
        TextView drawerNameText;
        TextView drawerItemCountText;
        ImageView drawerEditImageButton;
        ImageButton drawerDeleteImageButton;
        ConstraintLayout drawerLinearLayout;
        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void bind(ArrayList<Drawer> drawers, Integer position, DrawerRecyclerViewAdapter adapter) {
            drawerNameText = itemView.findViewById(R.id.drawerNameText);
            drawerItemCountText = itemView.findViewById(R.id.drawerItemCount);
            drawerEditImageButton = itemView.findViewById(R.id.drawerRecyclerEditButton);
            drawerDeleteImageButton = itemView.findViewById(R.id.recyclerDrawerDeleteButton);
            drawerLinearLayout = itemView.findViewById(R.id.drawerListRecyclerViewLayout);
            Tools.setMarginLeft(drawerLinearLayout,10);
            drawerEditImageButton.setBackgroundColor(Color.parseColor(drawers.get(position).getColor()));
            drawerNameText.setText(drawers.get(position).getName());
            drawerNameText.setOnFocusChangeListener((v, hasFocus) -> {
                if(!hasFocus){
                    String newName = drawerNameText.getText().toString();
                    Database.updateDrawerName(itemView.getContext(),MODE_PRIVATE,newName,drawers.get(position).getName());
                    drawers.get(position).setName(newName);
                    Tools.showSnackBar(String.format(itemView.getContext().getString(R.string.updated),drawers.get(position).getName()),
                            adapter.parentLayout,itemView.getContext(), Snackbar.LENGTH_SHORT);
                }
            });
            drawerLinearLayout.setOnClickListener(v -> drawerNameText.clearFocus());
            drawerItemCountText.setText(String.format(itemView.getContext().getString(R.string.clothes_count), drawers.get(position).getClothesList().size()));
            drawerEditImageButton.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ClothesListActivity.class);
                intent.putExtra("clothes_list",drawers.get(position).getClothesList());
                intent.putExtra("removable",true);
                intent.putExtra("addable",true);
                intent.putExtra("drawer_index",position);
                intent.putExtra("drawer_name",drawers.get(position).getName());
                ((Activity)itemView.getContext()).startActivityForResult(intent,7);
            });
            drawerDeleteImageButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle(itemView.getContext().getString(R.string.alert));
                builder.setMessage(itemView.getContext().getString(R.string.are_you_sure_drawer_delete));
                builder.setNegativeButton(itemView.getContext().getString(R.string.no), null);
                builder.setPositiveButton(itemView.getContext().getString(R.string.yes), (dialogInterface, i) -> {
                    drawerDeleteImageButton.setVisibility(View.INVISIBLE);
                    Drawer goner = drawers.get(position);
                    Database.removeDrawer(itemView.getContext(),MODE_PRIVATE,goner.getName());
                    Tools.showSnackBar(String.format(itemView.getContext().getString(R.string.removed),drawers.get(position).getName()),
                            adapter.parentLayout,itemView.getContext(), Snackbar.LENGTH_SHORT);
                    drawers.remove(drawers.get(position));
                    adapter.notifyDataSetChanged();
                });
                builder.show();
            });


        }
        public boolean isDeleteVisible(){
            return drawerDeleteImageButton.getVisibility() == View.VISIBLE;
        }

        public void setDeleteVisibility(int visibility){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            drawerDeleteImageButton.setVisibility(visibility);
            drawerDeleteImageButton.setMinimumHeight(drawerLinearLayout.getHeight());
            if(visibility == View.VISIBLE){
                Tools.setMarginLeft(drawerLinearLayout,-width/8);
            }
            else{
                Tools.setMarginLeft(drawerLinearLayout,10);
            }
        }

        public void lostFocusName(){
            drawerNameText.clearFocus();
        }

    }

}
