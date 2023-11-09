package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.sqldelight.Category
import com.example.sqldelight.Spending
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SpendingScreen(
    onItemClick: (Long) -> Unit,
    onSortClick: (SpendingSortType) -> Unit,
    onAddClick: (String, Double, Category) -> Unit,
    onCategoryClick: (String) -> Unit,
    chosenSortType: SpendingSortType,
    category: List<Category>,
    spendings: List<Spending>,
    modifier: Modifier = Modifier
) {
    val stateVertical = rememberLazyGridState()
    var dialog by remember { mutableStateOf(false) }
    var showScrollbar by remember { mutableStateOf(false) }


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Enter) { showScrollbar = true }
            .onPointerEvent(PointerEventType.Exit) { showScrollbar = false }
    ) {

        Box {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Text(
                        text = "Zarządzaj wydatkami",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { }
                    )
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                    ) {
                        SortPicker(
                            onSortClick = onSortClick,
                            value = chosenSortType.sortType,
                        )
                    }

                    OutlinedButton(
                        onClick = { showBottomSheet = !showBottomSheet },
                        modifier = Modifier
                            .wrapContentSize(Alignment.BottomEnd)
                            .padding(top = 24.dp, bottom = 24.dp, end = 36.dp)
                    ) {
                        Text("Filtruj")
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(
                            painterResource("drawable/icons/filter.svg"),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    state = stateVertical
                ) {

                    items(spendings) {
                        SingleSpendingItem(
                            onItemClick,
                            it
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(128.dp))

                    }
                }
            }
            ExtendedFloatingActionButton(
                text = { Text("Dodaj wydatek") },
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
                spendingDialog(onAddClick, { dialog = !dialog }, category)
            }
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                item {
                    Text(
                        text = "Pokaż wszystkie",
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                onCategoryClick("")
                            }
                    ) 
                }
                items(category) {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                onCategoryClick(it.name)
                            }
                    )
                }
            }
            Row {
                TextButton(
                    onClick = {
                        onCategoryClick("")
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Wyczyść filtrowanie")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Gotowe")
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheet() {

}

@Composable
fun spendingDialog(
    onAddClick: (String, Double, Category) -> Unit,
    onCloseDialog: () -> Unit,
    category: List<Category>
) {

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("Wybierz kategorię") }
    var chosenCategory by remember { mutableStateOf(0) }

    Row {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(48.dp)
                .fillMaxHeight()
                .wrapContentWidth()

        ) {
            Text(
                text = "Dodaj wydatek",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = name,
                label = { Text("Nazwa") },
                onValueChange = { name = it },
                modifier = Modifier
                    .padding(12.dp)
                    .focusable()
//                            .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(36.dp))
            OutlinedTextField(
                value = categoryName,
                label = { Text("Kategria") },
                singleLine = true,
                readOnly = true,
                onValueChange = { },
                modifier = Modifier
                    .padding(12.dp)
                    .focusable()

//                            .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(36.dp))
            OutlinedTextField(
                value = amount,
                label = { Text("Kwota") },
                onValueChange = { amount = it },
                modifier = Modifier
                    .padding(12.dp)
                    .focusable()
//                            .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    onAddClick(name, amount.toDouble(), category[chosenCategory])
                    run { onCloseDialog() }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Dodaj")
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(48.dp)
                .fillMaxHeight()
                .weight(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp)
            ) {
                items(category) {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                chosenCategory = category.indexOf(it)
                                categoryName = it.name
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun SingleSpendingItem(
    onItemClick: (Long) -> Unit,
    item: Spending
) {
    Row(
        modifier = Modifier
            .width(300.dp)
            .height(90.dp)
            .padding(end = 36.dp)
            .clickable { onItemClick(item.id) }
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .padding(4.dp)
        ) {
            Text(
                text = item.name,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.category,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "%.2f zł".format(item.amount),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .align(Alignment.End)
            )

        }
    }
}

@Composable
fun SortPicker(
    onSortClick: (SpendingSortType) -> Unit,
    value: String,
    modifier: Modifier = Modifier
) {
    val width = 225.dp
    var expanded by remember { mutableStateOf(false) }
    val arrowOrientation: Float by animateFloatAsState(
        targetValue = if (expanded) 180F else 0F,
        animationSpec = spring(
            stiffness = 1000f
        )
    )

    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            singleLine = true,
            readOnly = true,
            leadingIcon = {
                Icon(
                    painterResource("drawable/icons/sorting.svg"),
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(arrowOrientation)
                )
            },
            modifier = Modifier
                .width(width)

        )
        Spacer(
            modifier = Modifier
                .width(width)
                .height(56.dp)
                .clickable { expanded = !expanded }
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.width(width)
    ) {
        DropdownMenuItem(
            text = { Text(SpendingSortType.NAME_INC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.NAME_INC)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(SpendingSortType.NAME_DEC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.NAME_DEC)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(SpendingSortType.AMOUNT_INC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.AMOUNT_INC)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(SpendingSortType.AMOUNT_DEC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.AMOUNT_DEC)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(SpendingSortType.DATE_INC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.DATE_INC)
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text(SpendingSortType.AMOUNT_DEC.sortType) },
            onClick = {
                onSortClick(SpendingSortType.DATE_DEC)
                expanded = false
            }
        )

    }
}