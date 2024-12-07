package edu.birzeit.jetset.tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.birzeit.jetset.model.Flight;

public class FlightJsonParser {

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static List<Flight> getObjectFromJson(String json) {
        List<Flight> flights = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                Flight flight = new Flight();
                flight.setFlightId(0); //temporary
                flight.setFlightNumber(jsonObject.getString("flight_number"));
                flight.setDepartureCity(jsonObject.getString("departure_city"));
                flight.setDestinationCity(jsonObject.getString("destination_city"));

                Date departureDate = dateTimeFormat.parse(jsonObject.getString("departure_date") + " " + jsonObject.getString("departure_time"));
                flight.setDepartureDateTime(dateTimeFormat.format(departureDate));

                Date arrivalDate = dateTimeFormat.parse(jsonObject.getString("arrival_date") + " " + jsonObject.getString("arrival_time"));
                flight.setArrivalDateTime(dateTimeFormat.format(arrivalDate));

                flight.setDuration(jsonObject.getString("duration"));
                flight.setAircraftModel(jsonObject.getString("aircraft_model"));
                flight.setCurrentReservations(jsonObject.getInt("current_reservations"));
                flight.setMaxSeats(jsonObject.getInt("maximum_seats"));
                flight.setMissedFlights(jsonObject.getInt("missed_flight_count"));

                Date bookingOpenDate = dateFormat.parse(jsonObject.getString("booking_open_date"));
                flight.setBookingOpenDate(dateFormat.format(bookingOpenDate));

                flight.setPriceEconomy(jsonObject.getDouble("price_economy"));
                flight.setPriceBusiness(jsonObject.getDouble("price_business"));
                flight.setPriceExtraBaggage(jsonObject.getDouble("price_extra_baggage"));
                flight.setIsRecurrent(jsonObject.getString("is_recurrent"));

                flights.add(flight);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
        return flights;
    }
}
