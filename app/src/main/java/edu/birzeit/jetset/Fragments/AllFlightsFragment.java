package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.AdminHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllFlightsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllFlightsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String USER_ROLE = "UserRole";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SharedPrefManager sharedPrefManager;
    private DataBaseHelper dataBaseHelper;
    private LinearLayout cardContainer;
    private Button buttonFilter;
    private TextInputEditText editDepartureCity, editArrivalCity, editDepartureDate, editArrivalDate, editReturnDate;
    private String departureCity, arrivalCity;
    private Calendar departureDate, arrivalDate, returnDate;
    private RadioButton radioOneWay, radioRoundTrip;
    private RadioGroup radioGroup;
    private boolean isOneWay;
    private String mParam1;
    private String mParam2;

    public AllFlightsFragment() {
    }

    public static AllFlightsFragment newInstance(String param1, String param2) {
        AllFlightsFragment fragment = new AllFlightsFragment();
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
        return inflater.inflate(R.layout.fragment_all_flights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataBaseHelper = new DataBaseHelper(getContext());

        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("All Flights");
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }

        findAndSetupViews();
        radioOneWay.setOnClickListener(v -> {
            radioRoundTrip.setChecked(false);
            editReturnDate.setEnabled(false);
            editReturnDate.setText("");
            isOneWay = true;
        });
        radioRoundTrip.setOnClickListener(v -> {
            radioOneWay.setChecked(false);
            editReturnDate.setEnabled(true);
            isOneWay = false;
        });
        fillFlights();

        buttonFilter.setOnClickListener(v -> {
            if (!getData()) {
                return;
            }
            fillFlights();
        });

        getParentFragmentManager().setFragmentResultListener("flightDataUpdated", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                fillFlights();
            }
        });
    }

    private boolean isAllDataEmpty() {
        return departureCity.isEmpty() &&
                arrivalCity.isEmpty() &&
                editDepartureDate.getText().toString().isEmpty() &&
                editReturnDate.getText().toString().isEmpty() &&
                editArrivalDate.getText().toString().isEmpty();
    }

    private boolean getData() {
        if (editDepartureCity.getText().toString().isEmpty())
            departureCity = "";
        else
            departureCity = editDepartureCity.getText().toString();

        if (editArrivalCity.getText().toString().isEmpty())
            arrivalCity = "";
        else
            arrivalCity = editArrivalCity.getText().toString();

        try {
            if (!editDepartureDate.getText().toString().isEmpty()) {
                if (departureDate == null) {
                    departureDate = Calendar.getInstance();
                }
                departureDate.setTime(dateFormat.parse(editDepartureDate.getText().toString()));
            } else {
                departureDate = null;
            }

            if (!editArrivalDate.getText().toString().isEmpty()) {
                if (arrivalDate == null) {
                    arrivalDate = Calendar.getInstance();
                }
                arrivalDate.setTime(dateFormat.parse(editArrivalDate.getText().toString()));
            } else {
                arrivalDate = null;
            }

            if (!editReturnDate.getText().toString().isEmpty()) {
                if (returnDate == null) {
                    returnDate = Calendar.getInstance();
                }
                returnDate.setTime(dateFormat.parse(editReturnDate.getText().toString()));
            } else {
                returnDate = null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Flight", "getData: " + e.getMessage());
        }

        return true;
    }

    private void findAndSetupViews() {
        editDepartureCity = getView().findViewById(R.id.editDepartureCity);
        editArrivalCity = getView().findViewById(R.id.editArrivalCity);
        editDepartureDate = getView().findViewById(R.id.editDepartureDate);
        editArrivalDate = getView().findViewById(R.id.editArrivalDate);
        editReturnDate = getView().findViewById(R.id.editReturnDate);
        cardContainer = getView().findViewById(R.id.cardContainer);
        buttonFilter = getView().findViewById(R.id.buttonFilter);
        radioOneWay = getView().findViewById(R.id.radio_oneWay);
        radioGroup = getView().findViewById(R.id.radioGroup);
        radioRoundTrip = getView().findViewById(R.id.radio_round);

        if (sharedPrefManager.readString(USER_ROLE, "").equals("Admin")) {
            editArrivalDate.setVisibility(View.VISIBLE);
            editReturnDate.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        } else {
            editArrivalDate.setVisibility(View.GONE);
            editReturnDate.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            radioOneWay.setChecked(true);
            editReturnDate.setEnabled(false);
        }

        departureCity = "";
        arrivalCity = "";
        departureDate = Calendar.getInstance();
        arrivalDate = Calendar.getInstance();
        returnDate = Calendar.getInstance();

        setupDatePickers(editDepartureDate);
        setupDatePickers(editArrivalDate);
        setupDatePickers(editReturnDate);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDatePickers(TextInputEditText dateInput) {
        dateInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dateInput.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                v.performClick();
            }
            return false;

        });
    }

    @SuppressLint("SetTextI18n")
    public void fillFlights() {
        cardContainer.removeAllViews();
        Cursor cursor;

        String userRole = sharedPrefManager.readString(USER_ROLE, "");
        boolean isAdmin = userRole.equals("Admin");
        String departureDateString = (departureDate != null) ? dateFormat.format(departureDate.getTime()) : "";
        String arrivalDateString = (arrivalDate != null) ? dateFormat.format(arrivalDate.getTime()) : "";
        String returnDateString = (returnDate != null) ? dateFormat.format(returnDate.getTime()) : "";
        isOneWay = radioOneWay.isChecked();


        if (isAllDataEmpty())
            cursor = dataBaseHelper.getAllFlights();
        else if (isAdmin)
            cursor = dataBaseHelper.getFlightsByCityAndDate(departureCity, arrivalCity, departureDateString, arrivalDateString);
        else if (isOneWay)
            cursor = dataBaseHelper.getFlightsByCityAndDepartureDate(departureCity, arrivalCity, departureDateString);
        else
            cursor = dataBaseHelper.getRoundTripFlights(departureCity, arrivalCity, departureDateString, returnDateString);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                String flightId = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_ID"));
                String flightNumber = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                String departureCity = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_CITY"));
                String destinationCity = cursor.getString(cursor.getColumnIndexOrThrow("DESTINATION_CITY"));
                String departureDateTime = cursor.getString(cursor.getColumnIndexOrThrow("DEPARTURE_DATETIME"));
                String arrivalDateTime = cursor.getString(cursor.getColumnIndexOrThrow("ARRIVAL_DATETIME"));

                View cardView = getLayoutInflater().inflate(R.layout.flight_card, cardContainer, false);

                TextView textViewFlightNumber = cardView.findViewById(R.id.textViewFlightNumber);
                TextView textViewFrom = cardView.findViewById(R.id.textViewFrom);
                TextView textViewDeparture = cardView.findViewById(R.id.textViewDeparture);
                TextView textViewTo = cardView.findViewById(R.id.textViewTo);
                TextView textViewArrival = cardView.findViewById(R.id.textViewArrival);
                TextView textViewFlightID = cardView.findViewById(R.id.textViewFlightID);

                textViewFlightNumber.setText(flightNumber);
                textViewFrom.setText(departureCity);
                textViewDeparture.setText("Departure on " + departureDateTime);
                textViewTo.setText(destinationCity);
                textViewArrival.setText("Arrival on " + arrivalDateTime);
                textViewFlightID.setText(flightId);

                cardView.setTag(flightId);

                cardView.setOnClickListener(v -> {
                    if (sharedPrefManager.readString(USER_ROLE, "").equals("Admin"))
                        openFlightDetailsAdminFragment((String) v.getTag());
                    else
                        openFlightDetailsPassengerFragment((String) v.getTag());
                });
                cardContainer.addView(cardView);
            } while (cursor.moveToNext());
            cursor.close();
        }
        dataBaseHelper.close();
    }

    private void openFlightDetailsAdminFragment(String flightId) {
        Bundle bundle = new Bundle();
        bundle.putString("FLIGHT_ID", flightId);

        FlightDetailsAdminFragment detailsFragment = new FlightDetailsAdminFragment();
        detailsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openFlightDetailsPassengerFragment(String flightId) {
        Bundle bundle = new Bundle();
        bundle.putString("FLIGHT_ID", flightId);

        FlightDetailsPassengerFragment detailsFragment = new FlightDetailsPassengerFragment();
        detailsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}