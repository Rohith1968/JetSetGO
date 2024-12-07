package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.AdminHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;
import edu.birzeit.jetset.model.Reservation;
import edu.birzeit.jetset.utils.CustomArrayAdapter;

public class ConfirmReservationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPrefManager sharedPrefManager;
    String foodPreference, flightClass, numOfExtraBags;
    double totalPrice;
    private TextView textViewPassengerName, textViewPhoneNumber, textViewEmail, textViewPassportNumber, textViewIssuePlace,
            textViewIssueDate, textViewExpirationDate, textViewDOB, textViewNationality;
    private Spinner spinnerFoodPreference, spinnerFlightClass;
    private TextInputEditText editNumOfExtraBags;
    private String flightId;
    private String passengerEmail;
    private Button buttonConfirmReservation;
    private DataBaseHelper dataBaseHelper;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConfirmReservationFragment() {
        // Required empty public constructor
    }

    public static ConfirmReservationFragment newInstance(String param1, String param2) {
        ConfirmReservationFragment fragment = new ConfirmReservationFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Confirm Reservation");
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }
        if (getArguments() != null) {
            flightId = getArguments().getString("FLIGHT_ID");
            passengerEmail = getArguments().getString("PASSENGER_EMAIL");

            dataBaseHelper = new DataBaseHelper(getContext());

            findViews();
            setupSpinners();
            fillViews();

            buttonConfirmReservation.setOnClickListener(v -> {
                if (!getData())
                    return;
                if (!validateNumOfExtraBags())
                    return;

                totalPrice = dataBaseHelper.calculateTotalPrice(Integer.parseInt(flightId),
                                                                flightClass,
                                                                Integer.parseInt(numOfExtraBags));

                openConfirmSummaryFragment();


            });

            getParentFragmentManager().setFragmentResultListener("reservationConfirmed", this, (requestKey, result) -> {
                boolean isConfirmed = result.getBoolean("isConfirmed", false);
                if (isConfirmed) {
                    Reservation reservation = new Reservation();
                    createReservation(reservation);

                    if (dataBaseHelper.insertReservation(reservation) == -1)
                        Toast.makeText(getContext(), "Reservation Failed", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Flight Reserved Successfully", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        Bundle result2 = new Bundle();
                        result2.putBoolean("isUpdated", true);
                        getParentFragmentManager().setFragmentResult("reservationDataUpdated", result2);
                        getActivity().onBackPressed();
                    }, 0);
                }
            });


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataBaseHelper != null) {
            dataBaseHelper.close();
        }
    }

    private void openConfirmSummaryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("FLIGHT_ID", flightId);
        bundle.putString("FOOD_PREFERENCE", foodPreference);
        bundle.putString("FLIGHT_CLASS", flightClass);
        bundle.putString("NUM_OF_EXTRA_BAGS", numOfExtraBags);
        bundle.putString("TOTAL_PRICE", String.valueOf(totalPrice));


        ConfirmSummaryFragment summaryFragment = new ConfirmSummaryFragment();
        summaryFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, summaryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void createReservation(Reservation reservation) {
        reservation.setFlightId(flightId);
        reservation.setPassengerEmail(passengerEmail);
        reservation.setFlightClass(flightClass);
        reservation.setNumOfExtraBags(numOfExtraBags);
        reservation.setFoodPreference(foodPreference);
        reservation.setTotalPrice(totalPrice);
    }

    private void findViews() {
        textViewPassengerName = getView().findViewById(R.id.textViewPassengerName);
        textViewPhoneNumber = getView().findViewById(R.id.textViewPhoneNumber);
        textViewEmail = getView().findViewById(R.id.textViewEmail);
        textViewPassportNumber = getView().findViewById(R.id.textViewPassportNumber);
        textViewIssuePlace = getView().findViewById(R.id.textViewIssuePlace);
        textViewIssueDate = getView().findViewById(R.id.textViewIssueDate);
        textViewExpirationDate = getView().findViewById(R.id.textViewExpirationDate);
        textViewDOB = getView().findViewById(R.id.textViewDOB);
        textViewNationality = getView().findViewById(R.id.textViewNationality);
        spinnerFoodPreference = getView().findViewById(R.id.spinnerFoodPreference);
        spinnerFlightClass = getView().findViewById(R.id.spinnerFlightClass);
        editNumOfExtraBags = getView().findViewById(R.id.editNumOfExtraBags);
        buttonConfirmReservation = getView().findViewById(R.id.buttonConfirm);
    }

    private void setupSpinners() {
        String[] foodPreference = {"Vegetarian", "Non-Vegetarian", "Vegan", "Pescatarian", "Diabetic", "Halal", "Kosher"};
        ArrayList<String> foodPreferenceList = new ArrayList<>(List.of(foodPreference));
        CustomArrayAdapter adapter = new CustomArrayAdapter(getContext(), R.layout.spinner_item, foodPreferenceList, 14);
        spinnerFoodPreference.setAdapter(adapter);

        String[] flightClass = {"Economy", "Business"};
        ArrayList<String> flightClassList = new ArrayList<>(List.of(flightClass));
        CustomArrayAdapter adapter2 = new CustomArrayAdapter(getContext(), R.layout.spinner_item, flightClassList, 14);
        spinnerFlightClass.setAdapter(adapter2);
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void fillViews() {
        Cursor cursor = dataBaseHelper.getUsersByEmail(passengerEmail);
        if (cursor.moveToFirst()) {
            do {
                textViewPassengerName.setText(cursor.getString(cursor.getColumnIndex("FIRST_NAME")) + " " + cursor.getString(cursor.getColumnIndex("LAST_NAME")));
                textViewPhoneNumber.setText(cursor.getString(cursor.getColumnIndex("PHONE")));
                textViewEmail.setText(cursor.getString(cursor.getColumnIndex("EMAIL")));
                textViewPassportNumber.setText(cursor.getString(cursor.getColumnIndex("PASSPORT_NUMBER")));
                textViewIssuePlace.setText(cursor.getString(cursor.getColumnIndex("PASSPORT_ISSUE_PLACE")));
                textViewIssueDate.setText(cursor.getString(cursor.getColumnIndex("PASSPORT_ISSUE_DATE")));
                textViewExpirationDate.setText(cursor.getString(cursor.getColumnIndex("PASSPORT_EXPIRATION_DATE")));
                textViewDOB.setText(cursor.getString(cursor.getColumnIndex("DATE_OF_BIRTH")));
                textViewNationality.setText(cursor.getString(cursor.getColumnIndex("NATIONALITY")));

                String foodPreference = cursor.getString(cursor.getColumnIndex("FOOD_PREFERENCE"));
                ArrayAdapter adapter = (ArrayAdapter) spinnerFoodPreference.getAdapter();
                int spinnerPosition = adapter.getPosition(foodPreference);
                spinnerFoodPreference.setSelection(spinnerPosition);
            } while (cursor.moveToNext());
        }
    }

    private boolean getData() {
        foodPreference = spinnerFoodPreference.getSelectedItem().toString();
        flightClass = spinnerFlightClass.getSelectedItem().toString();
        numOfExtraBags = editNumOfExtraBags.getText().toString();

        if (isDataEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields with valid data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDataEmpty() {
        return foodPreference.isEmpty() || flightClass.isEmpty() || numOfExtraBags.isEmpty();
    }

    private boolean validateNumOfExtraBags() {
        if (numOfExtraBags.isEmpty() || Integer.parseInt(numOfExtraBags) < 0) {
            editNumOfExtraBags.setError("Number of extra bags must be a positive integer.");
            Toast.makeText(getContext(), "Please enter a valid number of extra bags", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}