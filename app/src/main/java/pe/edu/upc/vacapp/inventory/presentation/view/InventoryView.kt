package pe.edu.upc.vacapp.inventory.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState
import pe.edu.upc.vacapp.inventory.domain.model.Category
import pe.edu.upc.vacapp.inventory.domain.model.Product
import pe.edu.upc.vacapp.inventory.presentation.viewmodel.InventoryViewModel
import pe.edu.upc.vacapp.ui.theme.Emerald30
import pe.edu.upc.vacapp.ui.theme.Error40

@Composable
fun InventoryView(
    viewModel: InventoryViewModel,
    onAddProduct: () -> Unit,
    onAddCategory: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onEditCategory: (Category) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.resetErrorMessage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetDeleteSuccess()
    }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) productToDelete = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Estás seguro de eliminar \"${productToDelete!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            viewModel.deleteProduct(productToDelete!!.id)
                            isDeleting = false
                            productToDelete = null
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Eliminar", color = Error40)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar", color = Emerald30)
                }
            }
        )
    }

    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) categoryToDelete = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Estás seguro de eliminar \"${categoryToDelete!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            viewModel.deleteCategory(categoryToDelete!!.id)
                            isDeleting = false
                            categoryToDelete = null
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Eliminar", color = Error40)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancelar", color = Emerald30)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(4.dp)
                ) {
                    TabButton(
                        label = "Productos",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        label = "Categorías",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> ProductTab(
                        products = products,
                        onEdit = onEditProduct,
                        onDelete = { productToDelete = it }
                    )
                    1 -> CategoryTab(
                        categories = categories,
                        onEdit = onEditCategory,
                        onDelete = { categoryToDelete = it }
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 20.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    ExtendedFloatingActionButton(
                        onClick = onAddCategory,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Folder,
                                contentDescription = null
                            )
                        },
                        text = { Text("Categoría") }
                    )
                    ExtendedFloatingActionButton(
                        onClick = onAddProduct,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null
                            )
                        },
                        text = { Text("Producto") }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProductTab(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (products.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Inventory2,
                title = "Sin productos aún",
                description = "Los productos registrados aparecerán aquí."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { onEdit(product) },
                        onDelete = { onDelete(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTab(
    categories: List<Category>,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (categories.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Folder,
                title = "Sin categorías aún",
                description = "Las categorías registradas aparecerán aquí."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        onEdit = { onEdit(category) },
                        onDelete = { onDelete(category) }
                    )
                }
            }
        }
    }
}
