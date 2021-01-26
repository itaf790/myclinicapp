package com.example.myclinicapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myclinicapp.Interface.IRecyclerItemSelectedListener;
import com.example.myclinicapp.R;
import com.example.myclinicapp.info.calendier;
import com.example.myclinicapp.model.ApointementInformation;
import com.example.myclinicapp.model.TimeSlot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {


    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;
    SimpleDateFormat simpleDateFormat;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,parent,false);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.txt_time_slot.setText(new StringBuilder(calendier.convertTimeSlotToString(position)).toString());
        if(timeSlotList.size()==0){
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

        }
        else{
            for (TimeSlot slotValue:timeSlotList){
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == position){

                    holder.card_time_slot.setTag(calendier.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

                    holder.txt_time_slot_description.setText("Full");
                    if(slotValue.getType().equals("Checked"))
                        holder.txt_time_slot_description.setText("Choosen");
                    holder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                }
            }

        }
        if (!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);


        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for(CardView cardView:cardViewList) {
                    if (cardView.getTag() == null)
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }
                holder.card_time_slot.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.holo_orange_dark));

                Intent intent = new Intent(calendier.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(calendier.KEY_TIME_SLOT,position);
                calendier.currentTimeSlot = position ;
                intent.putExtra(calendier.KEY_STEP,2);
                Log.e("pos ", "onItemSelectedListener: "+position );
                localBroadcastManager.sendBroadcast(intent);
                if(calendier.CurrentUserType == "doctor" && holder.txt_time_slot_description.getText().equals("Available")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.card_time_slot.getContext());
                    alert.setTitle("Block");
                    alert.setMessage("Are you sure you want to block?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ApointementInformation apointementInformation = new ApointementInformation();
                            apointementInformation.setApointementType(calendier.Currentaappointementatype);
                            apointementInformation.setDoctorId(calendier.CurreentDoctor);
                            apointementInformation.setDoctorName(calendier.CurrentDoctorName);
                            apointementInformation.setChemin("Doctor/"+calendier.CurreentDoctor+"/"+calendier.simpleFormat.format(calendier.currentDate.getTime())+"/"+String.valueOf(calendier.currentTimeSlot));
                            apointementInformation.setType("full");
                            apointementInformation.setTime(new StringBuilder(calendier.convertTimeSlotToString(calendier.currentTimeSlot))
                                    .append("at")
                                    .append(simpleDateFormat.format(calendier.currentDate.getTime())).toString());
                            apointementInformation.setSlot(Long.valueOf(calendier.currentTimeSlot));

                            DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                    .collection("Doctor")
                                    .document(calendier.CurreentDoctor)
                                    .collection(calendier.simpleFormat.format(calendier.currentDate.getTime()))
                                    .document(String.valueOf(calendier.currentTimeSlot));

                            bookingDate.set(apointementInformation);
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return 20;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}

