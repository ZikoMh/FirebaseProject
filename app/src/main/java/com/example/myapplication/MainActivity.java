package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText editTextTask;
    private Button buttonAddTask;
    private RecyclerView recyclerViewTasks;
    private TasksAdapter adapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TasksAdapter(taskList);
        recyclerViewTasks.setAdapter(adapter);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        loadTasks();
    }

    private void addTask() {
        String taskDescription = editTextTask.getText().toString().trim();
        if (!taskDescription.isEmpty()) {
            Task newTask = new Task(taskDescription);
            db.collection("tasks").add(newTask).addOnSuccessListener(documentReference -> {
                taskList.add(newTask);
                adapter.notifyDataSetChanged();
                editTextTask.setText("");
            });
        }
    }

    private void loadTasks() {
        db.collection("tasks").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Task task = document.toObject(Task.class);
                taskList.add(task);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
