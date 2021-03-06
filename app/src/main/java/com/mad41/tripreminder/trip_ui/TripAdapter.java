package com.mad41.tripreminder.trip_ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad41.tripreminder.MainScreen;
import com.mad41.tripreminder.R;
import com.mad41.tripreminder.constants.Constants;
import com.mad41.tripreminder.room_database.MyRoomDataBase;
import com.mad41.tripreminder.room_database.trip.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<com.mad41.tripreminder.trip_ui.TripAdapter.ExampleViewHolder> {
    private static OnMenuClickListener listener;
    private static NoteReview noteReview;
    private static OnStartClickListener startListener;
    Trip currentItem;
    Animation fade_scale_animation;
    private List<Trip> tripModels = new ArrayList<>();

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        fade_scale_animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.fade_scale_animation);
        return evh;
    }

    public void setList(List<Trip> trips) {
        this.tripModels = trips;

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        currentItem = tripModels.get(position);
        String Status = null;
        if(currentItem.getStatus() == 0){
            Status = "Canceled";
        }
        else if(currentItem.getStatus() == 1){
            Status = "Completed";
        }
        else if(currentItem.getStatus() == -1){
            Status = "Missed";
        }else{
            Status = "Done";
        }
        holder.txt_date.setText(currentItem.getDate());
        holder.txt_time.setText(currentItem.getTime());
        holder.txt_start.setText(currentItem.getStartLoacation());
        holder.txt_end.setText(currentItem.getEndLoacation());
        holder.txt_state.setText(Status);
        holder.txt_place.setText(currentItem.getName());
        //get the id of the trip and add it to the card (viewHolder)
        holder.id = currentItem.getId();

        holder.card_trip.startAnimation(fade_scale_animation);

        Log.i("note", "the : " + currentItem.getEndLoacation());

    }

    @Override
    public int getItemCount() {
        return tripModels.size();
    }

    public void setOnItemClickListener(OnMenuClickListener listener) {
        this.listener = listener;

    }

    public void setOnNoteClickListener(NoteReview noteReview) {
        this.noteReview = noteReview;
    }

    public void setOnStartClickListener(OnStartClickListener startListener) {
        this.startListener = startListener;
    }

    public interface OnMenuClickListener {
        void onItemClick(View view, int id);
    }

    public interface NoteReview {
        void onNoteClick(View view, int id);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_date;
        public TextView txt_time;
        public TextView txt_place;
        public TextView txt_state;
        public TextView txt_start;
        public TextView txt_end;
        public int id;
        ImageView btn_note_review;
        ImageView btn_menu_card;
        private Button btn_startCard;
        CardView card_trip;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date2);
            txt_time = itemView.findViewById(R.id.txt_time2);
            txt_place = itemView.findViewById(R.id.txt_tripName2);
            txt_start = itemView.findViewById(R.id.txt_start2);
            txt_state = itemView.findViewById(R.id.txt_state);
            txt_end = itemView.findViewById(R.id.txt_end2);
            btn_menu_card = itemView.findViewById(R.id.btn_menu_card);
            btn_note_review = itemView.findViewById(R.id.btn_noteCard);
            btn_startCard = itemView.findViewById(R.id.btn_startCard);
            card_trip=itemView.findViewById(R.id.card_trip);

            btn_startCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call cancel alarm and start trip
                    startListener.startTrip(id);
                }
            });

            btn_menu_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, id);
                }
            });

            btn_note_review.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //  Log.i("note","the notes is: "+currentItem.getNotes().get(0));
                    noteReview.onNoteClick(v, id);
                }
            });
        }
    }

    public interface OnStartClickListener{
        void startTrip(int id);
    }

}

