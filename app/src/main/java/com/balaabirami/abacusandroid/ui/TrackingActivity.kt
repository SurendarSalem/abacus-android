package com.balaabirami.abacusandroid.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.balaabirami.abacusandroid.R
import com.balaabirami.abacusandroid.model.Session


class TrackingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        val steps = findViewById<AppCompatTextView>(R.id.steps)
        steps.movementMethod = ScrollingMovementMethod()
        var stepsStr = ""
        for (str in Session.getSteps()) {
            stepsStr += str
            stepsStr += "\n"
        }
        steps.text = stepsStr
    }
}