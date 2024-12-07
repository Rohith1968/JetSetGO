package edu.birzeit.jetset.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.birzeit.jetset.Fragments.DatePickerFragment;
import edu.birzeit.jetset.R;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.model.Passenger;
import edu.birzeit.jetset.tasks.Hash;
import edu.birzeit.jetset.tasks.ZoomOutPageTransformer;
import edu.birzeit.jetset.utils.CustomArrayAdapter;
import edu.birzeit.jetset.utils.VerticalPagerAdapter;

public class SignUpPassengerActivity extends AppCompatActivity {
    private static final int[] LAYOUTS = {R.layout.layout_passenger_first, R.layout.layout_passenger_second};
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
    View firstPageView, secondPageView;
    private TextInputEditText editFirstName, editLastName, editEmail, editPhone, editPassword, editConfirmPassword, editPassportNumber, editIssueDate, editExpiryDate, editDateOfBirth;
    private Spinner spinnerCountry, spinnerFoodPreference, spinnerNationality;
    private String firstName, lastName, email, phoneNumber, password, confirmPassword, hashedPassword, passportNumber, issuePlace, foodPreference, nationality;
    private Date issueDate, expiryDate, dateOfBirth;
    private DataBaseHelper dataBaseHelper;
    private ViewPager2 viewPager2;
    private VerticalPagerAdapter pagerAdapter;
    private Passenger passenger;
    private DotsIndicator dotsIndicator;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_passenger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up_passenger), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dataBaseHelper = new DataBaseHelper(SignUpPassengerActivity.this);

        viewPager2 = findViewById(R.id.viewPager);
        dotsIndicator = findViewById(R.id.worm_dots_indicator);
        pagerAdapter = new VerticalPagerAdapter(LAYOUTS);
        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        dotsIndicator.attachTo(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    firstPageView = pagerAdapter.getViewAtPosition(0);
                    if (firstPageView != null) {
                        findViews();
                    }
                } else if (position == 1) {
                    secondPageView = pagerAdapter.getViewAtPosition(1);
                    if (secondPageView != null) {
                        findViews();
                    }
                }
            }
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager2.getCurrentItem() == 0) {
                    finish();
                } else {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
                }
            }
        });

        Button continueButton = findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(v -> {
            if (!getData()) {
                return;
            }
            if (isDataInvalid()) {
                return;
            }
            if (dataBaseHelper.getUsersByEmail(email).getCount() > 0) {
                Toast.makeText(this, "Passenger already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            hashedPassword = Hash.hashPassword(password);

            passenger = new Passenger();
            createPassenger(passenger, email, phoneNumber, firstName, lastName, hashedPassword, passportNumber,
                            issueDate, expiryDate, issuePlace, foodPreference, dateOfBirth, nationality);

            dataBaseHelper.insertPassenger(passenger);

            Toast.makeText(this, "Passenger Created Successfully", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1500);
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSpinnersAndDates(String defaultCountry) {
        String[] types = {"Vegetarian", "Non-Vegetarian", "Vegan", "Pescatarian", "Diabetic", "Halal", "Kosher"};
        ArrayList<String> typesList = new ArrayList<>(List.of(types));
        CustomArrayAdapter adapter = new CustomArrayAdapter(this, R.layout.spinner_item, typesList, 14);
        spinnerFoodPreference.setAdapter(adapter);

        SortedSet<String> countries = new TreeSet<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!TextUtils.isEmpty(locale.getDisplayCountry())) {
                countries.add(locale.getDisplayCountry());
            }
        }

        ArrayList<String> countriesList = new ArrayList<>(countries);
        CustomArrayAdapter adapterCountries = new CustomArrayAdapter(this, R.layout.spinner_item, countriesList, 14);
        spinnerCountry.setAdapter(adapterCountries);
        spinnerNationality.setAdapter(adapterCountries);


        if (!TextUtils.isEmpty(defaultCountry)) {
            int position = adapterCountries.getPosition(defaultCountry);
            if (position >= 0) {
                spinnerCountry.setSelection(position);
                spinnerNationality.setSelection(position);
            } else {
                spinnerCountry.setSelection(0);
                spinnerNationality.setSelection(0);
            }
        }


        editIssueDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePickerDialog(editIssueDate);
                v.performClick();
            }
            return false;
        });
        editExpiryDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePickerDialog(editExpiryDate);
                v.performClick();
            }
            return false;
        });
        editDateOfBirth.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePickerDialog(editDateOfBirth);
                v.performClick();
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBaseHelper != null) {
            dataBaseHelper.close();
        }
    }


    private void findViews() {
        if (firstPageView != null) {
            editFirstName = firstPageView.findViewById(R.id.editFirstName);
            editLastName = firstPageView.findViewById(R.id.editLastName);
            editEmail = firstPageView.findViewById(R.id.editEmail);
            editPhone = firstPageView.findViewById(R.id.editPhone);
            editPassword = firstPageView.findViewById(R.id.editPassword);
            editConfirmPassword = firstPageView.findViewById(R.id.editConfirmPassword);
        }
        if (secondPageView != null) {
            editPassportNumber = secondPageView.findViewById(R.id.editPassportNumber);
            editIssueDate = secondPageView.findViewById(R.id.editTextDate);
            editExpiryDate = secondPageView.findViewById(R.id.editTextDate2);
            editDateOfBirth = secondPageView.findViewById(R.id.editTextDate3);
            spinnerFoodPreference = secondPageView.findViewById(R.id.spinner);
            spinnerCountry = secondPageView.findViewById(R.id.spinnerCountry);
            spinnerNationality = secondPageView.findViewById(R.id.spinnerNationality);

            Locale defaultLocale = Locale.getDefault();
            String defaultCountry = defaultLocale.getDisplayCountry();

            setupSpinnersAndDates(defaultCountry);
        }
    }

    private void createPassenger(Passenger passenger, String email, String phoneNumber, String firstName, String lastName, String hashedPassword,
                                 String passportNumber, Date issueDate, Date expiryDate, String issuePlace, String foodPreference, Date dateOfBirth, String nationality) {


        passenger.setEmail(email);
        passenger.setPhoneNumber(phoneNumber);
        passenger.setFirstName(firstName);
        passenger.setLastName(lastName);
        passenger.setHashedPassword(hashedPassword);
        passenger.setPassportNumber(passportNumber);
        passenger.setPassportIssueDate(dateFormat.format(issueDate));
        passenger.setPassportExpiryDate(dateFormat.format(expiryDate));
        passenger.setDateOfBirth(dateFormat.format(dateOfBirth));
        passenger.setPassportIssuePlace(issuePlace);
        passenger.setFoodPreference(foodPreference);
        passenger.setNationality(nationality);
    }

    private boolean getData() {

        if (firstPageView != null) {
            firstName = editFirstName.getText().toString();
            lastName = editLastName.getText().toString();
            email = editEmail.getText().toString();
            phoneNumber = editPhone.getText().toString();
            password = editPassword.getText().toString();
            confirmPassword = editConfirmPassword.getText().toString();
        }
        if (secondPageView != null) {
            passportNumber = editPassportNumber.getText().toString();
            issuePlace = editIssueDate.getText().toString();
            foodPreference = spinnerFoodPreference.getSelectedItem().toString();
            nationality = spinnerNationality.getSelectedItem().toString();

            try {
                issueDate = dateFormat.parse(editIssueDate.getText().toString());
                expiryDate = dateFormat.parse(editExpiryDate.getText().toString());
                dateOfBirth = dateFormat.parse(editDateOfBirth.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("PASSENGER", "getData: " + e.getMessage());
            }
        }

        if (isDataEmpty()) {
            Toast.makeText(this, "Please fill all fields with valid data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDataEmpty() {
        return firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || passportNumber.isEmpty() || issuePlace.isEmpty() || foodPreference.isEmpty() ||
                nationality.isEmpty() || dateOfBirth == null || issueDate == null || expiryDate == null;
    }

    private boolean isDataInvalid() {
        return !validateFirstName() || !validateLastName() || !validateEmail() || !validatePhoneNumber() || !validatePassword()
                || !validatePassportNumber() || !validateIssueDate() || !validateExpiryDate() || !validateDateOfBirth()
                || !validateNationality() || !validateFoodPreference() || !validateIssuePlace();
    }

    private boolean validateFirstName() {
        if (firstName.length() < 3 || firstName.length() > 20) {
            editFirstName.setError("Name must be at least 3 characters and less than 20 characters");
            Toast.makeText(this, "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateLastName() {
        if (lastName.length() < 3 || lastName.length() > 20) {
            editLastName.setError("Name must be at least 3 characters and less than 20 characters");
            Toast.makeText(this, "Please enter a valid last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
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

    private boolean validateEmail() {
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid email address");
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        if (phoneNumber.isEmpty()) {
            editPhone.setError("Phone number is required");
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            editPhone.setError("Invalid phone number");
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassportNumber() {
        if (TextUtils.isEmpty(passportNumber) || passportNumber.length() != 9 || !passportNumber.matches("\\d{9}")) {
            editPassportNumber.setError("Invalid passport number. It should be 9 digits.");
            Toast.makeText(this, "Please enter a valid passport number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateIssueDate() {
        if (TextUtils.isEmpty(editIssueDate.getText().toString())) {
            editIssueDate.setError("Issue date cannot be empty.");
            Toast.makeText(this, "Please enter a valid issue date", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean validateExpiryDate() {
        if (TextUtils.isEmpty(editExpiryDate.getText().toString())) {
            editExpiryDate.setError("Expiry date cannot be empty.");
            Toast.makeText(this, "Please enter a valid expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isDateAfter(issueDate, expiryDate)) {
            editExpiryDate.setError("Expiry date should be after issue date.");
            Toast.makeText(this, "Please make sure expiry date is after issue date", Toast.LENGTH_SHORT).show();

            return false;

        }
        return true;
    }

    private boolean validateDateOfBirth() {
        if (TextUtils.isEmpty(editDateOfBirth.getText().toString())) {
            editDateOfBirth.setError("Date of birth cannot be empty.");
            Toast.makeText(this, "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dateOfBirth.after(new Date())) {
            editDateOfBirth.setError("Date of birth cannot be in the future.");
            Toast.makeText(this, "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDateAfter(Date startDate, Date endDate) {
        return endDate != null && startDate != null && endDate.after(startDate);
    }

    private boolean validateNationality() {
        if (TextUtils.isEmpty(nationality)) {
            editDateOfBirth.setError("Nationality cannot be empty.");
            Toast.makeText(this, "Please enter a valid nationality", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateFoodPreference() {
        if (TextUtils.isEmpty(foodPreference)) {
            editDateOfBirth.setError("Food preference cannot be empty.");
            Toast.makeText(this, "Please enter a valid food preference", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateIssuePlace() {
        if (TextUtils.isEmpty(issuePlace)) {
            editIssueDate.setError("Issue place cannot be empty.");
            Toast.makeText(this, "Please enter a valid issue place", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener((year, month, day) -> {
            String selectedDate = day + "/" + (month + 1) + "/" + year;
            editText.setText(selectedDate);
        });
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }
}


