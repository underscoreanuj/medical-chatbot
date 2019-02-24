package com.example.yomna.symptomate;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class History extends AppCompatActivity {

    private RecyclerView ticket_list_view;

    private String id = "";

    private Query query;
    private FirebaseRecyclerAdapter<HistoryContent, HisoryViewHolder> adapter;
    private DatabaseReference mDatabaseTickets;
    static List<String> mappedValues = new ArrayList<String>();

    TextView empty;
    public String android_id;

    private FirebaseRecyclerOptions<HistoryContent> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Calendar calendar = Calendar.getInstance();

        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH)+1;
        int _day = calendar.get(Calendar.DAY_OF_MONTH);

        String day, mon, yea;

        if(_day/10 == 0)
            day = "0" + _day;
        else
            day = _day + "";

        if(_month/10 == 0)
            mon = "0" + _month;
        else
            mon = _month + "";

        yea = _year + "";

        String t_date = day + "-" + mon + "-" + yea;

        Toast.makeText(this, t_date , Toast.LENGTH_SHORT).show();

        query = FirebaseDatabase.getInstance().getReference().child("users").child(android_id).orderByChild("date");
        query.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<HistoryContent>()
                .setQuery(query, HistoryContent.class)
                .build();


        ticket_list_view = findViewById(R.id.hist_view_list);
        ticket_list_view.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new FirebaseRecyclerAdapter<HistoryContent, HisoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HisoryViewHolder holder, int position, @NonNull HistoryContent model) {
                final String post_key = getRef(position).getKey();

                holder.setDate(model.getDate());
                String alt = model.getSymptoms();
                alt = alt.replace("==>", "• ");
                //holder.setSymp(model.getSymptoms());
                holder.setSymp(alt);
                holder.setDiag(model.getDiagnosis());

            }

            @NonNull
            @Override
            public HisoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_view, parent, false);

                return new HisoryViewHolder(view);
            }



            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0) {
                    Toast.makeText(History.this, "No History!", Toast.LENGTH_SHORT).show();
                }
                else {

                }

            }
        };

        //ticket_list_view.scrollTo(30, 0);
        ticket_list_view.setAdapter(adapter);

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public class HisoryViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public HisoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setDate(String mDate) {
            TextView movie_date = mView.findViewById(R.id.hist_date);
            movie_date.setText(mDate);
        }

        public void setSymp(String mSymp) {
            TextView movie_time = mView.findViewById(R.id.hist_symp);
            movie_time.setText(mSymp);
        }

        public void setDiag(String mDiag) {
            TextView movie_time = mView.findViewById(R.id.hist_diag);
            movie_time.setText(mDiag);
        }
    }

}
