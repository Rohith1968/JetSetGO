package edu.birzeit.jetset.database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.birzeit.jetset.model.Admin;
import edu.birzeit.jetset.model.Flight;
import edu.birzeit.jetset.model.Passenger;
import edu.birzeit.jetset.model.Reservation;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "JJetSet.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(" +
                                       "EMAIL TEXT PRIMARY KEY, " +
                                       "PHONE TEXT, " +
                                       "FIRST_NAME TEXT, " +
                                       "LAST_NAME TEXT, " +
                                       "HASHED_PASSWORD TEXT, " +
                                       "ROLE TEXT, " +  // Admin or Passenger
                                       "PASSPORT_NUMBER TEXT, " +
                                       "PASSPORT_ISSUE_DATE TEXT, " +
                                       "PASSPORT_ISSUE_PLACE TEXT, " +
                                       "PASSPORT_EXPIRATION_DATE TEXT, " +
                                       "FOOD_PREFERENCE TEXT, " +
                                       "DATE_OF_BIRTH TEXT, " +
                                       "NATIONALITY TEXT" +
                                       ");");

        sqLiteDatabase.execSQL("CREATE TABLE FLIGHT(" +
                                       "FLIGHT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                       "FLIGHT_NUMBER TEXT," +
                                       "DEPARTURE_CITY TEXT," +
                                       "DESTINATION_CITY TEXT," +
                                       "DEPARTURE_DATETIME TEXT," +
                                       "ARRIVAL_DATETIME TEXT," +
                                       "DURATION TEXT," +
                                       "AIRCRAFT_MODEL TEXT," +
                                       "CURRENT_RESERVATIONS INTEGER," +
                                       "MAX_SEATS INTEGER," +
                                       "MISSED_FLIGHTS INTEGER," +
                                       "BOOKING_OPEN_DATE TEXT," +
                                       "PRICE_ECONOMY REAL," +
                                       "PRICE_BUSINESS REAL," +
                                       "PRICE_EXTRA_BAGGAGE REAL," +
                                       "IS_RECURRENT TEXT" +
                                       ");");

        sqLiteDatabase.execSQL("CREATE TABLE RESERVATION(" +
                                       "RESERVATION_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                       "FLIGHT_ID TEXT, " +
                                       "PASSENGER_EMAIL TEXT, " +
                                       "FLIGHT_CLASS TEXT, " +
                                       "NUM_EXTRA_BAGS INTEGER, " +
                                       "FOOD_PREFERENCE TEXT, " +
                                       "PRICE REAL," +
                                       "FOREIGN KEY(FLIGHT_ID) REFERENCES FLIGHT(FLIGHT_ID), " +
                                       "FOREIGN KEY(PASSENGER_EMAIL) REFERENCES USER(EMAIL)" +
                                       ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USER");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS FLIGHT");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS RESERVATION");
        onCreate(sqLiteDatabase);
    }

    public void insertAdmin(Admin admin) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", admin.getEmail());
        contentValues.put("PHONE", admin.getPhoneNumber());
        contentValues.put("FIRST_NAME", admin.getFirstName());
        contentValues.put("LAST_NAME", admin.getLastName());
        contentValues.put("HASHED_PASSWORD", admin.getHashedPassword());
        contentValues.put("ROLE", "Admin");

        sqLiteDatabase.insert("USER", null, contentValues);
    }

    public void insertPassenger(Passenger passenger) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", passenger.getEmail());
        contentValues.put("PHONE", passenger.getPhoneNumber());
        contentValues.put("FIRST_NAME", passenger.getFirstName());
        contentValues.put("LAST_NAME", passenger.getLastName());
        contentValues.put("HASHED_PASSWORD", passenger.getHashedPassword());
        contentValues.put("ROLE", "Passenger");
        contentValues.put("PASSPORT_NUMBER", passenger.getPassportNumber());
        contentValues.put("PASSPORT_ISSUE_DATE", passenger.getPassportIssueDate());
        contentValues.put("PASSPORT_ISSUE_PLACE", passenger.getPassportIssuePlace());
        contentValues.put("PASSPORT_EXPIRATION_DATE", passenger.getPassportExpiryDate());
        contentValues.put("FOOD_PREFERENCE", passenger.getFoodPreference());
        contentValues.put("DATE_OF_BIRTH", passenger.getDateOfBirth());
        contentValues.put("NATIONALITY", passenger.getNationality());

        sqLiteDatabase.insert("USER", null, contentValues);
    }

    public int insertFlight(Flight flight) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("FLIGHT_NUMBER", flight.getFlightNumber());
        contentValues.put("DEPARTURE_CITY", flight.getDepartureCity());
        contentValues.put("DESTINATION_CITY", flight.getDestinationCity());
        contentValues.put("DEPARTURE_DATETIME", flight.getDepartureDateTime());
        contentValues.put("ARRIVAL_DATETIME", flight.getArrivalDateTime());
        contentValues.put("DURATION", flight.getDuration());
        contentValues.put("AIRCRAFT_MODEL", flight.getAircraftModel());
        contentValues.put("CURRENT_RESERVATIONS", flight.getCurrentReservations());
        contentValues.put("MAX_SEATS", flight.getMaxSeats());
        contentValues.put("MISSED_FLIGHTS", flight.getMissedFlights());
        contentValues.put("BOOKING_OPEN_DATE", flight.getBookingOpenDate());
        contentValues.put("PRICE_ECONOMY", flight.getPriceEconomy());
        contentValues.put("PRICE_BUSINESS", flight.getPriceBusiness());
        contentValues.put("PRICE_EXTRA_BAGGAGE", flight.getPriceExtraBaggage());
        contentValues.put("IS_RECURRENT", flight.getIsRecurrent());

        long id = sqLiteDatabase.insert("FLIGHT", null, contentValues);
        sqLiteDatabase.close();

        return (int) id;
    }

    public int insertReservation(Reservation reservation) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FLIGHT_ID", reservation.getFlightId());
        contentValues.put("PASSENGER_EMAIL", reservation.getPassengerEmail());
        contentValues.put("FLIGHT_CLASS", reservation.getFlightClass());
        contentValues.put("NUM_EXTRA_BAGS", reservation.getNumOfExtraBags());
        contentValues.put("FOOD_PREFERENCE", reservation.getFoodPreference());
        contentValues.put("PRICE", reservation.getTotalPrice());

        return (int) sqLiteDatabase.insert("RESERVATION", null, contentValues);
    }

    public double calculateTotalPrice(int flightId, String flightClass, int numOfExtraBags) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        double totalPrice = 0.0;

        String query = "SELECT PRICE_ECONOMY, PRICE_BUSINESS, PRICE_EXTRA_BAGGAGE FROM FLIGHT WHERE FLIGHT_ID = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{String.valueOf(flightId)});

        if (cursor != null && cursor.moveToFirst()) {
            double priceEconomy = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE_ECONOMY"));
            double priceBusiness = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE_BUSINESS"));
            double priceExtraBaggage = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE_EXTRA_BAGGAGE"));

            if (flightClass.equalsIgnoreCase("Economy")) totalPrice = priceEconomy;
            else if (flightClass.equalsIgnoreCase("Business")) totalPrice = priceBusiness;
            totalPrice += (priceExtraBaggage * numOfExtraBags);
        }
        if (cursor != null) cursor.close();
        return totalPrice;
    }


    public int cancelReservation(String flightId, String passengerEmail) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete("RESERVATION", "FLIGHT_ID = ? AND PASSENGER_EMAIL = ?", new String[]{flightId, passengerEmail});
    }

    public String getPassengerNameFromReservation(String reservationId){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT FIRST_NAME, LAST_NAME FROM USER JOIN RESERVATION ON USER.EMAIL = RESERVATION.PASSENGER_EMAIL WHERE RESERVATION.RESERVATION_ID = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{reservationId});

        if (!cursor.moveToFirst()) return "";

        String firstName = cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME"));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME"));
        cursor.close();
        return firstName + " " + lastName;
    }


    public int updateAdmin(Admin admin, String oldEmail) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", admin.getEmail());
        contentValues.put("PHONE", admin.getPhoneNumber());
        contentValues.put("FIRST_NAME", admin.getFirstName());
        contentValues.put("LAST_NAME", admin.getLastName());
        contentValues.put("HASHED_PASSWORD", admin.getHashedPassword());
        contentValues.put("ROLE", "Admin");
        return sqLiteDatabase.update("USER", contentValues, "EMAIL = ?", new String[]{oldEmail});
    }

    public int updatePassenger(Passenger passenger, String oldEmail) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("EMAIL", passenger.getEmail());
        contentValues.put("PHONE", passenger.getPhoneNumber());
        contentValues.put("FIRST_NAME", passenger.getFirstName());
        contentValues.put("LAST_NAME", passenger.getLastName());
        contentValues.put("HASHED_PASSWORD", passenger.getHashedPassword());
        contentValues.put("ROLE", "Passenger");
        contentValues.put("PASSPORT_NUMBER", passenger.getPassportNumber());
        contentValues.put("PASSPORT_ISSUE_DATE", passenger.getPassportIssueDate());
        contentValues.put("PASSPORT_ISSUE_PLACE", passenger.getPassportIssuePlace());
        contentValues.put("PASSPORT_EXPIRATION_DATE", passenger.getPassportExpiryDate());
        contentValues.put("FOOD_PREFERENCE", passenger.getFoodPreference());
        contentValues.put("DATE_OF_BIRTH", passenger.getDateOfBirth());
        contentValues.put("NATIONALITY", passenger.getNationality());

        return sqLiteDatabase.update("USER", contentValues, "EMAIL = ?", new String[]{oldEmail});
    }

    public Cursor getAllFlights() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM FLIGHT " +
                "ORDER BY DEPARTURE_DATETIME ASC ";

        return sqLiteDatabase.rawQuery(query, null);
    }

    public Cursor getFlightsByCityAndDate(String departureCity, String destinationCity, String departureDate, String arrivalDate) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM FLIGHT WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (departureCity != null && !departureCity.isEmpty()) {
            queryBuilder.append(" AND DEPARTURE_CITY = ?");
            args.add(departureCity);
        }

        if (destinationCity != null && !destinationCity.isEmpty()) {
            queryBuilder.append(" AND DESTINATION_CITY = ?");
            args.add(destinationCity);
        }

        if (departureDate != null && !departureDate.isEmpty()) {
            queryBuilder.append(" AND DATE(DEPARTURE_DATETIME) = ?");
            args.add(departureDate);
        }

        if (arrivalDate != null && !arrivalDate.isEmpty()) {
            queryBuilder.append(" AND DATE(ARRIVAL_DATETIME) = ?");
            args.add(arrivalDate);
        }

        return sqLiteDatabase.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));
    }

    public Cursor getFlightsByCityAndDepartureDate(String departureCity, String destinationCity, String departureDate) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM FLIGHT WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (departureCity != null && !departureCity.isEmpty()) {
            queryBuilder.append(" AND DEPARTURE_CITY = ?");
            args.add(departureCity);
        }

        if (destinationCity != null && !destinationCity.isEmpty()) {
            queryBuilder.append(" AND DESTINATION_CITY = ?");
            args.add(destinationCity);
        }

        if (departureDate != null && !departureDate.isEmpty()) {
            queryBuilder.append(" AND DATE(DEPARTURE_DATETIME) = ?");
            args.add(departureDate);
        }

        return sqLiteDatabase.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));
    }

    public Cursor getRoundTripFlight(String departureCity, String destinationCity, String returnDate) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM FLIGHT WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (departureCity != null && !departureCity.isEmpty()) {
            queryBuilder.append(" AND DEPARTURE_CITY = ?");
            args.add(destinationCity);
        }

        if (destinationCity != null && !destinationCity.isEmpty()) {
            queryBuilder.append(" AND DESTINATION_CITY = ?");
            args.add(departureCity);
        }

        if (returnDate != null && !returnDate.isEmpty()) {
            queryBuilder.append(" AND DATE(DEPARTURE_DATETIME) = ?");
            args.add(returnDate);
        }

        return sqLiteDatabase.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));
    }

    public Cursor getRoundTripFlights(String departureCity, String arrivalCity, String departureDate, String returnDate) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT * FROM FLIGHT WHERE " +
                "(DEPARTURE_CITY = ? AND DESTINATION_CITY = ? AND DATE(DEPARTURE_DATETIME) = ?) " +
                "OR (DEPARTURE_CITY = ? AND DESTINATION_CITY = ? AND DATE(DEPARTURE_DATETIME) = ?)";

        return sqLiteDatabase.rawQuery(query, new String[]{
                departureCity, arrivalCity, departureDate,
                arrivalCity, departureCity, returnDate
        });
    }



    public Cursor getFlightById(String flightId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM FLIGHT WHERE FLIGHT_ID =?", new String[]{flightId});
    }

    public Cursor getFlightsOpenForBooking() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        String query = "SELECT * FROM FLIGHT WHERE BOOKING_OPEN_DATE <= ? AND DEPARTURE_DATETIME > ?";
        return db.rawQuery(query, new String[]{currentDateTime, currentDateTime});

    }

    public Cursor getFlightsNotOpenForBooking() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String query = "SELECT * FROM FLIGHT WHERE BOOKING_OPEN_DATE > ?";

        return db.rawQuery(query, new String[]{currentDate});
    }

    public Cursor getFLightsArchive() {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String query = "SELECT * FROM FLIGHT WHERE ARRIVAL_DATETIME < ?";
        return db.rawQuery(query, new String[]{currentDate});
    }

    public Cursor getUsersByEmail(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM USER WHERE  EMAIL =?", new String[]{email});
    }

    public void updateFlight(Flight flight) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FLIGHT_NUMBER", flight.getFlightNumber());
        contentValues.put("DEPARTURE_CITY", flight.getDepartureCity());
        contentValues.put("DESTINATION_CITY", flight.getDestinationCity());
        contentValues.put("DEPARTURE_DATETIME", flight.getDepartureDateTime());
        contentValues.put("ARRIVAL_DATETIME", flight.getArrivalDateTime());
        contentValues.put("DURATION", flight.getDuration());
        contentValues.put("AIRCRAFT_MODEL", flight.getAircraftModel());
        contentValues.put("CURRENT_RESERVATIONS", flight.getCurrentReservations());
        contentValues.put("MAX_SEATS", flight.getMaxSeats());
        contentValues.put("MISSED_FLIGHTS", flight.getMissedFlights());
        contentValues.put("BOOKING_OPEN_DATE", flight.getBookingOpenDate());
        contentValues.put("PRICE_ECONOMY", flight.getPriceEconomy());
        contentValues.put("PRICE_BUSINESS", flight.getPriceBusiness());
        contentValues.put("PRICE_EXTRA_BAGGAGE", flight.getPriceExtraBaggage());
        contentValues.put("IS_RECURRENT", flight.getIsRecurrent());

        sqLiteDatabase.update("FLIGHT", contentValues, "FLIGHT_ID = ?", new String[]{String.valueOf(flight.getFlightId())});
    }

    public void deleteFlight(String flightId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("FLIGHT", "FLIGHT_ID = ?", new String[]{flightId});
        sqLiteDatabase.close();
    }

    public Cursor getClosestFiveFlights() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String query = "SELECT * FROM FLIGHT " +
                "WHERE DEPARTURE_DATETIME > DATETIME('now') " +
                "ORDER BY DEPARTURE_DATETIME ASC " +
                "LIMIT 5";

        return sqLiteDatabase.rawQuery(query, null);
    }

    public Cursor getReservationsByFlight(String flightId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM RESERVATION WHERE FLIGHT_ID = ?";
        return sqLiteDatabase.rawQuery(query, new String[]{flightId});
    }

    public Cursor getReservationByEmailAndFlight(String email, String flightId) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM RESERVATION WHERE PASSENGER_EMAIL = ? AND FLIGHT_ID = ?";
        return sqLiteDatabase.rawQuery(query, new String[]{email, flightId});
    }

    public Cursor getCurrentReservationsForPassenger(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String query = "SELECT * FROM RESERVATION " +
                "JOIN FLIGHT ON RESERVATION.FLIGHT_ID = FLIGHT.FLIGHT_ID " +
                "WHERE RESERVATION.PASSENGER_EMAIL = ? " +
                "AND FLIGHT.DEPARTURE_DATETIME > ?";

        return sqLiteDatabase.rawQuery(query, new String[]{email, currentDate});
    }

    public Cursor getPastReservationsForPassenger(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String query = "SELECT * FROM RESERVATION " +
                "JOIN FLIGHT ON RESERVATION.FLIGHT_ID = FLIGHT.FLIGHT_ID " +
                "WHERE RESERVATION.PASSENGER_EMAIL = ? " +
                "AND FLIGHT.DEPARTURE_DATETIME <= ?";

        return sqLiteDatabase.rawQuery(query, new String[]{email, currentDate});
    }


    public boolean checkUserCredentials(String email, String hashedPassword) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM USER WHERE EMAIL = ? AND HASHED_PASSWORD = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email, hashedPassword});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean doesFlightExist(String flightNumber) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT FLIGHT_ID FROM FLIGHT WHERE FLIGHT_NUMBER = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{flightNumber});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    @SuppressLint("Range")
    public String getUserName(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT FIRST_NAME, LAST_NAME FROM USER WHERE EMAIL = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email});

        String userName = null;
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndex("FIRST_NAME"));
            String lastName = cursor.getString(cursor.getColumnIndex("LAST_NAME"));
            userName = firstName + " " + lastName;
        }

        cursor.close();
        return userName;
    }

    @SuppressLint("Range")
    public String getUserRole(String email) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT ROLE FROM USER WHERE EMAIL = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{email});
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex("ROLE"));
        }
        cursor.close();
        return role;
    }

    public boolean hasPassengerReservedFlight(String email, String flightId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM RESERVATION WHERE PASSENGER_EMAIL = ? AND FLIGHT_ID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, flightId});

        boolean hasReserved = false;
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            hasReserved = count > 0;
        }

        cursor.close();
        return hasReserved;
    }

}

