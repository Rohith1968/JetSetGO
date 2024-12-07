package edu.birzeit.jetset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.model.Admin;
import edu.birzeit.jetset.tasks.Hash;

public class SignUpAdminActivity extends AppCompatActivity {

    TextInputEditText editFirstName;
    TextInputEditText editLastName;
    TextInputEditText editEmail;
    TextInputEditText editPhoneNumber;
    TextInputEditText editPassword;
    TextInputEditText editConfirmPassword;

    Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_admin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DataBaseHelper dataBaseHelper = new DataBaseHelper(SignUpAdminActivity.this);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPhoneNumber = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> {
            Admin admin = new Admin();
            if (dataInvalid()) return;

            if (dataBaseHelper.getUsersByEmail(editEmail.getText().toString()).getCount() > 0) {
                Toast.makeText(this, "Admin already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            String hashedPassword = Hash.hashPassword(editPassword.getText().toString());

            createAdmin(admin, editEmail.getText().toString(), editPhoneNumber.getText().toString(), editFirstName.getText().toString(),
                        editLastName.getText().toString(), hashedPassword);

            dataBaseHelper.insertAdmin(admin);

            Toast.makeText(this, "Admin Created Successfully", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        });

    }

    private boolean dataInvalid() {
        boolean validName = validateFirstName(editFirstName.getText().toString());
        boolean validLastName = validateLastName(editLastName.getText().toString());
        boolean validPassword = validatePassword(editPassword.getText().toString(), editConfirmPassword.getText().toString());
        boolean validEmail = validateEmail(editEmail.getText().toString());
        boolean validPhoneNumber = validatePhoneNumber(editPhoneNumber.getText().toString());

        if (!validName) {
            Toast.makeText(this, "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validLastName) {
            Toast.makeText(this, "Please enter a valid last name", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validEmail) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validPhoneNumber) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validPassword) {
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!editPassword.getText().toString().equals(editConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean validateFirstName(String name) {
        if (name.length() < 3 || name.length() > 20) {
            editFirstName.setError("Name must be at least 3 characters and less than 20 characters");
            return false;
        }
        return true;
    }

    private boolean validateLastName(String name) {
        if (name.length() < 3 || name.length() > 20) {
            editLastName.setError("Name must be at least 3 characters and less than 20 characters");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password, String confirmPassword) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,15}$";

//        if (!password.matches(passwordPattern)) {
//            if (password.length() < 8 || password.length() > 15) {
//                editPassword.setError("Password must be at least 8 characters and less than 15 characters");
//            } else if (!password.matches(".*[a-z].*")) {
//                editPassword.setError("Password must contain at least one lowercase letter");
//            } else if (!password.matches(".*[A-Z].*")) {
//                editPassword.setError("Password must contain at least one uppercase letter");
//            } else if (!password.matches(".*[0-9].*")) {
//                editPassword.setError("Password must contain at least one number");
//            } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
//                editPassword.setError("Password must contain at least one special character");
//            }
//            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
//            return false;
        if (!password.matches(passwordPattern)) {
            editPassword.setError("Password must be 8-15 characters with upper, lower, number, and special character.");
            Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Does not match password");
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid email address");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            editPhoneNumber.setError("Phone number is required");
            return false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            editPhoneNumber.setError("Invalid phone number");
            return false;
        } else {
            return true;
        }
    }


    private void createAdmin(Admin admin, String email, String phoneNumber, String firstName, String lastName, String hashedPassword) {
        admin.setEmail(email);
        admin.setPhoneNumber(phoneNumber);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setHashedPassword(hashedPassword);
    }


}
