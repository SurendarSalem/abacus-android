package com.balaabirami.abacusandroid.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.balaabirami.abacusandroid.R
import com.balaabirami.abacusandroid.model.Session
import com.balaabirami.abacusandroid.room.AbacusDatabase
import com.balaabirami.abacusandroid.room.OrderDao
import kotlinx.coroutines.*


class TrackingActivity : AppCompatActivity() {
    lateinit var orderDao: OrderDao

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        val steps = findViewById<AppCompatTextView>(R.id.steps)
        steps.movementMethod = ScrollingMovementMethod()
        AbacusDatabase.getAbacusDatabase(applicationContext)?.orderDao()?.let {
            orderDao = it
            CoroutineScope(Dispatchers.IO).launch {
                val ordersLogs = orderDao.getOrderLogs()
                var stepsStr = ""
                ordersLogs.forEach() { orderLog ->
                    val id = orderLog.id
                    stepsStr += orderLog.orderId + " -  " + orderLog.step
                    stepsStr += "\n"
                }
                runOnUiThread {
                    steps.text = stepsStr
                }
            }

        }

        /*var stepsStr = ""
        ordersLogs.forEach { orderLog ->
            stepsStr += orderLog.orderId + " -  " + orderLog.step
            stepsStr += "\n"
        }
        runBlocking {
            steps.text = stepsStr
        }*/
        /* var stepsStr = ""
         for (str in Session.getSteps()) {
             stepsStr += str
             stepsStr += "\n"
         }*/

    }
}