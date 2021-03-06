package com.pokik;

import com.pokik.model.Data;
import com.pokik.model.IndividualRide;
import com.pokik.model.Vehicle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {
   private static Data data;
   private static ArrayList<IndividualRide> rides;
   private static HashSet<IndividualRide> takenRides;
   private static ArrayList<Vehicle> vehicles;

   private static List<String> filepaths = Arrays.asList(
           "C:\\Users\\User\\Downloads\\a_example",
           "C:\\Users\\User\\Downloads\\b_should_be_easy",
           "C:\\Users\\User\\Downloads\\c_no_hurry",
           "C:\\Users\\User\\Downloads\\d_metropolis",
           "C:\\Users\\User\\Downloads\\e_high_bonus"
   );

   public static void main(String[] args) {
       for (String filepath : filepaths) {
           data = Data.getFromFile(filepath + ".in");

           solveData();

           int index = filepath.lastIndexOf("\\");
           String filename = filepath.substring(index + 1);

           writeSolution(filepath);
       }
   }

   private static void solveData() {
       rides = new ArrayList<>(data.getIndividualRides());
       takenRides = new HashSet<>();
       vehicles = new ArrayList<>();

       for (int i = 0; i < data.getNumVehicles(); i++) {
           vehicles.add(new Vehicle());
       }

       for (int step = 0; step < data.getNumOfSteps(); step++) {
           while (true) {
               // searching for the best vehicle which may take the best ride
               Vehicle bestVehicle = null;
               IndividualRide bestRide = null;
               int curBestDistance = Integer.MAX_VALUE;

               for (Vehicle vehicle : vehicles) {
                   if (vehicle.isRiding()) {
                       continue;
                   }

                   for (IndividualRide ride : rides) {
                       if (takenRides.contains(ride))
                           continue;

                       int distance = calculateDistance(step, vehicle, ride);
                       // if a ride may obtain a bonus, favor it
                       if (step + distance <= ride.getEarliestStart())
                           distance -= data.getPerRideBonus();

                       if (distance < curBestDistance && distance + ride.getLength() <= ride.getLatestFinish() - step) {
                           curBestDistance = distance;
                           bestRide = ride;
                           bestVehicle = vehicle;
                       }
                   }
               }

               if (bestRide == null)
                   break;

               bestVehicle.setDoingRide(step, bestRide);
               rides.remove(bestRide);
               takenRides.add(bestRide);
           }

           moveAllVehicles();
       }
   }

   private static int calculateDistance(int curStep, Vehicle vehicle, IndividualRide ride) {
       int timeDistance = ride.getEarliestStart() - curStep;
       int spaceDistance = vehicle.getPosition().distance(ride.getStartLocation());
       return Integer.max(timeDistance, spaceDistance);
   }

   private static void moveAllVehicles() {
       for (Vehicle vehicle : vehicles) {
           vehicle.move();
       }
   }

   private static void writeSolution(String filepath) {
       try {
           PrintStream printStream = new PrintStream(new FileOutputStream(filepath + ".out"));
           for (Vehicle vehicle : vehicles) {
               printStream.print("" + vehicle.getCompletedRides().size());
               for (IndividualRide ride : vehicle.getCompletedRides()) {
                   printStream.print(" " + data.getIndividualRides().indexOf(ride));
               }
               printStream.println();
           }
           printStream.flush();
           printStream.close();
       } catch (IOException e) {
           // FIXME
       }
   }
}