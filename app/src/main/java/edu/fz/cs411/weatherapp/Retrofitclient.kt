package edu.fz.cs411.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val webservice: Api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}

class ExitButtonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // The layout for this fragment will be automatically inflated
        // based on the XML specified in fragment_exit_button.xml.
        // No need to manually inflate it here.

        // Find the ImageView in the fragment_exit_button.xml layout
        val exitButton: ImageView = requireView().findViewById(R.id.exitButton)

        // Set a click listener for the exit button
        exitButton.setOnClickListener {
            // Call a function to exit the app
            exitApp()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun exitApp() {
        // Exit the app
        activity?.finishAffinity()
    }
}