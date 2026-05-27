package pe.edu.upc.vacapp.monitoring.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import pe.edu.upc.vacapp.monitoring.data.di.DataModule
import pe.edu.upc.vacapp.monitoring.presentation.viewmodel.MonitoringViewModel

object PresentationModule {

    @Composable
    fun getMonitoringViewModel(): MonitoringViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MonitoringViewModel(DataModule.repository) as T
        }
    )
}
