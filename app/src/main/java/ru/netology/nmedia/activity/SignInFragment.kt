package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.domain.SignInViewModel
import ru.netology.nmedia.util.LongArg

@AndroidEntryPoint
class SignInFragment : Fragment() {
    companion object {
        var Bundle.postId: Long? by LongArg
        var Bundle.postLocalId: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )
        val viewModel: SignInViewModel by activityViewModels()

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                viewModel.clearState()
                findNavController().navigateUp()
            } else if (it.isError) {
                Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG).show()
            }
            binding.signInLogin.isEnabled = !it.isSigningIn
            binding.signInPassword.isEnabled = !it.isSigningIn
            binding.signInButton.isEnabled = !it.isSigningIn
        }

        binding.signInButton.setOnClickListener {
            if (binding.signInLogin.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_login_empty))
            } else if (binding.signInPassword.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_password_empty))
            } else viewModel.signIn(
                login = binding.signInLogin.text.toString(),
                password = binding.signInPassword.text.toString()
            )
        }

        return binding.root
    }
}