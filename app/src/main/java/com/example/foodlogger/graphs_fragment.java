package com.example.foodlogger;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class graphs_fragment extends Fragment {
    PieChartView pieChartView;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    long fruitsL, vegetablesL, grainsL, dairyL, proteinL;
    int fruits, vegetables, grains, dairy, protein;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_graphs_fragment, container, false);
        //https://www.codingdemos.com/android-pie-chart-tutorial/
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        pieChartView = fragmentView.findViewById(R.id.id_pieChart);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<SliceValue> pieData = new ArrayList<>();

                fruitsL = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Fruits").getValue();
                vegetablesL = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Vegetables").getValue();
                grainsL = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").getValue();
                dairyL = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Grains").getValue();
                proteinL = (long) dataSnapshot.child(user.getUid()).child("Foods").child("Type Counts").child("Protein").getValue();
                fruits = (int) fruitsL;
                vegetables = (int) vegetablesL;
                grains = (int) grainsL;
                protein = (int) proteinL;
                dairy = (int) dairyL;
                if (fruits > 0)
                    pieData.add(new SliceValue(fruits, Color.RED).setLabel("Fruits: " + fruits));
                if (vegetables > 0)
                    pieData.add(new SliceValue(vegetables, Color.GREEN).setLabel("Vegetables: " + vegetables));
                if (grains > 0)
                    pieData.add(new SliceValue(grains, Color.YELLOW).setLabel("Grains: " + grains));
                if (protein > 0)
                    pieData.add(new SliceValue(protein, Color.MAGENTA).setLabel("Protein: " + protein));
                if (dairy > 0)
                    pieData.add(new SliceValue(dairy, Color.BLUE).setLabel("Dairy: " + dairy));
                PieChartData pieChartData = new PieChartData(pieData);
                pieChartData.setHasLabels(true);
                pieChartView.setPieChartData(pieChartData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fragmentView;

    }
}
