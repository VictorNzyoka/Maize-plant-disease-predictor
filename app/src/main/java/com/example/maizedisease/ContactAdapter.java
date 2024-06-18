package com.example.maizedisease;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<UserModel> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserModel userDetails);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        
        this.listener = listener;
    }

    public ContactAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userDetails = userList.get(position);
        holder.bind(userDetails);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(userDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName, textEmail, textPhone;
        private LinearLayout LLUserHolders;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.userName);
            textEmail = itemView.findViewById(R.id.userEmail);
            textPhone = itemView.findViewById(R.id.userPhone);
            LLUserHolders = itemView.findViewById(R.id.LLUserHolder);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) LLUserHolders.getLayoutParams();
            layoutParams.topMargin = 5; // margin in pixels
            layoutParams.bottomMargin = 5;
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5; // Add right margin if needed
            LLUserHolders.setLayoutParams(layoutParams);
        }

        public void bind(UserModel userDetails) {
            textName.setText(userDetails.getUsername() + " (" + userDetails.getPhoneNumber() + ")");
            textEmail.setText(userDetails.getEmail());
            textPhone.setText(userDetails.getPhoneNumber());  // Set text for textPhone
        }
    }
}