package com.example.booknest.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booknest.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity. It only hosts the NavHostFragment declared in activity_main.xml;
 * the three screens are Fragments swapped in by the Navigation Component (nav_graph.xml).
 *
 * @AndroidEntryPoint is required because the Login and Dashboard Fragments receive
 * Hilt-injected ViewModels, and Hilt Fragments must live in a Hilt Activity.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app always uses its light cream theme, so ask for dark status/navigation bar
        // icons. Devices that can't draw dark navigation icons get a dark scrim instead.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, DARK_SCRIM)
        )
        setContentView(R.layout.activity_main)

        // Keep every screen clear of the status bar, navigation bar and on-screen keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private companion object {
        val DARK_SCRIM = Color.argb(0x80, 0x1B, 0x1B, 0x1B)
    }
}
