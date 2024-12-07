package edu.birzeit.jetset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.birzeit.jetset.R;

public class ChooseRoleActivity extends AppCompatActivity {

    RadioButton admin;
    RadioButton passenger;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_role);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.choose_role), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        admin = findViewById(R.id.radio_admin);
        passenger = findViewById(R.id.radio_passenger);
        continueButton = findViewById(R.id.buttonContinue);

        continueButton.setOnClickListener(v -> {
            Intent intent;
            if (admin.isChecked()) {
                intent = new Intent(ChooseRoleActivity.this, SignUpAdminActivity.class);
                startActivity(intent);
            } else if (passenger.isChecked()) {
                intent = new Intent(ChooseRoleActivity.this, SignUpPassengerActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ChooseRoleActivity.this, "Please choose a role to proceed.", Toast.LENGTH_SHORT).show();
            }

        });

    }
}
