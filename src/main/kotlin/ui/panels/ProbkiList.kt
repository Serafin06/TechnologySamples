package ui.panels

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.FlagType
import base.ProbkaDTO
import ui.AppColors

@Composable
fun ProbkiList(
    probki: List<ProbkaDTO>,
    onTechnologiaSave: (Int, String?, String?, String?, String?) -> Unit,
    onFlagUpdate: (Int, FlagType, Boolean) -> Unit
) {
    Column(modifier = Modifier.Companion.fillMaxSize()) {
        // Header z licznikiem
        Surface(
            modifier = Modifier.Companion.fillMaxWidth().padding(horizontal = 16.dp),
            color = Color.Companion.Transparent
        ) {
            Text(
                "Znaleziono: ${probki.size} próbek",
                style = MaterialTheme.typography.subtitle1,
                color = AppColors.Primary,
                fontWeight = FontWeight.Companion.Bold,
                modifier = Modifier.Companion.padding(vertical = 8.dp)
            )
        }

        val listState = rememberLazyListState()

        // Lista
        Box(
            modifier = Modifier.Companion.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.Companion.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(probki) { probka ->
                    ProbkaCard(
                        probka = probka,
                        onTechnologiaSave = { k1, k2, k3, k4 ->
                            onTechnologiaSave(probka.numer, k1, k2, k3, k4)
                        },
                        onFlagUpdate = { flagType, value ->
                            onFlagUpdate(probka.numer, flagType, value)
                        }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.Companion.fillMaxHeight().align(Alignment.Companion.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState = listState)
            )
        }
    }
}