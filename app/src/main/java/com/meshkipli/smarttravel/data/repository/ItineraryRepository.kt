package com.meshkipli.smarttravel.data.repository

import com.meshkipli.smarttravel.data.local.db.dao.ItineraryActivityDao
import com.meshkipli.smarttravel.data.local.db.dao.ItineraryDayDao
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryActivity
import com.meshkipli.smarttravel.data.local.db.entities.ItineraryDay
import kotlinx.coroutines.flow.Flow

class ItineraryRepository(
    private val itineraryDayDao: ItineraryDayDao,
    private val itineraryActivityDao: ItineraryActivityDao
) {
    // ItineraryDay Operations
    fun getAllDays(): Flow<List<ItineraryDay>> = itineraryDayDao.getAllDays()
    fun getDayById(dayId: Long): Flow<ItineraryDay?> = itineraryDayDao.getDayById(dayId)
    suspend fun insertDay(day: ItineraryDay): Long = itineraryDayDao.insertDay(day)
    suspend fun updateDay(day: ItineraryDay) = itineraryDayDao.updateDay(day)
    suspend fun deleteDay(day: ItineraryDay) = itineraryDayDao.deleteDay(day)

    // ItineraryActivity Operations
    fun getActivitiesForDay(dayId: Long): Flow<List<ItineraryActivity>> =
        itineraryActivityDao.getActivitiesForDay(dayId)
    fun getActivityById(activityId: Long): Flow<ItineraryActivity?> =
        itineraryActivityDao.getActivityById(activityId)
    suspend fun insertActivity(activity: ItineraryActivity) =
        itineraryActivityDao.insertActivity(activity)
    suspend fun updateActivity(activity: ItineraryActivity) =
        itineraryActivityDao.updateActivity(activity)
    suspend fun deleteActivity(activity: ItineraryActivity) =
        itineraryActivityDao.deleteActivity(activity)
}