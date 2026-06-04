package pe.edu.upc.vacapp.campaign.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.vacapp.campaign.domain.model.Campaign
import pe.edu.upc.vacapp.shared.data.di.timeFormatter
import pe.edu.upc.vacapp.ui.theme.Color

@Composable
@Preview
fun CardCampaignView(
    campaign: Campaign = Campaign()
) {
    Card(
        modifier = Modifier
            .width(380.dp)
            .height(220.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.AlmondCream,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding interior para dar margen al contenido
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                campaign.name,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp)) {
                            append("Description: \n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light, fontSize = 16.sp)) {
                            append("Test ${campaign.description}")
                        }
                    },
                    color = Color.Black,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Start Date:",
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    timeFormatter.format(campaign.startDate),
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "End Date:",
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    timeFormatter.format(campaign.endDate),
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}