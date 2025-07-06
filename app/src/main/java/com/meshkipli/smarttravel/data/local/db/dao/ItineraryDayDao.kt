package com.meshkipli.smarttravel.data.local.db.dao

import androidx.room.*
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: ItineraryDay): Long // Returns the id of the inserted day

    @Update
    suspend fun updateDay(day: ItineraryDay)

    @Delete
    suspend fun deleteDay(day: ItineraryDay)

    @Query("SELECT * FROM itinerary_days ORDER BY id ASC")
    fun getAllDays(): Flow<List<ItineraryDay>> // Use Flow for reactive updates

    @Query("SELECT * FROM itinerary_days WHERE id = :dayId")
    fun getDayById(dayId: Long): Flow<ItineraryDay?>
}

@Dao
interface ItineraryActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ItineraryActivity)

    @Update
    suspend fun updateActivity(activity: ItineraryActivity)

    @Delete
    suspend fun deleteActivity(activity: ItineraryActivity)

    @Query("SELECT * FROM itinerary_activities WHERE dayId = :dayId ORDER BY time ASC")
    fun getActivitiesForDay(dayId: Long): Flow<List<ItineraryActivity>>

    @Query("SELECT * FROM itinerary_activities WHERE id = :activityId")
    fun getActivityById(activityId: Long): Flow<ItineraryActivity?>
}
