package edu.birzeit.jetset.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.AdminHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;
import edu.birzeit.jetset.model.Admin;
import edu.birzeit.jetset.tasks.Hash;

public class EditProfileAdminFragment extends Fragment {
    private static final String SAVED_EMAIL = "SavedEmail";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText editFirstName, editLastName, editEmail, editPhone, editPassword, editConfirmPassword;
    private Button buttonSave;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private Admin admin;
    private String oldEmail;
    private String mParam1;
    private String mParam2;

    public EditProfileAdminFragment() {
    }

    public static EditProfileAdminFragment newInstance(String param1, String param2) {
        EditProfileAdminFragment fragment = new EditProfileAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataBaseHelper = new DataBaseHelper(getContext());
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Edit Profile");
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }
        oldEmail = sharedPrefManager.readString(SAVED_EMAIL, "");

        findViews();
        fillViews();

        buttonSave.setOnClickListener(v -> {
            admin = new Admin();
            if (dataInvalid()) return;
            String hashedPassword = Hash.hashPassword(editPassword.getText().toString());

            createAdmin(admin, editEmail.getText().toString(), editPhone.getText().toString(), editFirstName.getText().toString(),
                        editLastName.getText().toString(), hashedPassword);

            if (dataBaseHelper.updateAdmin(admin, oldEmail) == 1) {
                Toast.makeText(getContext(), "Admin Updated Successfully", Toast.LENGTH_SHORT).show();
                sharedPrefManager.writeString(SAVED_EMAIL, editEmail.getText().toString());
            } else
                Toast.makeText(getContext(), "Error Updating Admin", Toast.LENGTH_SHORT).show();
        });
    }

    private void fillViews() {
        Cursor cursor = dataBaseHelper.getUsersByEmail(oldEmail);
        if (cursor.moveToFirst()) {
            do {
                editFirstName.setText(cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")));
                editLastName.setText(cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")));
                editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")));
                editPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("PHONE")));
            } while (cursor.moveToNext());
        }
    }

    private void findViews() {
        editFirstName = getView().findViewById(R.id.editFirstName);
        editLastName = getView().findViewById(R.id.editLastName);
        editEmail = getView().findViewById(R.id.editEmail);
        editPhone = getView().findViewById(R.id.editPhone);
        editPassword = getView().findViewById(R.id.editPassword);
        editConfirmPassword = getView().findViewById(R.id.editConfirmPassword);
        buttonSave = getView().findViewById(R.id.buttonSave);
    }

    private boolean dataInvalid() {
        boolean validName = validateFirstName(editFirstName.getText().toString());
        boolean validLastName = validateLastName(editLastName.getText().toString());
        boolean validPassword = validatePassword(editPassword.getText().toString(), editConfirmPassword.getText().toString());
        boolean validEmail = validateEmail(editEmail.getText().toString());
        boolean validPhoneNumber = validatePhoneNumber(editPhone.getText().toString());

        if (!validName) {
            Toast.makeText(getContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validLastName) {
            Toast.makeText(getContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validEmail) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validPhoneNumber) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!validPassword) {
            Toast.makeText(getContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!editPassword.getText().toString().equals(editConfirmPassword.getText().toString())) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Does not match password");
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
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
            editPhone.setError("Phone number is required");
            return false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            editPhone.setError("Invalid phone number");
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