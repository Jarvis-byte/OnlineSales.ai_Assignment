package com.example.mathcalculator.DataBase

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ResultDatabaseTest: TestCase() {

    private lateinit var spendsDao: ResultDao
    private lateinit var db: ResultDatabase

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ResultDatabase::class.java
        ).build()
        spendsDao = db.resultDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadSpend() = runBlocking {

        // Get the current date
        val sdf = SimpleDateFormat("dd-MM-YYYY ', Time :' hh:mmaa", Locale.ENGLISH)
        val currentDate = sdf.format(Calendar.getInstance().time)
        val spend = ResultEntity(currentDate, "100", "for Bread")
        spendsDao.insert(spend)
        val spends = spendsDao.allResults

    }
}