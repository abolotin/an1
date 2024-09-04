package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostViewBinding
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.domain.PostInteractionListenerAbstract
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.domain.SignInViewModel
import ru.netology.nmedia.domain.SignUpViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg

class SignUpFragment : Fragment() {
    companion object {
        var Bundle.postId: Long? by LongArg
        var Bundle.postLocalId: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
        val viewModel: SignUpViewModel by activityViewModels()

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                viewModel.clearState()
                findNavController().navigateUp()
            } else if (it.isError) {
                Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG).show()
            }
            binding.signUpLogin.isEnabled = !it.isSigningUp
            binding.signUpPassword.isEnabled = !it.isSigningUp
            binding.signUpButton.isEnabled = !it.isSigningUp
        }

        binding.signUpButton.setOnClickListener {
            if (binding.signUpLogin.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_login_empty))
            } else if (binding.signUpPassword.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_password_empty))
            } else viewModel.signUp(
                login = binding.signUpLogin.text.toString(),
                password = binding.signUpPassword.text.toString()
            )
        }

        return binding.root
    }
}