package pe.edu.upc.vacapp.home.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.home.presentation.viewmodel.HomeViewModel
import pe.edu.upc.vacapp.shared.data.di.timeFormatter
import pe.edu.upc.vacapp.ui.theme.Color

@Composable
//@Preview
fun HomeView(
    onTapAddCampaign: () -> Unit = {},
    onTapAnimal: () -> Unit = {},
    onTapAddBarn: () -> Unit = {},
    onTapInventory: () -> Unit = {},
    // New On Taps Sections
    onTapAnimalsSection: () -> Unit = {},
    onTapCampaignSection: () -> Unit = {},
    onTapBarnSection: () -> Unit = {},
    onTapInventorySection: () -> Unit = {},
    // End of New On Taps Sections
    viewmodel: HomeViewModel
) {
    val isButtonActive = remember { mutableStateOf(false) }
    val userInfo = viewmodel.userInfo.collectAsState()

    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Welcome",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = androidx.compose.ui.graphics.Color(0xFF1D3620),
                textAlign = TextAlign.Center
            )
            Text(
                "${userInfo.value.name}!",
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 40.sp,
                color = androidx.compose.ui.graphics.Color(0xFF1D3620),
                textAlign = TextAlign.Center
            )
        }

        // Card de Registered Animals - Clickable
        Card(
            modifier = Modifier
                .width(365.dp)
                .height(95.dp)
                .clickable { onTapAnimalsSection() }, // Make clickable
            shape = RoundedCornerShape(5.dp), colors = CardDefaults.cardColors(
                containerColor = Color.AlmondCream, contentColor = Color.Black
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Registered animals", fontWeight = FontWeight.Light, fontSize = 24.sp)

                Text(
                    userInfo.value.totalAnimals.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(25.dp)) {
                // Card de Campaigns - Clickable
                Card(
                    modifier = Modifier
                        .width(165.dp)
                        .height(85.dp)
                        .clickable { onTapCampaignSection() }, // Make clickable
                    shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.AlmondCream,
                        contentColor = Color.Black,
                    ),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            5.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            userInfo.value.totalCampaigns.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text("Campaigns", fontWeight = FontWeight.Light, fontSize = 16.sp)
                    }
                }
                // Card de Barn (Stables) - Clickable
                Card(
                    modifier = Modifier
                        .width(165.dp)
                        .height(85.dp)
                        .clickable { onTapBarnSection() }, // Make clickable
                    shape = RoundedCornerShape(5.dp), colors = CardDefaults.cardColors(
                        containerColor = Color.AlmondCream, contentColor = Color.Black
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            5.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            userInfo.value.totalBarns.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text("Stables", fontWeight = FontWeight.Light, fontSize = 16.sp)
                    }
                }
            }

            // Card de Vaccines (Inventory) - Clickable
            /*Card(
                modifier = Modifier
                    .width(165.dp)
                    .height(85.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onTapInventorySection() }, // Make clickable
                shape = RoundedCornerShape(5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.AlmondCream,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    Text("Vaccines", fontWeight = FontWeight.Light, fontSize = 16.sp)
                }
            }*/
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(25.dp, 20.dp)
        ) {
            Text(
                "Upcoming Events",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = androidx.compose.ui.graphics.Color(0xFF1D3620),
                textAlign = TextAlign.Center
            )

            userInfo.value.nextCampaigns.forEach { cam ->
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp), // Divider Space
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            cam.name,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 1
                        )
                        Text(
                            timeFormatter.format(cam.startDate) + "/" + timeFormatter.format(cam.endDate),
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    Divider(
                        color = androidx.compose.ui.graphics.Color.Black,
                        thickness = 1.dp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(
                                horizontal = 0.15.dp
                            )
                    )
                }
            }


        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { isButtonActive.value = !isButtonActive.value }) {
                val icon = if (isButtonActive.value) R.drawable.x_circle else R.drawable.plus_circle
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    tint = Color.Black

                )
            }

            if (isButtonActive.value) {
                Popup(
                    alignment = Alignment.TopEnd, offset = IntOffset(-75, -395)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.AlmondCream)
                            .border(2.dp, Color.Black)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { onTapAnimal() }) {
                            Icon(
                                painter = painterResource(R.drawable.cow),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                tint = Color.Black
                            )
                            Text(
                                "Animal",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { onTapAddCampaign() }) {
                            Icon(
                                painter = painterResource(R.drawable.megaphone),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                tint = Color.Black

                            )
                            Text(
                                "Campaign",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        /*Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { onTapInventory() }) {
                            Icon(
                                painter = painterResource(R.drawable.resource_package),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                tint = Color.Black
                            )
                            Text(
                                "Inventory",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }*/
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { onTapAddBarn() }) {
                            Icon(
                                painter = painterResource(R.drawable.barn),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                tint = Color.Black
                            )
                            Text(
                                "Barn",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
