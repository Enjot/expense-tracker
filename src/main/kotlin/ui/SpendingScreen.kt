package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sqldelight.Spending

@Composable
fun SpendingScreen(
    spendings: List<Spending>,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val stateVertical = rememberLazyGridState()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box {
            Column {
                Row {
                    Text(
                        text = "Zarządzaj Wydatkami",
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(300.dp),
                    state = stateVertical
                ) {
                    items(spendings) {
                        SingleSpendingItem(it)
                    }
                }
            }
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(48.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(stateVertical),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .wrapContentHeight()
            )
        }
    }

}


@Composable
fun SingleSpendingItem(
    item: Spending
) {
    Row(
        modifier = Modifier
            .width(300.dp)
            .height(70.dp)
            .padding(end = 36.dp)
            .clickable { }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Text(
                text = item.name,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.category,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Text(
                text = item.date,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "%.2f zł".format(item.amount),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .align(Alignment.End)
            )
        }
    }
}
