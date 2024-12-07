package edu.birzeit.jetset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.tasks.ConnectionAsyncTask;


public class RetrievalFailedActivity extends AppCompatActivity implements ConnectionAsyncTask.TaskCallback{
    Button buttonRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_retrieval_failed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.retrieval_failed), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        buttonRetry = findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(v -> {
            ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(RetrievalFailedActivity.this);
            connectionAsyncTask.execute("https://mocki.io/v1/9261f6be-a97a-4ddb-8e7a-14dbdc7d8acc");
        });
    }

    @Override
    public void onTaskSuccess(String result) {
        Intent intent = new Intent(RetrievalFailedActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTaskFailure() {
        Toast.makeText(this, "Retry failed. Please check your connection.", Toast.LENGTH_SHORT).show();
    }
}
