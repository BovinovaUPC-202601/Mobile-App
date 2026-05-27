package pe.edu.upc.vacapp.alerts.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import pe.edu.upc.vacapp.alerts.data.di.DataModule
import pe.edu.upc.vacapp.alerts.presentation.viewmodel.AlertViewModel

object PresentationModule {

    @Composable
    fun getAlertViewModel(): AlertViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AlertViewModel(DataModule.repository) as T
        }
    )
}
