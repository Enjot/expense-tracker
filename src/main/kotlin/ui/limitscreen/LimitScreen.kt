package ui.limitscreen

import Validator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.sqldelight.Category
import ui.utils.LimitDatePicker

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LimitScreen(
    model: LimitScreenModel
) {

    val limit = model.limits.collectAsState(emptyList())
    val category = model.categories.collectAsState(emptyList())
    var showScrollbar by remember { mutableStateOf(false) }
    val stateVertical = rememberLazyListState()
    var dialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Enter) { showScrollbar = true }
            .onPointerEvent(PointerEventType.Exit) { showScrollbar = false }
    ) {
        Box(modifier = Modifier.padding(start = 36.dp)) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Zarządzaj Limitami",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.weight(1f).clickable { }
                    )
                    Box(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 36.dp)
                    ) {
                        LimitDatePicker(
                            selectedMonth = model.filter.value.selectedMonth,
                            selectedYear = model.filter.value.selectedYear,
                            onClick = { month, year -> model.setFilter(month, year, true) },
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxSize().padding(end = 36.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    LazyColumn(
                        state = stateVertical,
                        modifier = Modifier
                            .weight(6f)
                    ) {
                        items(limit.value){
                            SingleLimitCard(it)
                        }
                        item { Spacer(modifier = Modifier.height(128.dp)) }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

            }
            ExtendedFloatingActionButton(
                text = { Text("Ustal limit") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                onClick = { dialog = true },
                expanded = !stateVertical.canScrollBackward || !stateVertical.canScrollForward,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(48.dp)
            )
            AnimatedVisibility(
                visible = showScrollbar,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(stateVertical),
                    style = ScrollbarStyle(
                        minimalHeight = 16.dp,
                        thickness = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        hoverDurationMillis = 300,
                        unhoverColor = MaterialTheme.colorScheme.outlineVariant,
                        hoverColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .wrapContentHeight()
                )
            }

            if (dialog) {
                AlertDialog(
                    onDismissRequest = { dialog = false },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false
                    ),
                    modifier = Modifier
                        .width(1000.dp)
                        .padding(vertical = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    LimitDialog(
                        {category, amount -> model.insert(category, amount)},
                        { dialog = !dialog },
                        category.value
                    )
                }
            }
        }
    }
}



