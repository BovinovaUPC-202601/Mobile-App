package pe.edu.upc.vacapp.animal.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage
import pe.edu.upc.vacapp.ui.theme.Color

@Preview(showBackground = true)
@Composable
fun AnimalDetails(
    animal: Animal = Animal()
) {
    val icon = if (animal.isMale) R.drawable.gender_male else R.drawable.gender_female
    val imgUrl = when (val image = animal.image) {
        is AnimalImage.FromUrl -> image.url
        is AnimalImage.FromFile -> ""
        null -> ""
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(355.dp, 565.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.AlmondCream,
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        animal.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = Color.Black
                    )
                    Icon(
                        painterResource(icon),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }

                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(310.dp, 210.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column {
                            Text(
                                "Breed",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                animal.breed,
                                fontWeight = FontWeight.Light,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                        }

                        Column {
                            Text(
                                "Age",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                animal.age.toString(),
                                fontWeight = FontWeight.Light,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                        }

                        Column {
                            Text(
                                "Barn",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                animal.barnName,
                                fontWeight = FontWeight.Light,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        Column {
                            Text(
                                "BirthDate",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                            Text(
                                animal.birthDate,
                                fontWeight = FontWeight.Light,
                                fontSize = 24.sp,
                                color = Color.Black
                            )
                        }


                    }
                }
            }
        }
    }
}