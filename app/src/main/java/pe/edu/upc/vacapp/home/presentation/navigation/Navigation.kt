package pe.edu.upc.vacapp.home.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import pe.edu.upc.vacapp.animal.presentation.view.ManageBreedsView
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
import pe.edu.upc.vacapp.inventory.presentation.di.PresentationModule.getInventoryViewModel
import pe.edu.upc.vacapp.inventory.presentation.view.AddCategoryView
import pe.edu.upc.vacapp.inventory.presentation.view.AddProductView
import pe.edu.upc.vacapp.inventory.presentation.view.InventoryView
import pe.edu.upc.vacapp.monitoring.presentation.di.PresentationModule.getMonitoringViewModel
import pe.edu.upc.vacapp.monitoring.presentation.view.MonitoringView
import pe.edu.upc.vacapp.collars.presentation.di.PresentationModule.getCollarViewModel
import pe.edu.upc.vacapp.subscription.presentation.di.PresentationModule.getSubscriptionViewModel
import pe.edu.upc.vacapp.subscription.presentation.view.SubscriptionView
import pe.edu.upc.vacapp.shared.session.SessionManager

private val drawerItems: List<DrawerItem> = listOf(
    DrawerItem("home", "Inicio", Icons.Default.Home),
    DrawerItem("animals", "Animales", Icons.Default.Pets),
    DrawerItem("campaign", "Campañas", Icons.Default.MedicalServices),
    DrawerItem("inventory", "Inventario", Icons.Default.Inventory2),
    DrawerItem("barn", "Establos", Icons.Default.Warehouse),
    DrawerItem("monitoring", "Monitoreo", Icons.Default.Analytics, plusOnly = true),
    DrawerItem("alerts", "Alertas", Icons.Default.Notifications),
    DrawerItem("ai-assistant", "Asistente IA", Icons.Default.AutoAwesome, plusOnly = true),
    DrawerItem("subscription", "Suscripción", Icons.Default.WorkspacePremium)
)

