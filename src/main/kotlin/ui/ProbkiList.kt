package ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.ProbkaDTO

// 📜 Probki List

@Composable
fun ProbkiList(
    probki: List<ProbkaDTO>,
    onTechnologiaSave: (ProbkaDTO, String?, String?, String?, String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header z licznikiem
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = Color.Transparent
        ) {
            Text(
                "Znaleziono: ${probki.size} próbek",
                style = MaterialTheme.typography.subtitle1,
                color = AppColors.Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        val listState = rememberLazyListState()

        // Lista
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(probki) { probka ->
                    ProbkaCard(
                        probka = probka,
                        onTechnologiaSave = { k1, k2, k3, k4 ->
                            onTechnologiaSave(probka, k1, k2, k3, k4)
                        }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState = listState)
            )
        }
    }
}
