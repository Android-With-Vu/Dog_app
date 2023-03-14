package com.example.dogapp.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogapp.R;
import com.example.dogapp.databinding.DogRowItemBinding;
import com.example.dogapp.model.DogBreed;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DogsAdapter extends RecyclerView.Adapter<DogsAdapter.ViewHolder> implements Filterable {

    private ArrayList<DogBreed> DogsList;
    private ArrayList<DogBreed> DogsListCopy;

    public DogsAdapter(ArrayList<DogBreed> dogsList) {

        DogsList = dogsList;
        DogsListCopy = dogsList;
    }

    @NonNull
    @Override
    public DogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DogRowItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                                R.layout.dog_row_item,
                                                parent,
                                                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DogsAdapter.ViewHolder holder, int position) {
        DogBreed dog = DogsList.get(position);

        holder.binding.setDog(dog);
        Picasso.get().load(dog.getUrl()).into(holder.binding.imgDog);
    }

    @Override
    public int getItemCount() {
        return DogsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String input = charSequence.toString().toLowerCase();

                List<DogBreed> filteredDog = new ArrayList<DogBreed>();
                if (input.isEmpty()){
                    filteredDog.addAll(DogsListCopy);
                }
                else{
                    for (DogBreed dog: DogsListCopy){
                        if(dog.getName().toLowerCase(Locale.ROOT).contains(input)){
                            filteredDog.add(dog);
                        }
                    }
                }
                FilterResults filterResults =  new FilterResults() ;
                filterResults.values = filteredDog;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                DogsList = new ArrayList<>();
                DogsList.addAll((List)filterResults.values);
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public DogRowItemBinding binding;

        public ViewHolder(DogRowItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;

            binding.imgDog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DogBreed dog = DogsList.get(getAdapterPosition());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dogBreed",dog);
                    Navigation.findNavController(view).navigate(R.id.detailFragment,bundle);
                }
            });

            itemView.setOnTouchListener(new OnSwipeTouchListener(){
                @Override
                public void onSwipeLeft() {
                    if ( binding.layout1.getVisibility() == View.GONE){
                        binding.layout1.setVisibility(View.VISIBLE);
                        binding.layout2.setVisibility(View.GONE);
                    }else{
                        binding.layout2.setVisibility(View.VISIBLE);
                        binding.layout1.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    public class OnSwipeTouchListener implements View.OnTouchListener{

        private final GestureDetector gestureDetector = new GestureDetector( new GestureListener());

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }


        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 50;
            private static final int SWIPE_VELOCITY_THRESHOLD = 50;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                OnSwipeTouchListener.this.onLongPress();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        public void onClick() {

        }
        public void onLongPress() {
        }
    }
}
