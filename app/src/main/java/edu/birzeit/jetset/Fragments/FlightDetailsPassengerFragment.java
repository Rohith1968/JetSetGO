package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.PassengerHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;

public class FlightDetailsPassengerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SAVED_EMAIL = "SavedEmail";
    SharedPrefManager sharedPrefManager;
    LinearLayout summaryContainer;
    private String flightId, savedEmail;
    private TextView textViewFlightNumber, textViewAircraftModel, textViewDepartureCity, textViewDepartureDate, textViewDepartureTime,
            textViewArrivalCity, textViewArrivalDate, textViewArrivalTime, textViewBookingOpenDate, textViewDuration, textViewMaxNumOfSeats,
            textViewFrequency, textViewExtraBaggagePrice, textViewEconomyPrice, textViewBusinessPrice;
    private Button reserveButton, cancelButton;
    private DataBaseHelper dataBaseHelper;
    private String mParam1;
    private String mParam2;

    public FlightDetailsPassengerFragment() {
    }

    public static FlightDetailsPassengerFragment newInstance(String param1, String param2) {
        FlightDetailsPassengerFragment fragment = new FlightDetailsPassengerFragment();
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
        return inflater.inflate(R.layout.fragment_flight_details_passenger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Flight Details");
        if (getActivity() instanceof PassengerHomeActivity) {
            ((PassengerHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }
        savedEmail = sharedPrefManager.readString(SAVED_EMAIL, "");


        if (getArguments() != null) {
            flightId = getArguments().getString("FLIGHT_ID");
            dataBaseHelper = new DataBaseHelper(getContext());
            findViews();
            fillViews();

            cancelButton.setOnClickListener(v -> {
                if (dataBaseHelper.cancelReservation(flightId, savedEmail) == 0) {
                    Toast.makeText(getContext(), "Reservation Cancellation Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "Reservation Cancelled", Toast.LENGTH_SHORT).show();

                if (dataBaseHelper.hasPassengerReservedFlight(savedEmail, flightId)) {
                    reserveButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.VISIBLE);
                    summaryContainer.setVisibility(View.VISIBLE);
                } else {
                    reserveButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                    summaryContainer.setVisibility(View.GONE);
                }

            });

            reserveButton.setOnClickListener(v -> openConfirmReservationFragment());

            getParentFragmentManager().setFragmentResultListener("reservationDataUpdated", this, (requestKey, result) -> {
                boolean isUpdated = result.getBoolean("isUpdated", false);
                if (isUpdated) {
                    fillViews();
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

    @Override
    public void onResume() {
        super.onResume();
        fillViews();
    }

    private void findViews() {
        textViewFlightNumber = getView().findViewById(R.id.textViewFlightNumber);
        textViewAircraftModel = getView().findViewById(R.id.textViewAircraftModel);
        textViewDepartureCity = getView().findViewById(R.id.textViewDepartureCity);
        textViewArrivalCity = getView().findViewById(R.id.textViewArrivalCity);
        textViewDepartureDate = getView().findViewById(R.id.textViewDepartureDate);
        textViewDepartureTime = getView().findViewById(R.id.textViewDepartureTime);
        textViewArrivalDate = getView().findViewById(R.id.textViewArrivalDate);
        textViewArrivalTime = getView().findViewById(R.id.textViewArrivalTime);
        textViewBookingOpenDate = getView().findViewById(R.id.textViewBookingOpenDate);
        textViewDuration = getView().findViewById(R.id.textViewDuration);
        textViewMaxNumOfSeats = getView().findViewById(R.id.textViewMaxNumOfSeats);
        textViewExtraBaggagePrice = getView().findViewById(R.id.textViewExtraBaggage);
        textViewEconomyPrice = getView().findViewById(R.id.textViewEconomy);
        textViewBusinessPrice = getView().findViewById(R.id.textViewBusiness);
        textViewFrequency = getView().findViewById(R.id.textViewFrequency);
        reserveButton = getView().findViewById(R.id.buttonReserve);
        cancelButton = getView().findViewById(R.id.buttonCancel);
        summaryContainer = getView().findViewById(R.id.summaryContainer);
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void fillViews() {
        Cursor cursor = dataBaseHelper.getFlightById(flightId);

        if (cursor.moveToFirst()) {
            do {
                textViewFlightNumber.setText(cursor.getString(cursor.getColumnIndex("FLIGHT_NUMBER")));
                textViewAircraftModel.setText(cursor.getString(cursor.getColumnIndex("AIRCRAFT_MODEL")));
                textViewDepartureCity.setText(cursor.getString(cursor.getColumnIndex("DEPARTURE_CITY")));
                textViewArrivalCity.setText(cursor.getString(cursor.getColumnIndex("DESTINATION_CITY")));

                String departureDateTime = cursor.getString(cursor.getColumnIndex("DEPARTURE_DATETIME"));
                String[] departureParts = departureDateTime.split(" ");
                if (departureParts.length == 2) {
                    textViewDepartureDate.setText(departureParts[0]);
                    textViewDepartureTime.setText(departureParts[1]);
                }

                String arrivalDateTime = cursor.getString(cursor.getColumnIndex("ARRIVAL_DATETIME"));
                String[] arrivalParts = arrivalDateTime.split(" ");
                if (arrivalParts.length == 2) {
                    textViewArrivalDate.setText(arrivalParts[0]);
                    textViewArrivalTime.setText(arrivalParts[1]);
                }

                textViewBookingOpenDate.setText(cursor.getString(cursor.getColumnIndex("BOOKING_OPEN_DATE")));
                textViewDuration.setText(cursor.getString(cursor.getColumnIndex("DURATION")));
                textViewMaxNumOfSeats.setText(cursor.getString(cursor.getColumnIndex("MAX_SEATS")));
                textViewExtraBaggagePrice.setText("$" + cursor.getString(cursor.getColumnIndex("PRICE_EXTRA_BAGGAGE")));
                textViewEconomyPrice.setText("$" + cursor.getString(cursor.getColumnIndex("PRICE_ECONOMY")));
                textViewBusinessPrice.setText("$" + cursor.getString(cursor.getColumnIndex("PRICE_BUSINESS")));
                textViewFrequency.setText(cursor.getString(cursor.getColumnIndex("IS_RECURRENT")));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Calendar currentDateTime = Calendar.getInstance();

                try {
                    Calendar departureDateTimeCal = Calendar.getInstance();
                    departureDateTimeCal.setTime(sdf.parse(departureDateTime));

                    if (currentDateTime.after(departureDateTimeCal)) {
                        cancelButton.setText("Flight can't be booked");
                        cancelButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.blueGray));
                        cancelButton.setEnabled(false);
                        reserveButton.setVisibility(View.GONE);
                    } else {
                        if (dataBaseHelper.hasPassengerReservedFlight(savedEmail, flightId)) {
                            reserveButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.VISIBLE);
                            summaryContainer.setVisibility(View.VISIBLE);
                            fillSummary();
                        } else {
                            reserveButton.setVisibility(View.VISIBLE);
                            cancelButton.setVisibility(View.GONE);
                            summaryContainer.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        dataBaseHelper.close();
    }


    private void openConfirmReservationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("FLIGHT_ID", flightId);
        bundle.putString("PASSENGER_EMAIL", savedEmail);

        ConfirmReservationFragment reservationFragment = new ConfirmReservationFragment();
        reservationFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, reservationFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @SuppressLint("Range")
    private void fillSummary() {
        summaryContainer.removeAllViews();

        Cursor cursor = dataBaseHelper.getReservationByEmailAndFlight(sharedPrefManager.readString(SAVED_EMAIL, ""), flightId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));
                String flightId = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_ID"));
                Cursor flightCursor = dataBaseHelper.getFlightById(flightId);
                if (flightCursor != null && flightCursor.moveToFirst()) {
                    String flightNumber = flightCursor.getString(flightCursor.getColumnIndexOrThrow("FLIGHT_NUMBER"));
                    String departureCity = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DEPARTURE_CITY"));
                    String destinationCity = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DESTINATION_CITY"));
                    String departureDateTime = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DEPARTURE_DATETIME"));
                    String arrivalDateTime = flightCursor.getString(flightCursor.getColumnIndexOrThrow("ARRIVAL_DATETIME"));
                    String duration = flightCursor.getString(flightCursor.getColumnIndexOrThrow("DURATION"));

                    String flightClass = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_CLASS"));
                    String foodPreference = cursor.getString(cursor.getColumnIndexOrThrow("FOOD_PREFERENCE"));
                    String numOfExtraBags = cursor.getString(cursor.getColumnIndexOrThrow("NUM_EXTRA_BAGS"));
                    String totalPrice = cursor.getString(cursor.getColumnIndexOrThrow("PRICE"));

                    View summaryView = getLayoutInflater().inflate(R.layout.summary_card, summaryContainer, false);

                    TextView textViewFlightNumber = summaryView.findViewById(R.id.textViewFlightNumber);
                    TextView textViewFrom = summaryView.findViewById(R.id.textViewFrom);
                    TextView textViewTo = summaryView.findViewById(R.id.textViewTo);
                    TextView textViewDepartureDate = summaryView.findViewById(R.id.textViewDepartureDate);
                    TextView textViewArrivalDate = summaryView.findViewById(R.id.textViewArrivalDate);
                    TextView textViewDepartureTime = summaryView.findViewById(R.id.textViewDepartureTime);
                    TextView textViewArrivalTime = summaryView.findViewById(R.id.textViewArrivalTime);
                    TextView textViewDuration = summaryView.findViewById(R.id.textViewDuration);
                    TextView textViewClass = summaryView.findViewById(R.id.textViewClass);
                    TextView textViewBags = summaryView.findViewById(R.id.textViewBags);
                    TextView textViewFood = summaryView.findViewById(R.id.textViewFood);
                    TextView textViewPrice = summaryView.findViewById(R.id.textViewPrice);
                    TextView textViewFlightID = summaryView.findViewById(R.id.textViewFlightID);
                    TextView textViewReservationID = summaryView.findViewById(R.id.textViewReservationID);

                    textViewFlightNumber.setText(flightNumber);
                    textViewFrom.setText(departureCity);
                    textViewTo.setText(destinationCity);
                    textViewDepartureDate.setText(departureDateTime.split(" ")[0]);
                    textViewArrivalDate.setText(arrivalDateTime.split(" ")[0]);
                    textViewDepartureTime.setText(departureDateTime.split(" ")[1]);
                    textViewArrivalTime.setText(arrivalDateTime.split(" ")[1]);
                    textViewDuration.setText(duration);
                    textViewClass.setText(flightClass);
                    textViewBags.setText(numOfExtraBags);
                    textViewFood.setText(foodPreference);
                    textViewPrice.setText("$" + totalPrice);
                    textViewFlightID.setText(flightId);
                    textViewReservationID.setText(reservationId);

                    summaryView.setTag(flightId);

                    ImageView planeImageView = summaryView.findViewById(R.id.plane);
                    Animation planeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.plane_animation);
                    planeImageView.startAnimation(planeAnimation);

                    summaryContainer.addView(summaryView);
                }
                flightCursor.close();
            } while (cursor.moveToNext());
            cursor.close();
        }
        dataBaseHelper.close();


    }
}