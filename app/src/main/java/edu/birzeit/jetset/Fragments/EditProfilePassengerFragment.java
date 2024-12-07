package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.PassengerHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;
import edu.birzeit.jetset.model.Passenger;
import edu.birzeit.jetset.tasks.Hash;
import edu.birzeit.jetset.tasks.ZoomOutPageTransformer;
import edu.birzeit.jetset.utils.CustomArrayAdapter;
import edu.birzeit.jetset.utils.VerticalPagerAdapter2;

public class EditProfilePassengerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SAVED_EMAIL = "SavedEmail";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
    View firstPageView, secondPageView;
    private Button buttonSave;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String oldEmail;
    private TextInputEditText editFirstName, editLastName, editEmail, editPhone, editPassword, editConfirmPassword, editPassportNumber, editIssueDate, editExpiryDate, editDateOfBirth;
    private Spinner spinnerCountry, spinnerFoodPreference, spinnerNationality;
    private String firstName, lastName, email, phoneNumber, password, confirmPassword, passportNumber, issuePlace, foodPreference, nationality;
    private Date issueDate, expiryDate, dateOfBirth;
    private ViewPager2 viewPager2;
    private VerticalPagerAdapter2 pagerAdapter;
    private Passenger passenger;
    private DotsIndicator dotsIndicator;

    private String mParam1;
    private String mParam2;

    public EditProfilePassengerFragment() {
    }

    public static EditProfilePassengerFragment newInstance(String param1, String param2) {
        EditProfilePassengerFragment fragment = new EditProfilePassengerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_profile_passenger, container, false);

        firstPageView = inflater.inflate(R.layout.layout_passenger_edit_first, container, false);
        secondPageView = inflater.inflate(R.layout.layout_passenger_edit_second, container, false);
        pagerAdapter = new VerticalPagerAdapter2(Arrays.asList(firstPageView, secondPageView));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataBaseHelper = new DataBaseHelper(getContext());
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Edit Profile");
        if (getActivity() instanceof PassengerHomeActivity) {
            ((PassengerHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }
        oldEmail = sharedPrefManager.readString(SAVED_EMAIL, "");

        viewPager2 = getView().findViewById(R.id.viewPager2);
        dotsIndicator = getView().findViewById(R.id.worm_dots_indicator);

        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        dotsIndicator.attachTo(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    findViews();
                } else if (position == 1) {
                    findViews();
                }
            }
        });


        findViews();
        fillViews();

        buttonSave.setOnClickListener(v -> {
            passenger = new Passenger();
            if (!getData()) return;
            if (isDataInvalid()) return;

            String hashedPassword = Hash.hashPassword(editPassword.getText().toString());

            createPassenger(passenger, email, phoneNumber, firstName, lastName, hashedPassword, passportNumber,
                            issueDate, expiryDate, issuePlace, foodPreference, dateOfBirth, nationality);

            if (dataBaseHelper.updatePassenger(passenger, oldEmail) == 1) {
                Toast.makeText(getContext(), "Passenger Updated Successfully", Toast.LENGTH_SHORT).show();
                sharedPrefManager.writeString(SAVED_EMAIL, editEmail.getText().toString());
            } else
                Toast.makeText(getContext(), "Error Updating Passenger", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager2.getCurrentItem() == 0) {
                    requireActivity().finish();
                } else {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
                }
            }
        });
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
            Toast.makeText(getContext(), "Please fill all fields with valid data", Toast.LENGTH_SHORT).show();
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
            spinnerCountry = secondPageView.findViewById(R.id.spinnerCountry);
            spinnerFoodPreference = secondPageView.findViewById(R.id.spinner);
            spinnerNationality = secondPageView.findViewById(R.id.spinnerNationality);

            Locale defaultLocale = Locale.getDefault();
            String defaultCountry = defaultLocale.getDisplayCountry();

            setupSpinnersAndDates(defaultCountry);
        }
        buttonSave = getView().findViewById(R.id.buttonSave);
    }

    private void fillViews() {
        Cursor cursor = dataBaseHelper.getUsersByEmail(oldEmail);
        if (cursor.moveToFirst()) {
            do {
                editFirstName.setText(cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")));
                editLastName.setText(cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")));
                editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")));
                editPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("PHONE")));
                editPassportNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_NUMBER")));
                editIssueDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_ISSUE_DATE")));
                editExpiryDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("PASSPORT_EXPIRATION_DATE")));
                editDateOfBirth.setText(cursor.getString(cursor.getColumnIndexOrThrow("DATE_OF_BIRTH")));
                spinnerCountry.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow("PASSPORT_ISSUE_PLACE")));
                spinnerFoodPreference.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow("FOOD_PREFERENCE")));
                spinnerNationality.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow("NATIONALITY")));
            } while (cursor.moveToNext());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSpinnersAndDates(String defaultCountry) {
        String[] types = {"Vegetarian", "Non-Vegetarian", "Vegan", "Pescatarian", "Diabetic", "Halal", "Kosher"};
        ArrayList<String> typesList = new ArrayList<>(List.of(types));
        CustomArrayAdapter adapter = new CustomArrayAdapter(getContext(), R.layout.spinner_item, typesList, 14);
        spinnerFoodPreference.setAdapter(adapter);

        SortedSet<String> countries = new TreeSet<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!TextUtils.isEmpty(locale.getDisplayCountry())) {
                countries.add(locale.getDisplayCountry());
            }
        }

        ArrayList<String> countriesList = new ArrayList<>(countries);
        CustomArrayAdapter adapterCountries = new CustomArrayAdapter(getContext(), R.layout.spinner_item, countriesList, 14);
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

    private boolean validateFirstName() {
        if (firstName.length() < 3 || firstName.length() > 20) {
            editFirstName.setError("Name must be at least 3 characters and less than 20 characters");
            Toast.makeText(getContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateLastName() {
        if (lastName.length() < 3 || lastName.length() > 20) {
            editLastName.setError("Name must be at least 3 characters and less than 20 characters");
            Toast.makeText(getContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(getContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
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

    private boolean validateEmail() {
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Invalid email address");
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        if (phoneNumber.isEmpty()) {
            editPhone.setError("Phone number is required");
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            editPhone.setError("Invalid phone number");
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassportNumber() {
        if (TextUtils.isEmpty(passportNumber) || passportNumber.length() != 9 || !passportNumber.matches("\\d{9}")) {
            editPassportNumber.setError("Invalid passport number. It should be 9 digits.");
            Toast.makeText(getContext(), "Please enter a valid passport number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateIssueDate() {
        if (TextUtils.isEmpty(editIssueDate.getText().toString())) {
            editIssueDate.setError("Issue date cannot be empty.");
            Toast.makeText(getContext(), "Please enter a valid issue date", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean validateExpiryDate() {
        if (TextUtils.isEmpty(editExpiryDate.getText().toString())) {
            editExpiryDate.setError("Expiry date cannot be empty.");
            Toast.makeText(getContext(), "Please enter a valid expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isDateAfter(issueDate, expiryDate)) {
            editExpiryDate.setError("Expiry date should be after issue date.");
            Toast.makeText(getContext(), "Please make sure expiry date is after issue date", Toast.LENGTH_SHORT).show();

            return false;

        }
        return true;
    }

    private boolean validateDateOfBirth() {
        if (TextUtils.isEmpty(editDateOfBirth.getText().toString())) {
            editDateOfBirth.setError("Date of birth cannot be empty.");
            Toast.makeText(getContext(), "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dateOfBirth.after(new Date())) {
            editDateOfBirth.setError("Date of birth cannot be in the future.");
            Toast.makeText(getContext(), "Please enter a valid date of birth", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Please enter a valid nationality", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateFoodPreference() {
        if (TextUtils.isEmpty(foodPreference)) {
            editDateOfBirth.setError("Food preference cannot be empty.");
            Toast.makeText(getContext(), "Please enter a valid food preference", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateIssuePlace() {
        if (TextUtils.isEmpty(issuePlace)) {
            editIssueDate.setError("Issue place cannot be empty.");
            Toast.makeText(getContext(), "Please enter a valid issue place", Toast.LENGTH_SHORT).show();
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
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }
}