private fun pageTitleFor(route: String?): String? = when (route) {
    "home" -> "Inicio"
    "animals" -> "Animales"
    "campaign" -> "Campañas"
    "barn" -> "Establos"
    "monitoring" -> "Monitoreo"
    "alerts" -> "Alertas"
    "ai-assistant" -> "Asistente IA"
    "subscription" -> "Suscripción"
    "inventory" -> "Inventario"
    "manage-breeds" -> "Administrar razas"
    "animal-details" -> "Detalles del animal"
    "barn-details" -> "Establos"
    else -> if (route?.startsWith("add-") == true || route?.startsWith("edit-") == true) "Añadir" else null
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
    val selectedBarn = remember { mutableStateOf<Barn?>(null) }
    val homeViewModel = getHomeViewModel()
    val monitoringViewModel = getMonitoringViewModel()
    val alertViewModel = getAlertViewModel()
    val drawerSubscriptionViewModel = getSubscriptionViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userInfo by homeViewModel.userInfo.collectAsState()
    val currentSubscription by drawerSubscriptionViewModel.current.collectAsState()
    // Free/Plus badge in the drawer header (mirrors the web sidebar badge).
    val planLabel = currentSubscription?.let { if (it.isPlusActive) "Plus" else "Gratis" } ?: "Gratis"
    // Reload the plan on every screen change so the gating reflects upgrades/downgrades
    // (cancel happens on a different VM instance, so we re-fetch here).
    LaunchedEffect(currentRoute) { drawerSubscriptionViewModel.load() }

    // Guard: if a Free user is sitting on a Plus-only screen (e.g. after a downgrade),
    // bounce them back to Home. Only acts once we know the plan (subscription != null).
    LaunchedEffect(currentRoute, currentSubscription) {
        val plusRoutes = setOf("monitoring", "ai-assistant")
        if (currentSubscription?.isPlusActive == false && currentRoute in plusRoutes) {
            navController.navigate("home") { launchSingleTop = true }
        }
    }
    val pageTitle = pageTitleFor(currentRoute)
    val greeting = "Hola, ${userInfo.name.ifBlank { "ahí" }}"

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
                // Hide Plus-only features (IA, Monitoring) once we know the user isn't an
                // active Plus. While the subscription is still loading (null) we keep them.
                items = drawerItems.filter { !it.plusOnly || currentSubscription?.isPlusActive != false },
                activeRoute = currentRoute,
                onItemClick = { item -> scope.launch { drawerState.close() }; navigateTo(item.route) },
                onSignOut = {
                    // Clears token + alert bookkeeping + every cached ViewModel so the
                    // next user never inherits this session's state.
                    SessionManager.logout()
                    goToLogin()
                },
                plan = planLabel
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
                    val inventoryViewModel = getInventoryViewModel()
                    LaunchedEffect(Unit) { inventoryViewModel.getProducts() }
                    val products by inventoryViewModel.products.collectAsState()
                    val categoryCount = remember(products) {
                        products.map { it.categoryId }.distinct().size
                    }

                    HomeView(
                        viewmodel = homeViewModel,
                        totalCategories = categoryCount,
                        onTapAddCampaign = { navController.navigate("add-campaign") },
                        onTapAddBarn = { navController.navigate("add-barn") },
                        onTapAnimal = { navController.navigate("add-animal") },
                        onTapAddProduct = { navController.navigate("add-inventory") },
                        onTapAddCategory = { navController.navigate("add-category") },
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
                    viewmodel.getAnimals()
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
                        },
                        onEdit = {
                            navController.navigate("edit-animal/${it.id}")
                        },
                        onDelete = { animal ->
                            viewmodel.deleteAnimal(animal.id)
                        }
                    )
                }

                composable("inventory") {
                    val viewmodel = getInventoryViewModel()
                    InventoryView(
                        viewModel = viewmodel,
                        onAddProduct = { navController.navigate("add-inventory") },
                        onAddCategory = { navController.navigate("add-category") },
                        onEditProduct = { product ->
                            navController.navigate("edit-inventory/${product.id}")
                        },
                        onEditCategory = { category ->
                            navController.navigate("edit-category/${category.id}")
                        }
                    )
                }

                composable("ai-assistant") {
                    val viewmodel = remember { getAiAssistantViewModel() }
                    AiAssistantView(viewmodel)
                }

                composable("animal-details") {
                    // Guard against a missing selection (e.g. after process death) instead of !!.
                    val animal = selectedAnimal.value
                    if (animal == null) {
                        navigateTo("animals")
                    } else {
                        AnimalDetails(animal, getCollarViewModel(), getAnimalViewModel())
                    }
                }

                composable("add-animal") {
                    val viewmodel = getAnimalViewModel()
                    viewmodel.getBarns()
                    viewmodel.getBreeds()
                    viewmodel.getAllAnimals()
                    AddAnimalForm(
                        viewmodel,
                        goHome = { navigateTo("home") },
                        goAnimals = { navigateTo("animals") },
                        onManageBreeds = { navController.navigate("manage-breeds") },
                        editAnimal = null
                    )
                }

                composable("edit-animal/{animalId}") { backStackEntry ->
                    val viewmodel = getAnimalViewModel()
                    viewmodel.getBarns()
                    viewmodel.getBreeds()
                    viewmodel.getAllAnimals()
                    val animalId = backStackEntry.arguments?.getString("animalId")?.toIntOrNull()
                    if (animalId == null) {
                        navigateTo("animals")
                        return@composable
                    }
                    val animals by viewmodel.animals.collectAsState()
                    LaunchedEffect(Unit) {
                        if (animals.isEmpty()) viewmodel.getAllAnimals()
                    }
                    val editAnimal = remember(animalId, animals) {
                        animals.find { it.id == animalId }
                    }
                    AddAnimalForm(
                        viewmodel,
                        goHome = { navigateTo("home") },
                        goAnimals = { navigateTo("animals") },
                        onManageBreeds = { navController.navigate("manage-breeds") },
                        editAnimal = editAnimal
                    )
                }

                composable("manage-breeds") {
                    val viewmodel = getAnimalViewModel()
                    ManageBreedsView(
                        viewModel = viewmodel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("add-inventory") {
                    val viewmodel = getInventoryViewModel()
                    AddProductView(
                        goHome = { navigateTo("home") },
                        viewModel = viewmodel,
                        editProduct = null
                    )
                }

                composable("edit-inventory/{productId}") { backStackEntry ->
                    val viewmodel = getInventoryViewModel()
                    val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                    if (productId == null) {
                        navigateTo("home")
                        return@composable
                    }
                    val products by viewmodel.products.collectAsState()
                    LaunchedEffect(Unit) {
                        if (products.isEmpty()) viewmodel.getProducts()
                    }
                    val editProduct = remember(productId, products) {
                        products.find { it.id == productId }
                    }
                    AddProductView(
                        goHome = { navigateTo("home") },
                        viewModel = viewmodel,
                        editProduct = editProduct
                    )
                }

                composable("add-category") {
                    val viewmodel = getInventoryViewModel()
                    AddCategoryView(
                        goHome = { navigateTo("home") },
                        viewModel = viewmodel,
                        editCategory = null
                    )
                }

                composable("edit-category/{categoryId}") { backStackEntry ->
                    val viewmodel = getInventoryViewModel()
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
                    if (categoryId == null) {
                        navigateTo("home")
                        return@composable
                    }
                    val categories by viewmodel.categories.collectAsState()
                    LaunchedEffect(Unit) {
                        if (categories.isEmpty()) viewmodel.getCategories()
                    }
                    val editCategory = remember(categoryId, categories) {
                        categories.find { it.id == categoryId }
                    }
                    AddCategoryView(
                        goHome = { navigateTo("home") },
                        viewModel = viewmodel,
                        editCategory = editCategory
                    )
                }

                composable("monitoring") {
                    val animalVm = getAnimalViewModel()
                    val collarVm = getCollarViewModel()
                    LaunchedEffect(Unit) {
                        animalVm.getAllAnimals()
                        collarVm.fetchCollars()
                    }
                    val animals by animalVm.animals.collectAsState()
                    val collars by collarVm.collars.collectAsState()
                    // Only bovines with a collar stream IoT data.
                    val collaredIds = collars.map { it.bovineId }.toSet()
                    val monitorable = animals
                        .filter { it.id in collaredIds }
                        .map { it.id to it.name }
                    MonitoringView(monitoringViewModel, monitorable)
                }

                composable("alerts") {
                    AlertView(alertViewModel, userInfo.id)
                }

                composable("subscription") {
                    val viewmodel = remember { getSubscriptionViewModel() }
                    LaunchedEffect(Unit) { viewmodel.load() }
                    SubscriptionView(viewmodel)
                }
            }
        }
    }
}
