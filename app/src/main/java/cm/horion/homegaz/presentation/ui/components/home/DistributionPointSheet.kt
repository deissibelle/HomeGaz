package cm.horion.homegaz.presentation.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cm.horion.homegaz.R
import cm.horion.homegaz.domain.model.home.DistributorPoint

@Composable
fun DistributionPointSheet(
    point: DistributorPoint,
    onBuyClick: () -> Unit = {},
    onRouteClick: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(
        topStart = 38.dp,
        topEnd = 38.dp,
        bottomStart = 38.dp,
        bottomEnd = 38.dp
    )

    val pointerShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, size.height / 2f)
        lineTo(0f, size.height)
        close()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(172.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = cardShape,
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(cardShape)
                .background(Color.White.copy(alpha = 0.8f))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Image
                Image(
                    painter = painterResource(
                        if (point.imageUrl.isNotEmpty()) R.drawable.algogaz else R.drawable.optimum
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                )

                Box(
                    modifier = Modifier
                        .width(131.dp)
                        .height(19.dp)
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = point.name,
                        color = Color.White,
                       style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Statut stock
                Image(
                    painter = painterResource(if (point.stockAvailable) R.drawable.ok else R.drawable.sad),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = if (point.stockAvailable) "STOCK DISPONIBLE" else "STOCK INDISPONIBLE",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall.copy(
                        lineHeight = 16.sp,
                    ),

                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(112.dp)
                        .padding(vertical = 4.dp)
                )

                // Bouton Acheter
                Button(
                    onClick = onBuyClick,
                    enabled = point.stockAvailable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .width(91.dp)
                        .height(35.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        stringResource(R.string.home_sheet_btn_order),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Bouton Itinéraire
                Button(
                    onClick = onRouteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .width(76.dp)
                        .height(24.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        stringResource(R.string.home_sheet_btn_route),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
        Box(
            modifier = Modifier
                .size(width = 14.dp, height = 28.dp)
                .clip(pointerShape)
                .background(Color.White.copy(alpha = 0.8f))
        )
    }
}

