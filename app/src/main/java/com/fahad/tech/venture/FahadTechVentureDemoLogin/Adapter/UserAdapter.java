package com.fahad.tech.venture.FahadTechVentureDemoLogin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.fahad.tech.venture.FahadTechVentureDemoLogin.AdminScreen;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.AppDatabase;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.DBhelper.User;
import com.fahad.tech.venture.FahadTechVentureDemoLogin.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.tvSerialNumber.setText(String.valueOf(position + 1));
        User user = userList.get(position);
        holder.tvUsername.setText(user.username);
        holder.tvEmail.setText(user.email);

        holder.ivEdit.setOnClickListener(v -> showEditDialog(user));
        holder.ivDelete.setOnClickListener(v -> deleteUser(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialNumber, tvUsername, tvEmail;
        ImageView ivEdit, ivDelete;

        UserViewHolder(View itemView) {
            super(itemView);
            tvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    private void showEditDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);
        builder.setTitle("Edit User Details");

        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etFirstName = view.findViewById(R.id.etFirstName);
        EditText etLastName = view.findViewById(R.id.etLastName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        etUsername.setText(user.username);
        etFirstName.setText(user.firstName);
        etLastName.setText(user.lastName);
        etEmail.setText(user.email);

        AlertDialog dialog = builder.create();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnUpdate.setOnClickListener(v -> {
            user.username = etUsername.getText().toString().trim();
            user.firstName = etFirstName.getText().toString().trim();
            user.lastName = etLastName.getText().toString().trim();
            user.email = etEmail.getText().toString().trim();

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                db.userDao().updateUser(user);
                List<User> updatedUsers = db.userDao().getAllUsers(); // Fetch updated users
                ((AdminScreen) context).runOnUiThread(() -> {
                    ((AdminScreen) context).refreshUserList(updatedUsers);
                    dialog.dismiss();
                });
            }).start();
        });

        dialog.show();
    }

    private void deleteUser(User user) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.userDao().deleteUser(user);
            List<User> updatedUsers = db.userDao().getAllUsers(); // Fetch updated users
            ((AdminScreen) context).runOnUiThread(() -> {
                ((AdminScreen) context).refreshUserList(updatedUsers);
            });
        }).start();
    }

    public void updateUserList(List<User> users) {
        this.userList.clear();
        this.userList.addAll(users);
        notifyDataSetChanged();
    }
}
