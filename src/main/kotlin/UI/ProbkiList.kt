package pl.rafapp.techSam.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.rafapp.techSam.Base.ProbkaDTO

// 📜 Probki List

@Composable
fun ProbkiList(probki: List<ProbkaDTO>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header z licznikiem
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = Color.Transparent
        ) {
            Text(
                "Znaleziono: ${probki.size} próbek",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
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
                ProbkaCard(probka)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 🃏 Probka Card
// ═══════════════════════════════════════════════════════════════

@Composable
fun ProbkaCard(probka: ProbkaDTO) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Assignment,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Zamówienie #${probka.numer}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Podstawowe info
            InfoRow("Oddział", probka.oddzialNazwa, Icons.Default.Business)
            InfoRow("Data", probka.dataZamowienia.toString().take(10), Icons.Default.CalendarToday)
            probka.art?.let { InfoRow("ART", it, Icons.Default.Label) }
            probka.receptura?.let { InfoRow("Receptura", it, Icons.Default.Science) }

            Spacer(Modifier.height(12.dp))

            // Statusy w kompaktowej formie
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip("ZO", probka.statusZO, Modifier.weight(1f))
                StatusChip("ZK", probka.statusZK, Modifier.weight(1f))
                StatusChip("ZD", probka.statusZD, Modifier.weight(1f))
            }

            // Rozwinięte szczegóły
            if (expanded) {
                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                Text("Szczegóły produktu", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                DetailRow("Szerokość", probka.szerokosc?.toString() ?: "-")
                DetailRow("Grubość 1", probka.grubosc11 ?: "-")
                DetailRow("Grubość 2", probka.grubosc21 ?: "-")
                DetailRow("Grubość 3", probka.grubosc31 ?: "-")

                Spacer(Modifier.height(16.dp))
                Text("Szczegóły statusów", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                probka.statusZO?.let { StatusDetails("ZO", it) }
                probka.statusZK?.let { StatusDetails("ZK", it) }
                probka.statusZD?.let { StatusDetails("ZD", it) }
            }
        }
    }
}