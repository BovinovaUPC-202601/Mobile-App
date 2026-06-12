package pe.edu.upc.vacapp.home.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.ai.presentation.di.PresentationModule.getAiAssistantViewModel
import pe.edu.upc.vacapp.ai.presentation.view.AiAssistantView
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.presentation.di.PresentationModule.getAnimalViewModel
import pe.edu.upc.vacapp.animal.presentation.view.AddAnimalForm
import pe.edu.upc.vacapp.animal.presentation.view.AnimalCardList
import pe.edu.upc.vacapp.animal.presentation.view.AnimalDetails
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.barn.presentation.di.PresentationModel.getBarnViewModel
import pe.edu.upc.vacapp.barn.presentation.view.AddBarnView
import pe.edu.upc.vacapp.barn.presentation.view.BarnDetailsView
import pe.edu.upc.vacapp.barn.presentation.view.BarnView
import pe.edu.upc.vacapp.campaign.presentation.di.PresentacionModel.getCampaignViewModel
import pe.edu.upc.vacapp.campaign.presentation.view.CampaignView
import pe.edu.upc.vacapp.campaign.presentation.view.FormCampaignView
import pe.edu.upc.vacapp.home.presentation.di.PresentationModule.getHomeViewModel
import pe.edu.upc.vacapp.home.presentation.view.HomeView
import pe.edu.upc.vacapp.iam.presentation.view.components.AppDrawer
import pe.edu.upc.vacapp.iam.presentation.view.components.AppTopBar
import pe.edu.upc.vacapp.iam.presentation.view.components.DrawerItem
import pe.edu.upc.vacapp.alerts.presentation.di.PresentationModule.getAlertViewModel
import pe.edu.upc.vacapp.alerts.presentation.view.AlertView
import pe.edu.upc.vacapp.inventory.domain.model.Inventory
import pe.edu.upc.vacapp.inventory.presentation.di.PresentationModule.getInventoryViewModel
import pe.edu.upc.vacapp.inventory.presentation.view.AddInventoryForm
import pe.edu.upc.vacapp.inventory.presentation.view.InventoryCardList
import pe.edu.upc.vacapp.inventory.presentation.view.InventoryDetails
import pe.edu.upc.vacapp.monitoring.presentation.di.PresentationModule.getMonitoringViewModel
import pe.edu.upc.vacapp.monitoring.presentation.view.MonitoringView
import pe.edu.upc.vacapp.collars.presentation.di.PresentationModule.getCollarViewModel
import pe.edu.upc.vacapp.shared.data.local.JwtStorage

private val drawerItems: List<DrawerItem> = listOf(
    DrawerItem("home", "Home", Icons.Default.Home),
    DrawerItem("animals", "Animals", Icons.Default.Pets),
    DrawerItem("campaign", "Campaigns", Icons.Default.MedicalServices),
    DrawerItem("barn", "Barns", Icons.Default.Warehouse),
    DrawerItem("monitoring", "Monitoring", Icons.Default.Analytics),
    DrawerItem("alerts", "Alerts", Icons.Default.Notifications),
    DrawerItem("ai-assistant", "AI Assistant", Icons.Default.AutoAwesome)
)

private fun pageTitleFor(route: String?): String? = when (route) {
    "home" -> "Home"
    "animals" -> "Animals"
    "campaign" -> "Campaigns"
    "barn" -> "Barns"
    "monitoring" -> "Monitoring"
    "alerts" -> "Alerts"
    "ai-assistant" -> "AI Assistant"
    "add-campaign", "add-barn", "add-animal", "add-inventory" -> "Add"
    "animal-details" -> "Animal details"
    "inventory-details" -> "Inventory details"
    "barn-details" -> "Barns"
    else -> null
}

