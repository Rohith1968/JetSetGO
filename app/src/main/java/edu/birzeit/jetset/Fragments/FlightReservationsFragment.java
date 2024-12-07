package edu.birzeit.jetset.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.activities.AdminHomeActivity;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;


public class FlightReservationsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String flightId;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private LinearLayout summaryContainer;
    private String mParam1;
    private String mParam2;

    public FlightReservationsFragment() {
    }

    public static FlightReservationsFragment newInstance(String param1, String param2) {
        FlightReservationsFragment fragment = new FlightReservationsFragment();
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
        return inflater.inflate(R.layout.fragment_flight_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        sharedPrefManager.writeToolbarTitle("Reservations");
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).toolbarTitle.setText(sharedPrefManager.readToolbarTitle());
        }

        if (getArguments() != null) {
            flightId = getArguments().getString("FLIGHT_ID");

            dataBaseHelper = new DataBaseHelper(getContext());
            sharedPrefManager = SharedPrefManager.getInstance(getContext());
            summaryContainer = view.findViewById(R.id.summaryContainer);

            fillReservations();
        }
    }

    @SuppressLint("SetTextI18n")
    private void fillReservations() {
        summaryContainer.removeAllViews();
        Cursor cursor = dataBaseHelper.getReservationsByFlight(flightId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String reservationId = cursor.getString(cursor.getColumnIndexOrThrow("RESERVATION_ID"));
                String flightId = cursor.getString(cursor.getColumnIndexOrThrow("FLIGHT_ID"));
                String passengerName = dataBaseHelper.getPassengerNameFromReservation(reservationId);
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

                    View summaryView = getLayoutInflater().inflate(R.layout.summary_card_admin, summaryContainer, false);

                    TextView textViewPassengerName = summaryView.findViewById(R.id.textViewName);
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


                    textViewPassengerName.setText(passengerName);
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