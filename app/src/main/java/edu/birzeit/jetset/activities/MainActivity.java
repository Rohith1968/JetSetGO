package edu.birzeit.jetset.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import edu.birzeit.jetset.R;
import edu.birzeit.jetset.database.DataBaseHelper;
import edu.birzeit.jetset.database.SharedPrefManager;
import edu.birzeit.jetset.model.Flight;
import edu.birzeit.jetset.tasks.ConnectionAsyncTask;
import edu.birzeit.jetset.tasks.FlightJsonParser;
import edu.birzeit.jetset.tasks.Hash;


public class MainActivity extends AppCompatActivity implements ConnectionAsyncTask.TaskCallback {

    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private static final String USER_ROLE = "UserRole";
    private static final String SAVED_EMAIL = "SavedEmail";
    private static final String USER_NAME_KEY = "UserName";
    private static final String REMEMBER_ME = "RememberMe";
    Button signUp;
    Button login;
    EditText email;
    EditText password;
    CheckBox rememberMe;
    DataBaseHelper dataBaseHelper;
    List<Flight> flightsAdded = new ArrayList<>();
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        sharedPrefManager = SharedPrefManager.getInstance(MainActivity.this);

        signUp = findViewById(R.id.buttonSignUp);
        login = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        rememberMe = findViewById(R.id.checkBox);


        boolean isLoggedIn = sharedPrefManager.readBoolean(IS_LOGGED_IN, false);
        String savedEmail = sharedPrefManager.readString(SAVED_EMAIL, "");

        ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(MainActivity.this);
        connectionAsyncTask.execute("https://mocki.io/v1/9261f6be-a97a-4ddb-8e7a-14dbdc7d8acc");

        if (sharedPrefManager.readBoolean(REMEMBER_ME, false) && !savedEmail.isEmpty())
            email.setText(savedEmail);

        if (isLoggedIn)
            if (sharedPrefManager.readString(USER_ROLE, "").equals("Passenger"))
                navigateToPassengerHome();
            else if (sharedPrefManager.readString(USER_ROLE, "").equals("Admin"))
                navigateToAdminHome();


        login.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = Hash.hashPassword(password.getText().toString());
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                displayToast("Please enter both email and password");
                return;
            }

            boolean isValidLogin = dataBaseHelper.checkUserCredentials(emailText, passwordText);
            if (isValidLogin) {
                sharedPrefManager.writeBoolean(REMEMBER_ME, rememberMe.isChecked());

                sharedPrefManager.writeString(SAVED_EMAIL, emailText);
                String userName = getUserNameFromDatabase(emailText);

                sharedPrefManager.writeString(USER_NAME_KEY, userName);
                sharedPrefManager.writeBoolean(IS_LOGGED_IN, true);
                String userRole = getUserRoleFromDatabase(emailText);
                sharedPrefManager.writeString(USER_ROLE, userRole);
                sharedPrefManager.apply();

                if (userRole.equals("Passenger")) navigateToPassengerHome();
                else if (userRole.equals("Admin")) navigateToAdminHome();
            } else displayToast("Invalid email or password");
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseRoleActivity.class);
            startActivity(intent);
        });

    }

    private String getUserNameFromDatabase(String email) {
        try (DataBaseHelper dataBaseHelper = new DataBaseHelper(this)) {
            return dataBaseHelper.getUserName(email);
        }
    }

    private String getUserRoleFromDatabase(String email) {
        try (DataBaseHelper dataBaseHelper = new DataBaseHelper(this)) {
            return dataBaseHelper.getUserRole(email);
        }
    }

    private void navigateToAdminHome() {
        dataBaseHelper.close();
        Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToPassengerHome() {
        dataBaseHelper.close();
        Intent intent = new Intent(MainActivity.this, PassengerHomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskSuccess(String result) {
        displayToast("Connected!");
        List<Flight> flights = FlightJsonParser.getObjectFromJson(result);
        Log.d("MainActivity", "onTaskSuccess: " + flights);
        if (flights != null) {
            for (Flight flight : flights) {
                if (dataBaseHelper.doesFlightExist(flight.getFlightNumber())) {
                    dataBaseHelper.updateFlight(flight);
                } else {
                    flight.setFlightId(dataBaseHelper.insertFlight(flight));
                }
                flightsAdded.add(flight);
            }
            sharedPrefManager.saveFlightList(flightsAdded);
        }
    }

    @Override
    public void onTaskFailure() {
        Intent intent = new Intent(MainActivity.this, RetrievalFailedActivity.class);
        startActivity(intent);
    }


}
