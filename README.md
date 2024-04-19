# Description

This Java program simulates an aviation conglomerate with countless subsidiaries
including regional, continental and international airline companies. The program
implements algorithms that give the successive flights with the minimum total cost
to an airport in a given time frame.

- **Airport:** Where planes depart, park and land. Every airport has a unique AirportCode,
an associated AirfieldName, a latitude, a longitude and a parkingCost. Information on
Airports will be provided in <airports-csv> file.


- **Airfield:** An Airfield is a group of airports that share the same weather conditions.
At a given Air- fieldName and Time, there is a corresponding WeatherCode. This
information is given in the <weather-csv> file.


- **Weather multipliers:** Multipliers used in the calculation of the cost to fly from an
airport to another one. They represent the effects of weather conditions on the cost
of flight. Weather multiplier of an airport at a given time can be inferred from the
WeatherCode wc that is associated with the Airfield the airport belongs to and given Time.

  - Let **_Bw_**, **_Br_**, **_Bs_**, **_Bh_**, **_Bb_** be variables that represent
  whether the corresponding weather conditions are present: Wind, Rain, Snow, Hail, Bolt.
  These values are determined by their corresponding bits of the 5-bit unsigned binary
  representation of the wc.

  - **Example:** WeatherCode: 25. Binary: 11001. Bw = 1, Br = 1, Bs = 0, Bh = 0, Bb = 1.
  Windy and rainy with bolts.


- **Distance:** Distance between the airports is given by The Haversine Formula.


- **Flight Durations:** Three main subsidiaries are Lounge Turkey, Lounge Continental and
Lounge International. These subsidiaries use different planes with different flight
durations.


- **Possible Flights:** There is a set of possible flights from each airport to other
airports. These are given in the <directions-csv> file.


- **Flight Cost:** Let d be the distance between the departed airport and the landing
airport, WD weather multiplier of the departed airport at the time of departing,
WL weather multiplier of the landing airport at the time of landing. Cost of the
flight is given by the formula:
   - _cf = 300 * WD * WL + d_


- **Parking Cost:** A parking operation is waiting at an airport for 6 hours with a cost
of parkingCost. Every airport has a parkingCost value. Successive parking operations can
also be made without a limit. For example, 4 successive parking operations would be
equivalent to 24 hours of parking.


- **Mission:** Every mission consists of 4 elements: AirportOrigin, TimeOrigin,
AirportDestination and Deadline. Starting from the AirportOrigin at TimeOrigin, the
program comes up with a sequence of successive possible flight and park operations to
reach the AirportDestination before the Deadline with the minimum total cost.


- **Time:** Every flight and park operation takes time and time marches forward with every
operation. The program also takes this into account when accessing weather data.


# Requirements

- Java Development Kit (JDK) installed on your system.

# Usage

To compile and run the program, follow these steps:
1. Compile: Open your terminal and navigate to the project directory.
```console
javac src/*.java -d ./
```

2. Run: Execute the compiled Java program with 6 arguments representing file names.
```console
java Main <airports-csv> <directions-csv> <weather-csv> <missions-in> <task1-out> <task2-out>
```

# Arguments

The program expects the following 6 arguments:

1. **_<airports-csv>_ :** Essential attributes of the Airports.
2. **_<directions-csv>_ :** Possible directions of two different airports. Note that the
lines are directed.
3. **_<weather-csv>_ :** Weather conditions at specific times and airfields.
4. **_<missions-in>_ :** In the first line of this file, the plane model which the
missions will be accomplished with should be given. In the subsequent lines, AirportOrigin,
AirportDestination, TimeOrigin and Deadline information about the mission sequentially
should be given.
5. **_<task1-out>_ :** Task 1 is a simplified version of the Task 2. In this task, the
program finds successive possible flight operations from AirportOrigin to
AirportDestination. It is assumed there is no deadline, and all flights are happening at
TimeOrigin for each mission.
6. **_<task2-out>_ :** In this task, the program finds a sequence of successive possible
flight and park operations starting from the AirportOrigin at TimeOrigin to reach the
AirportDestination before the Deadline with the minimum total cost.

Some example input and output cases are provided in the repository.

# Notes

- \<task1-out\> and \<task2-out\> files are both ".out" files. Every line of the ".out" file
is the solution of the corresponding mission in the <missions-in> file.
- Files stated in the "arguments" section (except .out files as they will be created by the
java program) should be in the project directory just above the "src/" directory.