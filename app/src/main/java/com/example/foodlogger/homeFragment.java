package com.example.foodlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {
    ListView listView;
    ArrayList<foodItem> foodList;
    DatabaseReference databaseReference;
    FirebaseUser user;
    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        listView = fragmentView.findViewById(R.id.id_listView);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        foodList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList = new ArrayList<>();
                long listLength = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").getChildrenCount();
                for (int i = 0; i < (int) listLength; i++) {
                    String foodName = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodName").getValue().toString();
                    String foodType = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodType").getValue().toString();
                    String foodDate = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("foodDate").getValue().toString();
                    String imageURI = dataSnapshot.child(user.getUid()).child("Foods").child("Food Info List").child("" + i).child("imageURI").getValue().toString();
                    foodList.add(new foodItem(foodName, foodType, foodDate, imageURI));
                }
                final CustomAdapter customAdapter = new CustomAdapter(fragmentView.getContext(), R.layout.foodlayout, foodList);
                listView.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("TAG", "List View Clicked");
                        Intent i = new Intent(getContext(), popUpWindow.class);
                        i.putExtra("name", foodList.get(position).getFoodName());
                        i.putExtra("type", foodList.get(position).getFoodType());
                        i.putExtra("date", foodList.get(position).getFoodDate());
                        i.putExtra("imageURI", foodList.get(position).getImageURI());
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fragmentView;
    }

    public class CustomAdapter extends ArrayAdapter<foodItem> {
        Context context;
        List<foodItem> foods;
        int resource;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<foodItem> objects) {
            super(context, resource, objects);
            this.context = context;
            foods = objects;
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) { //gets stuff inside each thing
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View adapterLayout = layoutInflater.inflate(resource, null);
            //widgets in ListView
            TextView foodNameTV = adapterLayout.findViewById(R.id.id_foodNameTV);
            TextView foodTypeTV = adapterLayout.findViewById(R.id.id_foodType);
            TextView foodDateTV = adapterLayout.findViewById(R.id.id_dateConsumed);
            ImageView foodImage = adapterLayout.findViewById(R.id.id_foodImage2);
            foodNameTV.setText(foodList.get(position).getFoodName());
            foodTypeTV.setText(foodList.get(position).getFoodType());
            foodDateTV.setText(foodList.get(position).getFoodDate());
            Glide.with(adapterLayout.getContext()).load(foodList.get(position).getImageURI()).into(foodImage);
            notifyDataSetChanged();
            return adapterLayout;
        }

    }
}
