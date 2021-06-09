package tr.edu.yildiz.virtualcabinet.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.AddActivityActivity;
import tr.edu.yildiz.virtualcabinet.CombineActivity;
import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.models.ActivityModel;
import tr.edu.yildiz.virtualcabinet.models.Combine;
import tr.edu.yildiz.virtualcabinet.service.Database;
import tr.edu.yildiz.virtualcabinet.service.Tools;

import static android.content.Context.MODE_PRIVATE;

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<ActivityRecyclerViewAdapter.RowHolder> {

    ArrayList<ActivityModel> activities;
    ConstraintLayout parentLayout;

    public ActivityRecyclerViewAdapter(ArrayList<ActivityModel> activities, ConstraintLayout parentLayout) {
        this.activities = activities;
        this.parentLayout = parentLayout;
    }

    @NonNull
    @NotNull
    @Override


    public ActivityRecyclerViewAdapter.RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_activities_list_recycler, parent, false);
        return new ActivityRecyclerViewAdapter.RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ActivityRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(activities, position, this);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class RowHolder extends RecyclerView.ViewHolder {
        private final int[] colors = new int[]{
                itemView.getContext().getColor(R.color.blue),
                itemView.getContext().getColor(R.color.orange),
                itemView.getContext().getColor(R.color.dark_green),
                itemView.getContext().getColor(R.color.yellow),
                itemView.getContext().getColor(R.color.medium_green),
                itemView.getContext().getColor(R.color.violet),
                itemView.getContext().getColor(R.color.vivid_green)
        };
        TextView activityNameField;
        TextView activityTypeField;
        TextView activityDateField;
        TextView activityLocationField;
        ImageButton activityCombineButton;
        ImageButton deleteButton;
        ConstraintLayout constraintLayout;


        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }


        public void bind(ArrayList<ActivityModel> activities, Integer position, ActivityRecyclerViewAdapter adapter) {
            activityNameField = itemView.findViewById(R.id.activityRecyclerNameField);
            activityTypeField = itemView.findViewById(R.id.activityRecyclerTypeField);
            activityDateField = itemView.findViewById(R.id.activityRecyclerDateField);
            activityLocationField = itemView.findViewById(R.id.activityRecyclerLocationField);
            activityCombineButton = itemView.findViewById(R.id.activityRecyclerCombineButton);
            deleteButton = itemView.findViewById(R.id.recyclerActivitiesDeleteButton);
            deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle(itemView.getContext().getString(R.string.alert));
                builder.setMessage(itemView.getContext().getString(R.string.are_you_sure_activity_delete));
                builder.setNegativeButton(itemView.getContext().getText(R.string.no), null);
                builder.setPositiveButton(itemView.getContext().getText(R.string.yes), (dialogInterface, i) -> {
                    deleteButton.setVisibility(View.INVISIBLE);
                    ActivityModel goner = activities.get(position);
                    Database.removeActivity(itemView.getContext(), MODE_PRIVATE, goner.getName());
                    Tools.showSnackBar(String.format(itemView.getContext().getString(R.string.removed), activities.get(position).getName()),
                            adapter.parentLayout, itemView.getContext(), Snackbar.LENGTH_SHORT);
                    activities.remove(activities.get(position));
                    adapter.notifyDataSetChanged();
                });
                builder.show();
            });
            activityCombineButton.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), CombineActivity.class);
                Combine combine = Database.getSingleCombine(itemView.getContext(), MODE_PRIVATE, activities.get(position).getCombineName());
                if (combine != null) {
                    intent.putExtra("combine", combine);
                    intent.putExtra("initialize", true);
                    intent.putExtra("index", position);
                    ((Activity) itemView.getContext()).startActivityForResult(intent, 2);
                }
            });
            constraintLayout = itemView.findViewById(R.id.activityRecyclerConstraintLayout);

            activityNameField.setText(activities.get(position).getName());
            activityTypeField.setText(activities.get(position).getType());
            activityDateField.setText(activities.get(position).getDate());
            activityLocationField.setText(activities.get(position).getAddress());

            constraintLayout.setBackgroundColor(colors[position % colors.length]);
            constraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AddActivityActivity.class);
                intent.putExtra("initialize", true);
                intent.putExtra("activity", activities.get(position));
                intent.putExtra("index", position);
                ((Activity) itemView.getContext()).startActivityForResult(intent, 1);
            });
        }

        public boolean isDeleteVisible() {
            return deleteButton.getVisibility() == View.VISIBLE;
        }

        public void setDeleteVisibility(int visibility) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            deleteButton.setVisibility(visibility);
            deleteButton.setMinimumHeight(constraintLayout.getHeight());
            if (visibility == View.VISIBLE) {
                Tools.setMarginLeft(constraintLayout, -width / 8);
            } else {
                Tools.setMarginLeft(constraintLayout, 0);
            }
        }
    }
}
