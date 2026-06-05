package pe.edu.upc.vacapp.barn.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.barn.presentation.viewmodel.BarnViewModel
import pe.edu.upc.vacapp.ui.theme.Color
import androidx.compose.runtime.collectAsState

@Composable

fun FormBarnView(
    viewModel: BarnViewModel,
    goHome:() ->Unit
) {
    val barn = remember { mutableStateOf(Barn()) }
    val isLoading = viewModel.isLoading.collectAsState()
    val saveSuccess = viewModel.saveSuccess.collectAsState()
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Black,
        disabledTextColor = Color.Black
    )
    LaunchedEffect(saveSuccess.value) {
        if (saveSuccess.value == true) {
            goHome()
            viewModel.resetSaveSuccess()
        }
    }

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
                colors = colors,
                value = barn.value.name,
                onValueChange = {
                    barn.value = barn.value.copy(name = it)
                },
                label = {
                    Text(
                        "Name",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 40.sp,
                    )
                },
                textStyle = TextStyle(color = Color.Black)

            )



            TextField(
                colors = colors,
                value = barn.value.limit,
                onValueChange = {
                    barn.value = barn.value.copy(limit = it)
                },
                label = {
                    Text(
                        "Limit",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 40.sp
                    )
                },
                textStyle = TextStyle(color = Color.Black)

            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { goHome() },
                    enabled = !isLoading.value
                ) {
                    Icon(
                        painterResource(R.drawable.x_circle),
                        null,
                        modifier = Modifier.size(45.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.addBarn(barn.value) },
                    enabled = !isLoading.value
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(35.dp),
                            color = Color.Black,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.check_circle), null,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun Underlined(value: String) {
    Text(
        text = value,
        fontSize = 18.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    )
}