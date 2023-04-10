package com.mandalorian.chatapp.ui.presentation.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mandalorian.chatapp.R
import com.mandalorian.chatapp.databinding.FragmentRegisterBinding
import com.mandalorian.chatapp.ui.presentation.base.BaseFragment
import com.mandalorian.chatapp.ui.presentation.register.viewModel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val viewModel: SignUpViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_register

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        binding?.viewModel = viewModel
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        lifecycleScope.launch {
            viewModel.signUpComplete.collect {
                val action = RegisterFragmentDirections.toLogin()
                navController.navigate(action)
            }
        }

        lifecycleScope.launch {
            viewModel.signInComplete.collect {
                navController.popBackStack()
            }
        }
    }
}