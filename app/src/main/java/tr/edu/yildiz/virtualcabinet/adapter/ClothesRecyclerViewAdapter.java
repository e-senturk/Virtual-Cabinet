package tr.edu.yildiz.virtualcabinet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import tr.edu.yildiz.virtualcabinet.AddClothesActivity;
import tr.edu.yildiz.virtualcabinet.BuildConfig;
import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.Clothes;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ClothesRecyclerViewAdapter extends RecyclerView.Adapter<ClothesRecyclerViewAdapter.RowHolder> {
    ArrayList<Clothes> clothes;
    boolean clickable;
    boolean removable;
    Intent data;
    String drawerName;
    ConstraintLayout parentLayout;

    public ClothesRecyclerViewAdapter(ArrayList<Clothes> clothes, String drawerName, boolean clickable, boolean removable, Intent data, ConstraintLayout parentLayout) {
        this.clothes = clothes;
        this.drawerName = drawerName;
        this.clickable = clickable;
        this.removable = removable;
        this.data = data;
        this.parentLayout = parentLayout;
    }

    @NonNull
    @NotNull
    @Override
    public ClothesRecyclerViewAdapter.RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_clothes_list_recycler, parent, false);
        return new ClothesRecyclerViewAdapter.RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ClothesRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(clothes, drawerName, position, this, clickable, removable, data);
    }

    @Override
    public int getItemCount() {
        return clothes.size();
    }

    public static class RowHolder extends RecyclerView.ViewHolder {
        ImageView clothesImage;
        ImageButton deleteButton;
        TextView wearingLocation;
        TextView type;
        TextView color;
        TextView purchaseDate;
        TextView pattern;
        TextView price;
        ConstraintLayout parentLayout;
        ConstraintLayout layout;

        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void bind(ArrayList<Clothes> clothes, String drawerName, Integer position, ClothesRecyclerViewAdapter adapter, boolean clickable, boolean removable, Intent data2) {
            parentLayout = itemView.findViewById(R.id.clothesListRecyclerView);
            layout = itemView.findViewById(R.id.clothesRecyclerConstraintLayout);
            clothesImage = itemView.findViewById(R.id.clothesListImageView);
            deleteButton = itemView.findViewById(R.id.recyclerClothesDeleteButton);
            wearingLocation = itemView.findViewById(R.id.clothesListWearingLocation);
            type = itemView.findViewById(R.id.clothesListType);
            color = itemView.findViewById(R.id.clothesListColor);
            purchaseDate = itemView.findViewById(R.id.clothesListPurchaseDate);
            pattern = itemView.findViewById(R.id.clothesListPattern);
            price = itemView.findViewById(R.id.clothesListPrice);
            if (!clothes.get(position).getAttachmentPath().equals("")) {
                File file = new File(clothes.get(position).getAttachmentPath());
                Uri imageUri = FileProvider.getUriForFile(itemView.getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                Tools.initializeImageView(itemView.getContext(), imageUri, clothesImage);
            }
            if (clickable) {
                layout.setOnClickListener(v -> {
                    Intent data = new Intent();
                    data.putExtra("clothes_path", clothes.get(position).getAttachmentPath());
                    ((Activity) itemView.getContext()).setResult(RESULT_OK, data);
                    ((Activity) itemView.getContext()).finish();
                });
            } else {
                layout.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), AddClothesActivity.class);
                    intent.putExtra("index", position);
                    intent.putExtra("initialize", true);
                    intent.putExtra("clothes", clothes.get(position));
                    intent.putExtra("drawer_name", drawerName);
                    ((Activity) itemView.getContext()).startActivityForResult(intent, 2);
                });
            }
            if (removable) {
                deleteButton.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle(itemView.getContext().getString(R.string.alert));
                    builder.setMessage(itemView.getContext().getString(R.string.are_you_sure_clothes_delete));
                    builder.setNegativeButton(itemView.getContext().getText(R.string.no), null);
                    builder.setPositiveButton(itemView.getContext().getText(R.string.yes), (dialogInterface, i) -> {
                        deleteButton.setVisibility(View.INVISIBLE);
                        Clothes goner = clothes.get(position);
                        Database.removeClothes(itemView.getContext(), MODE_PRIVATE, goner.getAttachmentPath());
                        Tools.showSnackBar(String.format(itemView.getContext().getString(R.string.removed), itemView.getContext().getString(R.string.clothes)),
                                adapter.parentLayout, itemView.getContext(), Snackbar.LENGTH_SHORT);
                        clothes.remove(clothes.get(position));
                        data2.putExtra("clothes_list", clothes);
                        Uri uri = Tools.getUriFromStringPath(goner.getAttachmentPath(), itemView.getContext());
                        itemView.getContext().getContentResolver().delete(uri, null, null);
                        ((Activity) itemView.getContext()).setResult(RESULT_OK, data2);
                        adapter.notifyDataSetChanged();
                    });
                    builder.show();

                });
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
            }
            wearingLocation.setText(clothes.get(position).getWearingLocation());
            type.setText(clothes.get(position).getType());
            color.setBackgroundColor(Color.parseColor(clothes.get(position).getColor()));
            purchaseDate.setText(clothes.get(position).getPurchaseDate());
            pattern.setText(clothes.get(position).getClothesPattern());
            String priceValue = clothes.get(position).getPrice() + " " + Currency.getInstance(Locale.getDefault()).getSymbol();
            price.setText(priceValue);
        }

        public boolean isDeleteVisible() {
            return deleteButton.getVisibility() == View.VISIBLE;
        }

        public void setDeleteVisibility(int visibility) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            deleteButton.setVisibility(visibility);
            deleteButton.setMinimumHeight(layout.getHeight());
            if (visibility == View.VISIBLE) {
                Tools.setMarginLeft(layout, -width / 8);
            } else {
                Tools.setMarginLeft(layout, 0);
            }
        }

    }
}
