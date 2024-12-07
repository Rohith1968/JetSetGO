# <img src="/jetset_round.webp" alt="Jet Set" width="50"/> Jet Set Flight Reservation App

## Table of Contents

- [ Jet Set Flight Reservation App](#-jet-set-flight-reservation-app)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Getting Started](#getting-started)
  - [Features](#features)
    - [General Features:](#general-features)
    - [User Roles](#user-roles)
      - [Admin Role:](#admin-role)
      - [Passenger Role:](#passenger-role)
  - [Navigation](#navigation)
    - [Activities](#activities)
    - [Fragments](#fragments)
  - [Database Structure](#database-structure)
  - [Animations](#animations)
  - [Dependencies](#dependencies)
  - [Screenshots](#screenshots)

## Overview

Jet Set is a flight reservation application developed in Java with XML for designing the user interface. The app uses SQLite for managing data related to flights, users, and reservations. Upon launch, mock flight data is fetched from an external API. Users can either be admins or passengers, each having distinct functionalities. The app offers a smooth user experience with custom animations and a responsive design.

## Getting Started

To get started with Jet Set <img src="/jetset_round.webp" alt="Jet Set" width="20"/>, download the pre-release JetSet.v1.0.0.apk file on an android device or emulator.

## Features

### General Features:

- User authentication (sign-up, log-in, profile editing) and session management.
- Toast messages indicating the success or failure of operations.
- Smooth navigation through activities and fragments.
- Error handling with a dedicated "Retrieval Failed" screen if the API fetch fails.

### User Roles

#### Admin Role:

- Perform CRUD operations on flights.
- Filter and search flights by specifying departure and arrival places and dates.
- View reservations made by passengers on flights.

#### Passenger Role:

- View and filter available flights.
- View flight details.
- Make and cancel reservations.
- Specify flight class, baggage options, and food preferences when making a reservation.
- View a detailed reservation summary with a confirmation option.

## Navigation

### Activities

- **Splash Screen:** Custom flight animation created using After Effects and imported via the Lottie Library.

- **Retrieval Failed:** This screen indicates that data fetching from the API failed, and prompts the user to press a 'Retry' button to try again.

- **Login:** User authentication screen.

- **Choose Role:** The sign up button on the login screen send the user to choose a role (admin/passenger) to continue the sign up process.

- **Admin Sign Up:** Shown if the user chooses admin role. The user is prompted to enter basic personal information and a password.

- **Passenger Sign Up:** Shown if the user chooses passenger role. The user is prompted to enter basic personal information and a password. Additionaly, swiping right will show the rest of the information needed to be filled (passport information and food preference).

- **Home:** For both admin and passenger. The main screen of the app. Contains a list of the closest five flights by date.

### Fragments

- Flight CRUD and filtering, flight details, reservation creation and summary, and confirmation are all handled within fragments navigated via a navigation sidebar.

## Database Structure

The app uses SQLite for local data storage with the following structure:

- **Users Table:** Contains user information such as name, email, password, role, and ID.

- **FLIGHT:**

  - '**FLIGHT_ID:**' Primary key.
  - **FLIGHT_NUMBER**, **DEPARTURE_CITY**, **DESTINATION_CITY**, etc.
  - **PRICE_ECONOMY**, **PRICE_BUSINESS**, **PRICE_EXTRA_BAGGAGE**: Relevant pricing information.

- **RESERVATION:**

  - **RESERVATION_ID:** Primary key.
  - **FLIGHT_ID**, **PASSENGER_EMAIL**, **FLIGHT_CLASS**, etc.
  - **PRICE**: Calculated based on flight class and additional baggage.

- **USER:**
  - Stores user information for both admins and passengers.

## Animations

- **Splash Screen:** Animated using After Effects and imported via the Lottie Library.
- **Reservation Summary:** Includes a custom tween animation for the plane icon.

## Dependencies

- Android SDK
- SQLite
- Lottie Library for animations
- Java and XML for development


