package tr.edu.yildiz.virtualcabinet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

import tr.edu.yildiz.virtualcabinet.AddCombineActivity;
import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class CombineRecyclerViewAdapter extends RecyclerView.Adapter<CombineRecyclerViewAdapter.RowHolder> {
    @NonNull
    @NotNull
    ArrayList<Combine> combines;
    boolean selectMode;
    ConstraintLayout parentLayout;

    public CombineRecyclerViewAdapter(@NotNull ArrayList<Combine> combines, boolean selectMode, ConstraintLayout parentLayout) {
        this.combines = combines;
        this.selectMode = selectMode;
        this.parentLayout = parentLayout;
    }

    @Override
    public CombineRecyclerViewAdapter.@NotNull RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_combine_list_recycler, parent, false);
        return new CombineRecyclerViewAdapter.RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CombineRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(combines, position, selectMode, this, parentLayout);
    }

    @Override
    public int getItemCount() {
        return combines.size();
    }

    public static class RowHolder extends RecyclerView.ViewHolder {
        TextView combineNameText;
        ImageView combineTopHeadImage;
        ImageView combineFaceImage;
        ImageView combineUpperBodyImage;
        ImageView combineLowerBodyImage;
        ImageView combineFeetImage;
        ConstraintLayout constraintLayout;
        ImageButton deleteButton;

        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void bind(ArrayList<Combine> combines, Integer position, Boolean selectMode, CombineRecyclerViewAdapter adapter, ConstraintLayout parentLayout) {
            combineNameText = itemView.findViewById(R.id.combineRecyclerTextView);
            combineTopHeadImage = itemView.findViewById(R.id.combineRecyclerTopHeadImage);
            combineFaceImage = itemView.findViewById(R.id.combineRecyclerFaceImage);
            combineUpperBodyImage = itemView.findViewById(R.id.combineRecyclerUpperBodyImage);
            combineLowerBodyImage = itemView.findViewById(R.id.combineRecyclerLowerBodyImage);
            combineFeetImage = itemView.findViewById(R.id.combineRecyclerFeetImage);
            constraintLayout = itemView.findViewById(R.id.combineRecyclerConstraintLayout);
            deleteButton = itemView.findViewById(R.id.combineRecyclerDeleteButton);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int size = (int) (Math.max(height, width) / 6.5);
            combineNameText.setText(combines.get(position).getName());
            combineTopHeadImage.setImageBitmap(Tools.makeImageSmaller(combines.get(position).getTopHeadImage(itemView.getContext()), size));
            combineFaceImage.setImageBitmap(Tools.makeImageSmaller(combines.get(position).getFaceImage(itemView.getContext()), size));
            combineUpperBodyImage.setImageBitmap(Tools.makeImageSmaller(combines.get(position).getUpperBodyImage(itemView.getContext()), size));
            combineLowerBodyImage.setImageBitmap(Tools.makeImageSmaller(combines.get(position).getLowerBodyImage(itemView.getContext()), size));
            combineFeetImage.setImageBitmap(Tools.makeImageSmaller(combines.get(position).getFeetImage(itemView.getContext()), size));
            deleteButton.setMinimumWidth(combineFeetImage.getWidth());

            constraintLayout.setOnClickListener(v -> {
                if (selectMode) {
                    Intent data = new Intent();
                    data.putExtra("combine", combines.get(position));
                    ((Activity) itemView.getContext()).setResult(RESULT_OK, data);
                    ((Activity) itemView.getContext()).finish();
                } else {
                    Intent intent = new Intent(itemView.getContext(), AddCombineActivity.class);
                    intent.putExtra("combine", combines.get(position));
                    intent.putExtra("index", position);
                    intent.putExtra("initialize", true);
                    ((Activity) itemView.getContext()).startActivityForResult(intent, 1);
                }

            });
            deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle(itemView.getContext().getString(R.string.alert));
                builder.setMessage(itemView.getContext().getString(R.string.are_you_sure_combine_delete));
                builder.setNegativeButton(itemView.getContext().getString(R.string.no), null);
                builder.setPositiveButton(itemView.getContext().getString(R.string.yes), (dialogInterface, i) -> {
                    deleteButton.setVisibility(View.INVISIBLE);
                    Combine goner = combines.get(position);
                    Database.removeCombine(itemView.getContext(), MODE_PRIVATE, goner.getName());
                    Tools.showSnackBar(String.format(itemView.getContext().getString(R.string.removed), combines.get(position).getName()),
                            parentLayout, itemView.getContext(), Snackbar.LENGTH_SHORT);
                    combines.remove(combines.get(position));
                    adapter.notifyDataSetChanged();
                });
                builder.show();

            });
        }

        public void showDelete() {
            deleteButton.setVisibility(View.VISIBLE);
        }

        public void hideDelete() {
            deleteButton.setVisibility(View.GONE);
        }

        public boolean isDeleteVisible() {
            return deleteButton.getVisibility() == View.VISIBLE;
        }
    }
}
