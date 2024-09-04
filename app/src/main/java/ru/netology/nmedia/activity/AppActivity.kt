package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.domain.AuthViewModel

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkGooglePlayServices()

        val viewModel by viewModels<AuthViewModel>()
        viewModel.auth.observe(this) {
            addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    menu.setGroupVisible(R.id.authorized, viewModel.isAuthorized)
                    menu.setGroupVisible(R.id.unauthorized, !viewModel.isAuthorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when(menuItem.itemId) {
                        R.id.sign_in -> {
                            val navController = Navigation.findNavController(
                                this@AppActivity,
                                R.id.fragment_container_view
                            )
                            navController.navigate(R.id.signInFragment)
                            true
                        }
                        R.id.sign_up -> {
                            // AppAuth.getInstance().setAuth(5, "x-token")
                            val navController = Navigation.findNavController(
                                this@AppActivity,
                                R.id.fragment_container_view
                            )
                            navController.navigate(R.id.signUpFragment)
                            true
                        }
                        R.id.logout -> {
                            AppAuth.getInstance().clearAuth()
                            true
                        }
                        else -> return false
                    }
                }

            })
        }
    }

    private fun checkGooglePlayServices() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code != ConnectionResult.SUCCESS) {
                if (isUserResolvableError(code)) {
                    getErrorDialog(this@AppActivity, code, 9000)?.show()
                } else {
                    Toast.makeText(
                        this@AppActivity,
                        getString(R.string.google_api_unavailable_message), Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
