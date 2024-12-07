package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.AdminHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;
import edu.birzeit.jetset.model.Flight;
import edu.birzeit.jetset.utils.CustomArrayAdapter;

public class FlightDetailsAdminFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SharedPrefManager sharedPrefManager;
    private String flightId;
    private TextInputEditText editFlightNumber, editAircraftModel, editDepartureCity, editDepartureDate, editDepartureTime,
            editDestinationCity, editArrivalDate, editArrivalTime, editBookingOpenDate, editDuration, editMaxNumOfSeats,
            editExtraBaggagePrice, editEconomyPrice, editBusinessPrice;
    private TextView currentReservations, missedReservations;
    private Spinner spinnerIsRecurrent;
    private Calendar departureDateTime, arrivalDateTime, bookingOpenDateTime, durationTime;
    private String flightNumber, aircraftModel, departureCity, arrivalCity, isRecurrentStr;
    private int maxNumOfSeats;
    private double extraBaggagePrice, economyPrice, businessPrice;
    private Button saveButton, deleteButton;
    private DataBaseHelper dataBaseHelper;
    private Flight flight;
    private String mParam1;
    private String mParam2;

    public FlightDetailsAdminFragment() {
    }

    public static FlightDetailsAdminFragment newInstance(String param1, String param2) {
        FlightDetailsAdminFragment fragment = new FlightDetailsAdminFragment();
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
        return inflater.inflate(R.layout.fragment_flight_details_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Flight Details");
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }


        if (getArguments() != null) {
            flightId = getArguments().getString("FLIGHT_ID");
            dataBaseHelper = new DataBaseHelper(getContext());
            findViews();
            setupSpinnersAndDates();
            fillViews();

            deleteButton.setOnClickListener(v -> {
                dataBaseHelper.deleteFlight(flightId);
                Toast.makeText(getContext(), "Flight Deleted Successfully", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("flightDataUpdated", result);
                    getActivity().onBackPressed();
                }, 1500);
            });

            saveButton.setOnClickListener(v -> {
                if (!getData())
                    return;
                if (!validateFlightForm())
                    return;

                flight = new Flight();
                createFlight(flight, flightId, flightNumber, aircraftModel, departureCity, arrivalCity, bookingOpenDateTime.getTime(),
                             departureDateTime.getTime(), arrivalDateTime.getTime(), durationTime.getTime(), maxNumOfSeats, extraBaggagePrice, economyPrice, businessPrice, isRecurrentStr);

                dataBaseHelper.updateFlight(flight);
                Toast.makeText(getContext(), "Flight Updated Successfully", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("flightDataUpdated", result);
                    getActivity().onBackPressed();
                }, 1500);
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

    private void createFlight(Flight flight, String flightId, String flightNumber, String aircraftModel, String departureCity, String arrivalCity,
                              Date bookingOpenDate, Date departureDateTime, Date arrivalDateTime, Date durationTime, int maxNumOfSeats, double extraBaggagePrice,
                              double economyPrice, double businessPrice, String isRecurrent) {

        flight.setFlightId(Integer.parseInt(flightId));
        flight.setFlightNumber(flightNumber);
        flight.setAircraftModel(aircraftModel);
        flight.setDepartureCity(departureCity);
        flight.setDestinationCity(arrivalCity);
        flight.setBookingOpenDate(dateFormat.format(bookingOpenDate));
        flight.setDepartureDateTime(dateTimeFormat.format(departureDateTime));
        flight.setArrivalDateTime(dateTimeFormat.format(arrivalDateTime));
        flight.setDuration(timeFormat.format(durationTime));
        flight.setMaxSeats(maxNumOfSeats);
        flight.setPriceExtraBaggage(extraBaggagePrice);
        flight.setPriceEconomy(economyPrice);
        flight.setPriceBusiness(businessPrice);
        flight.setIsRecurrent(isRecurrent);
    }

    private boolean validateFlightForm() {
        return validateFlightNumber() &&
                validateAircraftModel() &&
                validateCity(departureCity, editDepartureCity) &&
                validateCity(arrivalCity, editDestinationCity) &&
                validateDate(editDepartureDate, "Departure Date") &&
                validateDate(editArrivalDate, "Arrival Date") &&
                validateDate(editBookingOpenDate, "Booking Open Date") &&
                validateDateSequence(departureDateTime, arrivalDateTime, editArrivalDate) &&
                validateTime(editDepartureTime, "Departure Time") &&
                validateTime(editArrivalTime, "Arrival Time") &&
                validateMaxNumOfSeats() &&
                validatePrice(extraBaggagePrice, editExtraBaggagePrice) &&
                validatePrice(economyPrice, editEconomyPrice) &&
                validatePrice(businessPrice, editBusinessPrice);
    }

    private boolean validateFlightNumber() {
        if (flightNumber.isEmpty()) {
            editFlightNumber.setError("Flight number is required.");
            Toast.makeText(getContext(), "Please enter a valid flight number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!flightNumber.matches("^[A-Za-z0-9]{3,6}$")) {
            editFlightNumber.setError("Invalid flight number. It should be 3-6 alphanumeric characters.");
            Toast.makeText(getContext(), "Please enter a valid flight number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAircraftModel() {
        if (aircraftModel.isEmpty()) {
            editAircraftModel.setError("Aircraft model is required.");
            Toast.makeText(getContext(), "Please enter a valid aircraft model", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateCity(String city, EditText editText) {
        if (city.isEmpty()) {
            editText.setError("City is required.");
            Toast.makeText(getContext(), "Please enter a valid city", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!city.matches("^[a-zA-Z\\s]{2,}$")) {
            editText.setError("Invalid city name. Use letters only.");
            Toast.makeText(getContext(), "Please enter a valid city", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateDate(EditText editText, String fieldName) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(fieldName + " is required.");
            Toast.makeText(getContext(), "Please select a valid " + fieldName, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateTime(EditText editText, String fieldName) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(fieldName + " is required.");
            Toast.makeText(getContext(), "Please select a valid " + fieldName, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateDateSequence(Calendar startDateTime, Calendar endDateTime, EditText endEditText) {
        if (startDateTime != null && endDateTime != null && endDateTime.getTimeInMillis() <= startDateTime.getTimeInMillis()) {
            endEditText.setError("End date should be after start date.");
            Toast.makeText(getContext(), "Please ensure the end date is after the start date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateMaxNumOfSeats() {
        if (maxNumOfSeats == 0) {
            editMaxNumOfSeats.setError("Maximum number of seats is required.");
            Toast.makeText(getContext(), "Please enter a valid number of seats", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!String.valueOf(maxNumOfSeats).matches("\\d+")) {
            editMaxNumOfSeats.setError("Invalid number of seats. Use digits only.");
            Toast.makeText(getContext(), "Please enter a valid number of seats", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePrice(Double price, EditText editText) {
        if (price == null) {
            editText.setError("Price is required.");
            Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!price.toString().matches("\\d+(\\.\\d{1,2})?")) {
            editText.setError("Invalid price. Use digits and up to 2 decimal places.");
            Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void findViews() {
        editFlightNumber = getView().findViewById(R.id.editFlightNumber);
        editAircraftModel = getView().findViewById(R.id.editAircraftModel);
        editDepartureCity = getView().findViewById(R.id.editDepartureCity);
        editDestinationCity = getView().findViewById(R.id.editDestinationCity);
        editDepartureDate = getView().findViewById(R.id.editDepartureDate);
        editDepartureTime = getView().findViewById(R.id.editDepartureTime);
        editArrivalDate = getView().findViewById(R.id.editArrivalDate);
        editArrivalTime = getView().findViewById(R.id.editArrivalTime);
        editBookingOpenDate = getView().findViewById(R.id.editBookingOpenDate);
        editDuration = getView().findViewById(R.id.editDuration);
        editMaxNumOfSeats = getView().findViewById(R.id.editMaxNumOfSeats);
        editExtraBaggagePrice = getView().findViewById(R.id.editExtraBaggagePrice);
        editEconomyPrice = getView().findViewById(R.id.editEconomyPrice);
        editBusinessPrice = getView().findViewById(R.id.editBusinessPrice);
        spinnerIsRecurrent = getView().findViewById(R.id.spinnerIsRecurrent);
        currentReservations = getView().findViewById(R.id.currentReservations);
        missedReservations = getView().findViewById(R.id.missedReservations);
        saveButton = getView().findViewById(R.id.buttonSave);
        deleteButton = getView().findViewById(R.id.buttonDelete);
    }

    @SuppressLint("Range")
    private void fillViews() {
        Cursor cursor = dataBaseHelper.getFlightById(flightId);
        if (cursor.moveToFirst()) {
            do {
                editFlightNumber.setText(cursor.getString(cursor.getColumnIndex("FLIGHT_NUMBER")));
                editAircraftModel.setText(cursor.getString(cursor.getColumnIndex("AIRCRAFT_MODEL")));
                editDepartureCity.setText(cursor.getString(cursor.getColumnIndex("DEPARTURE_CITY")));
                editDestinationCity.setText(cursor.getString(cursor.getColumnIndex("DESTINATION_CITY")));

                String departureDateTime = cursor.getString(cursor.getColumnIndex("DEPARTURE_DATETIME"));
                String[] departureParts = departureDateTime.split(" ");
                if (departureParts.length == 2) {
                    editDepartureDate.setText(departureParts[0]);
                    editDepartureTime.setText(departureParts[1]);
                }

                String arrivalDateTime = cursor.getString(cursor.getColumnIndex("ARRIVAL_DATETIME"));
                String[] arrivalParts = arrivalDateTime.split(" ");
                if (arrivalParts.length == 2) {
                    editArrivalDate.setText(arrivalParts[0]);
                    editArrivalTime.setText(arrivalParts[1]);
                }

                editBookingOpenDate.setText(cursor.getString(cursor.getColumnIndex("BOOKING_OPEN_DATE")));
                editDuration.setText(cursor.getString(cursor.getColumnIndex("DURATION")));
                editMaxNumOfSeats.setText(cursor.getString(cursor.getColumnIndex("MAX_SEATS")));
                editExtraBaggagePrice.setText(cursor.getString(cursor.getColumnIndex("PRICE_EXTRA_BAGGAGE")));
                editEconomyPrice.setText(cursor.getString(cursor.getColumnIndex("PRICE_ECONOMY")));
                editBusinessPrice.setText(cursor.getString(cursor.getColumnIndex("PRICE_BUSINESS")));
                spinnerIsRecurrent.setSelection(cursor.getInt(cursor.getColumnIndex("IS_RECURRENT")));
                currentReservations.setText(cursor.getString(cursor.getColumnIndex("CURRENT_RESERVATIONS")));
                missedReservations.setText(cursor.getString(cursor.getColumnIndex("MISSED_FLIGHTS")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private boolean getData() {
        flightNumber = editFlightNumber.getText().toString();
        aircraftModel = editAircraftModel.getText().toString();
        departureCity = editDepartureCity.getText().toString();
        arrivalCity = editDestinationCity.getText().toString();
        maxNumOfSeats = Integer.parseInt(editMaxNumOfSeats.getText().toString());
        extraBaggagePrice = Double.parseDouble(editExtraBaggagePrice.getText().toString());
        economyPrice = Double.parseDouble(editEconomyPrice.getText().toString());
        businessPrice = Double.parseDouble(editBusinessPrice.getText().toString());
        isRecurrentStr = spinnerIsRecurrent.getSelectedItem().toString();

        try {
            bookingOpenDateTime = Calendar.getInstance();
            bookingOpenDateTime.setTime(dateFormat.parse(editBookingOpenDate.getText().toString()));

            departureDateTime = Calendar.getInstance();
            departureDateTime.setTime(dateTimeFormat.parse(editDepartureDate.getText().toString() + " " + editDepartureTime.getText().toString()));

            arrivalDateTime = Calendar.getInstance();
            arrivalDateTime.setTime(dateTimeFormat.parse(editArrivalDate.getText().toString() + " " + editArrivalTime.getText().toString()));

            durationTime = Calendar.getInstance();
            durationTime.setTime(timeFormat.parse(editDuration.getText().toString()));

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Flight", "getData: " + e.getMessage());
        }


        if (isDataEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields with valid data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDataEmpty() {
        return flightNumber.isEmpty() || aircraftModel.isEmpty() || departureCity.isEmpty() || arrivalCity.isEmpty() ||
                maxNumOfSeats == 0 || extraBaggagePrice == 0.0 || economyPrice == 0.0 || businessPrice == 0.0 ||
                isRecurrentStr.isEmpty() || bookingOpenDateTime == null || departureDateTime == null || arrivalDateTime == null;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSpinnersAndDates() {
        String[] frequency = {"Not Recurrent", "Daily", "Weekly"};
        ArrayList<String> freqList = new ArrayList<>(List.of(frequency));
        CustomArrayAdapter adapter = new CustomArrayAdapter(getContext(), R.layout.spinner_item, freqList, 14);
        spinnerIsRecurrent.setAdapter(adapter);

        setupDatePickers(editBookingOpenDate);
        setupDateAndTimePickers(editDepartureDate, editDepartureTime, departureDateTime);
        setupDateAndTimePickers(editArrivalDate, editArrivalTime, arrivalDateTime);
        setupTimePickers(editDuration);
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

    @SuppressLint("ClickableViewAccessibility")
    private void setupDateAndTimePickers(TextInputEditText dateInput, TextInputEditText timeInput, Calendar dateTime) {
        if (dateTime == null) {
            dateTime = Calendar.getInstance();
        }
        Calendar finalDateTime = dateTime;
        dateInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dateInput.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime()));
                    finalDateTime.set(year, month, dayOfMonth);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                v.performClick();
            }
            return false;
        });

        timeInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    timeInput.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime()));
                    finalDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    finalDateTime.set(Calendar.MINUTE, minute);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                v.performClick();
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTimePickers(TextInputEditText timeInput) {
        timeInput.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    timeInput.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                v.performClick();
            }
            return false;
        });
    }
}