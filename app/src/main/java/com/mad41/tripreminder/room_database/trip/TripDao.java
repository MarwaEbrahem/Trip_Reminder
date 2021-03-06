package com.mad41.tripreminder.room_database.trip;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mad41.tripreminder.room_database.trip.MyGenericDao;
import com.mad41.tripreminder.room_database.trip.Trip;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TripDao  {


    @Insert
    long insertTrip(Trip trip);


    @Query("update trips_table  SET name=:name ,startLoacation=:startLoacation," +
            "endLoacation=:endLoacation,time=:time,date=:date,notes=:notes, status=:status,isRepeated=:isRepeated," +
            "isRound=:isRound Where id=:id")
    void updateTrip(int id, String name, String startLoacation, String endLoacation, String time, String date, ArrayList<String>notes, int status, boolean isRepeated, boolean isRound);

    @Update
    void updateRow(Trip trip);

    @Query("update trips_table  SET status=:status Where id=:id")
    void updateStatus(int id,int status);

    @Query("DELETE FROM trips_table WHERE id = :id")
    void deletTripById(int id);
    @Delete
    void deletTrip(Trip trip);

    @Query("DELETE  FROM trips_table")
    void deleteAllTrips();


    @Query("SELECT * FROM trips_table WHERE status=2")
    LiveData<List<Trip>> getUpcomingTrips();

    @Query("SELECT * FROM trips_table WHERE status!=2")
    LiveData<List<Trip>>getHistoryTrips();


    @Query("SELECT * FROM trips_table")
    LiveData<List<Trip>> getAllTrips();

    @Query("SELECT * FROM trips_table where id=:id")
    Trip getTripById(int id);

    @Query("SELECT * FROM trips_table")
    List<Trip> getAllTripsForFireBase();

}