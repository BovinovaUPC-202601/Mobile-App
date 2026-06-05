package pe.edu.upc.vacapp.campaign.presentation.view

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.campaign.domain.model.Campaign
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel
import pe.edu.upc.vacapp.ui.theme.Color

@Composable

fun AddCampaignView(
    goHome: () -> Unit = {},
    viewModel: CampaignViewModel
) {
    val barns = viewModel.barn.collectAsState()
    val campaign = remember { mutableStateOf(Campaign()) }
    Card(
        modifier = Modifier
            .width(356.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.AlmondCream,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            TextField(
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                value = campaign.value.name,
                onValueChange = {
                    campaign.value = campaign.value.copy(name = it)
                },

                label = {
                    Text(
                        "Name",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        color = Color.Black
                    )
                },
                textStyle = TextStyle(color = Color.Black)

            )
            TextField(
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                value = campaign.value.description,
                onValueChange = {
                    campaign.value = campaign.value.copy(description = it)
                },

                label = {
                    Text(
                        "Description",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                        color = Color.Black
                    )
                },
                textStyle = TextStyle(color = Color.Black)

            )
            Row {
                DropdownSelector(
                    label = "Barn",
                    items = barns.value,
                    onItemSelected = { barn ->
                        campaign.value = campaign.value.copy(barnId = barn.id)
                    }
                )
            }

            DatePickerTextField(
                label = "Start date",
                date = campaign.value.startDate,
                onDateChange = {
                    campaign.value = campaign.value.copy(startDate = it)
                }
            )

            DatePickerTextField(
                label = "End date",
                date = campaign.value.endDate,
                onDateChange = {
                    campaign.value = campaign.value.copy(endDate = it)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { goHome() }
                ) {
                    Icon(
                        painterResource(R.drawable.x_circle),
                        null,
                        modifier = Modifier.size(45.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.addCanpaing(campaign.value) }
                ) {
                    Icon(
                        painterResource(R.drawable.check_circle), null,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }


        }
    }
}

@Composable
fun DatePickerTextField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(selectedDate)
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = date.format(formatter),
        onValueChange = { },
        readOnly = true,
        label = {
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                color = Color.Black
            )
        },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick Date",
                    tint = Color.Black
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black
        ),
        textStyle = TextStyle(color = Color.Black)
    )
}

@Composable
fun DropdownSelector(
    label: String,
    items: List<Barn>,
    onItemSelected: (Barn) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<Barn?>(null) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedItem.value?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                    color = Color.Black
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded.value = true },
                    tint = Color.Black
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black
            ),
            textStyle = TextStyle(color = Color.Black)
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem.value = item
                        expanded.value = false
                        onItemSelected(selectedItem.value!!)
                    },
                    text = { Text(text = item.name) }
                )
            }
        }
    }
}
