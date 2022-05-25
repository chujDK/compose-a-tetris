package com.chuj.compose_a_tetris

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chuj.compose_a_tetris.logic.ScoreContract
import com.chuj.compose_a_tetris.logic.ScoreDBHelper
import com.chuj.compose_a_tetris.ui.theme.Compose_a_tetrisTheme
import com.google.android.material.composethemeadapter3.createMdc3Theme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import kotlin.jvm.internal.Intrinsics

class ScoreSearchViewModel : ViewModel() {
    private val _viewState = mutableStateOf(ScoreSearchViewState())
    val viewState : State<ScoreSearchViewState> = _viewState

    private fun reduce(newViewState: ScoreSearchViewState) {
        _viewState.value = newViewState
    }

    fun reduceSearchAlert(onSearchAlert: Boolean) {
        reduce(viewState.value.copy(onSearchAlert = onSearchAlert))
    }

    fun reduceRecords(records: List<ScoreContract.Record>) {
        reduce(viewState.value.copy(
            records = records,
            recordsWasSet = true,
        ))
    }

    fun reduceSearchTime(
        timeStart : Long = viewState.value.searchTimeStart,
        timeEnd : Long = viewState.value.searchTimeEnd
    ) {
        _viewState.value = viewState.value.copy(
            searchTimeEnd = timeEnd,
            searchTimeStart = timeStart
        )
    }
}

data class ScoreSearchViewState (
    val records: List<ScoreContract.Record> = emptyList(),
    val searchTimeStart : Long = 0L,
    val searchTimeEnd : Long = 0L,
    val onSearchAlert : Boolean = false,
    val recordsWasSet : Boolean = false,
)

class ScoreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_a_tetrisTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val viewModel = viewModel<ScoreSearchViewModel>()
                    val viewState = viewModel.viewState

                    if (!viewState.value.recordsWasSet) {
                        val dbHelper = ScoreDBHelper(context)
                        val allRecords = dbHelper.selectAll()
                        val allRecordsSorted = allRecords.sortedByDescending { it.score }
                        viewModel.reduceRecords(allRecordsSorted)
                    }

                    HighScoreScreen()

                    if (viewState.value.onSearchAlert) {
                        SearchAlert()
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreList(modifier: Modifier = Modifier) {
    val viewModel = viewModel<ScoreSearchViewModel>()
    val viewState = viewModel.viewState

    val records = remember { mutableStateListOf<ScoreContract.Record>() }

    records.clear()
    records.addAll(viewState.value.records)

    LazyColumn(
        modifier = modifier,

    ) {
        items(records) { record ->
            ScoreItem(score = record, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
@SuppressLint("SimpleDateFormat")
fun ScoreItem(
    modifier: Modifier = Modifier,
    score : ScoreContract.Record = ScoreContract.Record(100, 1653213538, "chuj")
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color.Red, Color.Blue, Color.Green),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .border(
                brush = gradientBrush,
                width = 2.dp,
                shape = CircleShape
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = score.name,
                fontSize = 22.sp
            )
            Text(
                text = score.score.toString(),
                fontSize = 20.sp,
            )
        }

        Text(text = SimpleDateFormat("yy/MM/dd HH:mm:ss").format(score.currentTimeMillis))
    }
}

@Composable
fun BasicButton(onClick : () -> Unit = {}, text : String = "") {
    Button(
        modifier = Modifier.height(50.dp),
        onClick = onClick
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ExitButton() {
    val context = LocalContext.current
    BasicButton(
        onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        },
        text = stringResource(id = R.string.button_exit_str)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchRecordButton() {
    val viewModel = viewModel<ScoreSearchViewModel>()
    BasicButton(
        onClick = {
            viewModel.reduceSearchAlert(true)
        },
        text = stringResource(id = R.string.search_record_button_str)
    )
}

enum class SearchType {
    ByName,
    ByTime,
    OnDeciding,
}

enum class TimeSetState {
    OnSetStartTime,
    OnSetEndTime,
    OnInit,
}

@Preview(showBackground = true)
@Composable
@SuppressLint("SimpleDateFormat")
fun SearchAlert() {
    val context = LocalContext.current
    var searchType by remember { mutableStateOf(SearchType.OnDeciding)}
    val viewModel = viewModel<ScoreSearchViewModel>()
    val viewState = viewModel.viewState
    val dbHelper = ScoreDBHelper(context)
    var searchName by remember { mutableStateOf("") }
    var timeSetState by remember { mutableStateOf(TimeSetState.OnInit) }
    var timeInput by remember { mutableStateOf("00:00") }
    var dateInput by remember { mutableStateOf("2000/01/01") }
    val currentStartYear = dateInput.subSequence(0, 4).toString().toInt()
    val currentStartMonth = dateInput.subSequence(5, 7).toString().toInt()
    val currentStartDay = dateInput.subSequence(8, 10).toString().toInt()
    val currentStartHour = timeInput.subSequence(0, 2).toString().toInt()
    val currentStartMinute = timeInput.subSequence(3, 5).toString().toInt()
    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute : Int ->
            timeInput = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        }, currentStartHour, currentStartMinute, false
    )
    val datePickerDialog = DatePickerDialog(
        context,
        {_, year : Int, month : Int, dayOfMonth : Int ->
            dateInput = "$year/${(month + 1).toString().padStart(2, '0')}/${dayOfMonth.toString().padStart(2, '0')}"
        }, currentStartYear, currentStartMonth, currentStartDay
    )

    var searchStartTime by remember { mutableStateOf(0L) }
    var searchEndTime by remember { mutableStateOf(0L) }

    val timeToParse = "$dateInput $timeInput"
    println("[!] $timeToParse")
    if (timeSetState == TimeSetState.OnSetStartTime || timeSetState == TimeSetState.OnInit) {
        searchStartTime = SimpleDateFormat("yyyy/MM/dd HH:mm").parse(timeToParse).time
        println("[+] start time set $searchStartTime")
    }
    if (timeSetState == TimeSetState.OnSetEndTime || timeSetState == TimeSetState.OnInit) {
        searchEndTime = SimpleDateFormat("yyyy/MM/dd HH:mm").parse(timeToParse).time
        println("[+] end time set $searchStartTime")
    }

    if (viewState.value.onSearchAlert) {
        when(searchType) {
            SearchType.OnDeciding -> AlertDialog(
                onDismissRequest = { viewModel.reduceSearchAlert(false) },
                title = {
                    Text(text = "Search")
                },
                text = {
                    Text(text = "What you want to search by?")
                },
                confirmButton = {
                    TextButton(
                        onClick = { searchType = SearchType.ByName }
                    ) {
                        Text(text = "By Name")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { searchType = SearchType.ByTime }
                    ) {
                        Text(text = "By Time")
                    }
                }
            )
            SearchType.ByTime -> AlertDialog(
                onDismissRequest = { viewModel.reduceSearchAlert(false) },
                title = {
                    Text(text = "Search By Time")
                },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClickableText(
                            text = AnnotatedString(SimpleDateFormat("yy/MM/dd ")
                                .format(searchStartTime)),
                            onClick = {
                                timeSetState = TimeSetState.OnSetStartTime
                                datePickerDialog.show()
                            }
                        )
                        ClickableText(
                            text = AnnotatedString(SimpleDateFormat("HH:mm")
                                .format(searchStartTime)),
                            onClick = {
                                timeSetState = TimeSetState.OnSetStartTime
                                timePickerDialog.show()
                            }
                        )
                        Text(
                            text = "   To   "
                        )
                        ClickableText(
                            text = AnnotatedString(SimpleDateFormat("yy/MM/dd ")
                                .format(searchEndTime)),
                            onClick = {
                                timeSetState = TimeSetState.OnSetEndTime
                                datePickerDialog.show()
                            }
                        )
                        ClickableText(
                            text = AnnotatedString(SimpleDateFormat("HH:mm")
                                .format(searchEndTime)),
                            onClick = {
                                timeSetState = TimeSetState.OnSetEndTime
                                timePickerDialog.show()
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val records = dbHelper.searchScoreByTime(searchStartTime, searchEndTime)
                        viewModel.reduceRecords(records = records)
                        viewModel.reduceSearchAlert(false)
                    }) {
                        Text(text = "Search!")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.reduceSearchAlert(false) }) {
                        Text(text = "Cancel")
                    }
                }
            )
            SearchType.ByName -> AlertDialog(
                onDismissRequest = { viewModel.reduceSearchAlert(false) },
                title = {
                    Text(text = "Search By Name")
                },
                text = {
                    Text(text = "Enter a name")
                    TextField(
                        value = searchName,
                        onValueChange = {
                            searchName = it
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val records = dbHelper.searchScoreByName(name = searchName)
                        viewModel.reduceRecords(records = records)
                        viewModel.reduceSearchAlert(false)
                    }) {
                        Text(text = "Search!")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.reduceSearchAlert(false) }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlButtonsDisplay(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchRecordButton()
        ExitButton()
    }
}

@Preview(showBackground = true)
@Composable
fun HighScoreScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoreList(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
        )

        ControlButtonsDisplay(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp))
    }
}