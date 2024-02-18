package com.example.practica_2

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Configuracion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuracion)

        val themeSwitch: Switch = findViewById(R.id.themeSwitch)
        val dollarSwitch: Switch = findViewById(R.id.dollarSwitch)

        val sharedPref = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        val isNightMode = sharedPref.getBoolean("isNightMode", false)
        val isDollar = Utilities.getCurrencyPreference(this)

        themeSwitch.isChecked = isNightMode
        dollarSwitch.isChecked = isDollar

        dollarSwitch.setOnClickListener {
            if (dollarSwitch.isChecked) {
                if(Utilities.isDifferentDay(this)) {
                    Utilities.getCurrencyRate(this)
                }

            } else {
                Utilities.getCurrencyRate(this)
            }

            Utilities.saveCurrencyPreference(this, dollarSwitch.isChecked)
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked, use night theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // The switch is disabled/unchecked, use day theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val sharedPref = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean("isNightMode", isChecked)
                apply()
            }

        }
    }
}