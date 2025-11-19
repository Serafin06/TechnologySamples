package pl.rafapp.techSam.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.rafapp.techSam.Base.ProbkaDTO

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
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Lista
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
    }
}
