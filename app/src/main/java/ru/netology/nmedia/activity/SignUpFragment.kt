package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.domain.SignUpViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {
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

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri = it.data?.data
                        viewModel.updatePhoto(uri, uri?.toFile())
                    }
                }
            }

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                viewModel.clearState()
                findNavController().navigateUp()
            } else if (it.isError) {
                Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG).show()
            }
            with (binding) {
                progressBar.isVisible = it.isSigningUp
                signUpLogin.isEnabled = !it.isSigningUp
                signUpPassword1.isEnabled = !it.isSigningUp
                signUpPassword2.isEnabled = !it.isSigningUp
                signUpName.isEnabled = !it.isSigningUp
                signUpButton.isEnabled = !it.isSigningUp
                pickPhotoButton.isEnabled = !it.isSigningUp
                pickCameraButton.isEnabled = !it.isSigningUp
            }
        }

        binding.signUpButton.setOnClickListener {
            if (binding.signUpLogin.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_login_empty))
            } else if (binding.signUpPassword1.text.toString().length < 1) {
                viewModel.setError(getString(R.string.error_password_empty))
            } else if (!binding.signUpPassword1.text.toString().equals(binding.signUpPassword2.text.toString())) {
                viewModel.setError(getString(R.string.error_passwords_not_equal))
            } else viewModel.signUp(
                login = binding.signUpLogin.text.toString(),
                password = binding.signUpPassword1.text.toString(),
                name = binding.signUpName.text.toString(),

            )
        }

        binding.pickPhotoButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.pickCameraButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .compress(2048)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removeImagePreviewButton.setOnClickListener {
            viewModel.updatePhoto(null, null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            binding.imagePreviewLayout.isVisible = (!it.isEmpty)
            binding.imagePreview.setImageURI(it.uri)
        }


        return binding.root
    }
}