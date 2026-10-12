package com.boots.firebasetaskapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TaskAdapter adapter;
    private TextView tvEmpty;
    private ListenerRegistration registration;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            goToLogin();
            return;
        }
        userId = user.getUid();
        db = FirebaseFirestore.getInstance();

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        Button btnLogout = findViewById(R.id.btnLogout);

        adapter = new TaskAdapter(this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddTaskDialog());
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            goToLogin();
        });

        listenToTasks();
    }

    // Sirf is user ke tasks live sunte hain
    private void listenToTasks() {
        registration = db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (snapshots == null) return;

                    List<Task> list = snapshots.toObjects(Task.class);
                    // Naye tasks upar
                    Collections.sort(list, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                    adapter.setTasks(list);
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void showAddTaskDialog() {
        EditText input = new EditText(this);
        input.setHint("Task title");
        input.setSingleLine(true);

        new AlertDialog.Builder(this)
                .setTitle("New Task")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addTask(title);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addTask(String title) {
        Task task = new Task(title, userId);
        db.collection("tasks").add(task)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onToggleDone(Task task, boolean isDone) {
        if (task.getId() == null) return;
        db.collection("tasks").document(task.getId()).update("done", isDone)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onDelete(Task task) {
        if (task.getId() == null) return;
        db.collection("tasks").document(task.getId()).delete()
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) registration.remove();
    }
}