@Preview
@Composable
fun Navigation(
    goToLogin: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val selectedAnimal = remember { mutableStateOf<Animal?>(null) }
    val selectedInventory = remember { mutableStateOf<Inventory?>(null) }
    val selectedBarn = remember { mutableStateOf<Barn?>(null) }
    val homeViewModel = getHomeViewModel()
    val monitoringViewModel = getMonitoringViewModel()
    val alertViewModel = getAlertViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userInfo by homeViewModel.userInfo.collectAsState()
    val pageTitle = pageTitleFor(currentRoute)
    val greeting = "Hello, ${userInfo.name.ifBlank { "there" }}"

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                userName = userInfo.name,
                items = drawerItems,
                activeRoute = currentRoute,
                onItemClick = { item -> scope.launch { drawerState.close() }; navigateTo(item.route) },
                onSignOut = {
                    JwtStorage.clearToken()
                    goToLogin()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    greeting = greeting,
                    title = pageTitle,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onBellClick = { navigateTo("alerts") }
                )
            },
            modifier = Modifier
        ) { padding ->
            NavHost(
                navController,
                startDestination = "home",
                modifier = Modifier.padding(top = padding.calculateTopPadding())
            ) {
                composable("home") {
                    homeViewModel.getUserInfo()

                    HomeView(
                        viewmodel = homeViewModel,
                        onTapAddCampaign = { navController.navigate("add-campaign") },
                        onTapAddBarn = { navController.navigate("add-barn") },
                        onTapAnimal = { navController.navigate("add-animal") },
                        onTapInventory = { navController.navigate("add-inventory") },
                        onTapAnimalsSection = { navigateTo("animals") },
                        onTapCampaignSection = { navigateTo("campaign") },
                        onTapBarnSection = { navigateTo("barn") },
                        onTapInventorySection = { navigateTo("inventory") }
                    )
                }

                composable("campaign") {
                    val viewmodel = getCampaignViewModel()
                    viewmodel.getCampaing()
                    CampaignView(
                        viewModel = viewmodel,
                        onTapAddCampaign = { navController.navigate("add-campaign") }
                    )
                }

                composable("add-campaign") {
                    val viewmodel = getCampaignViewModel()
                    viewmodel.getBarns()
                    FormCampaignView(
                        goHome = { navigateTo("home") },
                        viewModel = viewmodel
                    )
                }

                composable("barn") {
                    val viewmodel = getBarnViewModel()
                    val animalViewmodel = getAnimalViewModel()
                    viewmodel.getBarns()
                    animalViewmodel.getAllAnimals()
                    val animals by animalViewmodel.animals.collectAsState()
                    BarnView(
                        viewModel = viewmodel,
                        animals = animals,
                        onTapAddBarn = { navController.navigate("add-barn") },
                        onBarnClick = { barn ->
                            selectedBarn.value = barn
                            navController.navigate("barn-details")
                        }
                    )
                }

                composable("barn-details") {
                    val animalViewmodel = getAnimalViewModel()
                    animalViewmodel.getAllAnimals()
                    val barn = selectedBarn.value
                    if (barn == null) {
                        navigateTo("barn")
                    } else {
                        BarnDetailsView(
                            barn = barn,
                            animals = animalViewmodel.animals.collectAsState().value,
                            onAnimalClick = { animal ->
                                selectedAnimal.value = animal
                                navController.navigate("animal-details")
                            }
                        )
                    }
                }

                composable("add-barn") {
                    val viewmodel = getBarnViewModel()
                    AddBarnView(
                        viewmodel,
                        goHome = { navController.popBackStack() }
                    )
                }

                composable("animals") {
                    val viewmodel = getAnimalViewModel()
                    viewmodel.getAllAnimals()
                    AnimalCardList(
                        viewmodel = viewmodel,
                        onTap = {
                            selectedAnimal.value = it
                            navController.navigate("animal-details")
                        },
                        onTapAddAnimal = {
                            navController.navigate("add-animal")
                        }
                    )
                }

                composable("inventory") {
                    val viewmodel = getInventoryViewModel()
                    viewmodel.getAllInventories()
                    InventoryCardList(viewmodel) {
                        selectedInventory.value = it
                        navController.navigate("inventory-details")
                    }
                }

                composable("ai-assistant") {
                    val viewmodel = remember { getAiAssistantViewModel() }
                    AiAssistantView(viewmodel)
                }

                composable("animal-details") {
                    AnimalDetails(selectedAnimal.value!!, getCollarViewModel())
                }

                composable("inventory-details") {
                    InventoryDetails(selectedInventory.value!!)
                }

                composable("add-animal") {
                    val viewmodel = getAnimalViewModel()
                    viewmodel.getBarns()
                    viewmodel.getBreeds()
                    AddAnimalForm(
                        viewmodel,
                        goHome = { navigateTo("home") },
                        goAnimals = { navigateTo("animals") }
                    )
                }

                composable("add-inventory") {
                    val viewmodel = getInventoryViewModel()
                    viewmodel.getAnimals()
                    AddInventoryForm(
                        viewmodel,
                        goHome = { navigateTo("home") }
                    )
                }

                composable("monitoring") {
                    MonitoringView(monitoringViewModel)
                }

                composable("alerts") {
                    AlertView(alertViewModel, userInfo.id)
                }
            }
        }
    }
}
