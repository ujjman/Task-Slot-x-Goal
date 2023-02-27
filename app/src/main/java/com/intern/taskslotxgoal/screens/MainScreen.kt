package com.intern.taskslotxgoal.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.intern.taskslotxgoal.R
import com.intern.taskslotxgoal.Screen
import com.intern.taskslotxgoal.ui.theme.DisabledButton
import com.intern.taskslotxgoal.ui.theme.TimerProgress
import com.intern.taskslotxgoal.viewmodels.MainViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController, mainViewModel: MainViewModel
) {
    var firstText by remember {
        mutableStateOf(navController.context.getString(R.string.tap_circle))
    }
    var firstTextTime by remember {
        mutableStateOf("")
    }
    var secondText by remember {
        mutableStateOf(navController.context.getString(R.string.tag_select))
    }
    var thirdText by remember {
        mutableStateOf("")
    }
    var showTimerDialog by remember {
        mutableStateOf(false)
    }
    var progressValue by remember {
        mutableStateOf(mainViewModel.maxProgress.value)
    }
    var buttonColor by remember {
        mutableStateOf(mainViewModel.buttonColor.value)
    }
    var buttonText by remember {
        mutableStateOf(mainViewModel.buttonText.value)
    }

    val progress by animateFloatAsState(progressValue!!)
    var mSelectedText by remember { mutableStateOf(navController.context.getString(R.string.select_text)) }
    val mList =
        listOf("Study Physics", "Study Chemistry", " Study Maths", "Study Biology", "Study English")
    var mExpanded by remember { mutableStateOf(false) }

    mainViewModel.firstText.observe(navController.context as LifecycleOwner) {
        if (it.isNotEmpty()) {
            firstText = it
        }

    }
    mainViewModel.firstTextContinued.observe(navController.context as LifecycleOwner) {
        firstTextTime = it
    }
    mainViewModel.secondText.observe(navController.context as LifecycleOwner) {
        if (it.isNotEmpty()) {
            secondText = it
        }
    }
    mainViewModel.thirdText.observe(navController.context as LifecycleOwner) {
        if (it.isNotEmpty()) {
            thirdText = it
        }
    }
    mainViewModel.showGrantOverlayDialog.observe(navController.context as LifecycleOwner) {
        showTimerDialog = it
    }
    mainViewModel.progressValue.observe(navController.context as LifecycleOwner) {
        progressValue = it
    }
    mainViewModel.buttonColor.observe(navController.context as LifecycleOwner) {
        buttonColor = it
    }
    mainViewModel.buttonText.observe(navController.context as LifecycleOwner) {
        buttonText = it
    }

    mainViewModel.createMediaPlayer(navController.context)
    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp
    else Icons.Filled.KeyboardArrowDown
    when (showTimerDialog) {
        true -> {
            SetTime(context = navController.context, mainViewModel = mainViewModel, selectedText = mSelectedText)
        }
        else -> {}
    }


    DisposableEffect(Unit) {
        onDispose {
            mainViewModel.exit()
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBar(modifier = Modifier.fillMaxWidth(), backgroundColor = White, title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Task Slot x Goal",
                    style = MaterialTheme.typography.h6,
                    color = Black,
                    textAlign = TextAlign.Center
                )
            })
        }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(0.8f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.padding(top = 30.dp))
                    CircularProgressBar(
                        modifier = Modifier.size(250.dp),
                        progress = progress,
                        progressMax = mainViewModel.maxProgress.value!!,
                        progressBarColor = TimerProgress,
                        progressBarWidth = 10.dp,
                        backgroundProgressBarColor = LightGray,
                        backgroundProgressBarWidth = 10.dp,
                        roundBorder = true,
                        startAngle = 360f,
                        mainViewModel = mainViewModel
                    )
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                    Row {
                        Text(
                            text = firstText, style = MaterialTheme.typography.h1
                        )
                        Text(text = firstTextTime, style = MaterialTheme.typography.h4)
                    }

                    Spacer(modifier = Modifier.padding(top = 50.dp))
                    Text(
                        text = secondText, style = MaterialTheme.typography.h2
                    )

                    Spacer(modifier = Modifier.padding(top = 20.dp))


                    when (thirdText) {
                        "" -> {
                            Box(modifier = Modifier.clickable {
                                    mExpanded = !mExpanded
                                }) {
                                Row {
                                    Text(
                                        text = mSelectedText, style = MaterialTheme.typography.h3
                                    )
                                    Spacer(modifier = Modifier.padding(start = 10.dp))
                                    Icon(icon, "contentDescription", tint = Blue)
                                    DropdownMenu(
                                        expanded = mExpanded,
                                        onDismissRequest = { mExpanded = false },
                                        properties = PopupProperties(focusable = true)
                                    ) {
                                        mList.forEach { label ->
                                            DropdownMenuItem(onClick = {
                                                mExpanded = false
                                                mSelectedText = label
                                                mainViewModel.changeFirstText(
                                                    subject = mSelectedText
                                                )
                                                mainViewModel.tagSelected.value = true
                                            }) {
                                                Text(text = label)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = thirdText, style = MaterialTheme.typography.h3
                            )
                        }
                    }
                    when (mainViewModel.showGrantOverlayDialog.value) {
                        true -> {
                            SetTime(context = navController.context, mainViewModel = mainViewModel, selectedText = mSelectedText)
                        }
                        else -> {}
                    }

                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                                  handleButtonClick(mainViewModel = mainViewModel, navController = navController, mSelectedText = mSelectedText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = mainViewModel.tagSelected.value == true,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = buttonColor!!,
                            disabledBackgroundColor = DisabledButton
                        )

                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "$buttonText")
                        }
                    }

                }
            }

        }
    }
}


@Composable
fun CircularProgressBar(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    progressMax: Float = 100f,
    progressBarColor: Color = Black,
    progressBarWidth: Dp = 7.dp,
    backgroundProgressBarColor: Color = Color.Gray,
    backgroundProgressBarWidth: Dp = 3.dp,
    roundBorder: Boolean = false,
    startAngle: Float = 0f,
    mainViewModel: MainViewModel
) {

    val interactionSource = remember { MutableInteractionSource() }
    var hr by remember {
        mutableStateOf("")
    }
    var min by remember {
        mutableStateOf("")
    }
    var sec by remember {
        mutableStateOf("")
    }


    mainViewModel.remainingHour.observe(LocalContext.current as LifecycleOwner) {
        hr = it.toString()
        if (hr.length == 1) {
            hr = "0$hr"
        }
    }
    mainViewModel.remainingMin.observe(LocalContext.current as LifecycleOwner) {
        min = it.toString()
        if (min.length == 1) {
            min = "0$min"
        }
    }
    mainViewModel.remainingSec.observe(LocalContext.current as LifecycleOwner) {
        sec = it.toString()
        if (sec.length == 1) {
            sec = "0$sec"
        }
    }



    Box(contentAlignment = Alignment.Center, modifier = modifier
        .fillMaxSize()
        .clickable(
            interactionSource = interactionSource, indication = null
        ) {
            if(mainViewModel.buttonText.value?.equals("End",true)!! || mainViewModel.buttonText.value?.equals("New Goal",true)!!)
            {
                return@clickable
            }
            mainViewModel.showGrantOverlayDialog.value = true
        }) {
        var radius by remember {
            mutableStateOf(0f)
        }
        Canvas(modifier = modifier.fillMaxSize()) {

            val canvasSize = size.minDimension
            radius = canvasSize / 2 - maxOf(backgroundProgressBarWidth, progressBarWidth).toPx() / 2
            drawCircle(
                color = backgroundProgressBarColor,
                radius = radius,
                center = size.center,
                style = Stroke(width = backgroundProgressBarWidth.toPx())
            )

            drawArc(
                color = progressBarColor,
                startAngle = 270f + startAngle,
                sweepAngle = (progress / progressMax) * 360f,
                useCenter = false,
                topLeft = size.center - Offset(radius, radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = progressBarWidth.toPx(),
                    cap = if (roundBorder) StrokeCap.Round else StrokeCap.Butt
                )
            )
        }
        Text(
            text = "$hr:$min:$sec", color = Black, fontSize = 35.sp, fontWeight = SemiBold
        )
    }
}

@Composable
private fun SetTime(context: Context, mainViewModel: MainViewModel, selectedText: String) {
    var hr by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var sec by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
        mainViewModel.showGrantOverlayDialog.value = false
    }, confirmButton = {
        Button(onClick = {
            if (mainViewModel.checkTime(hr = hr, min = min, sec = sec) == 2) {
                mainViewModel.setTime(hr = hr.toInt(), min = min.toInt(), sec = sec.toInt())
                mainViewModel.showGrantOverlayDialog.value = false
                mainViewModel.changeFirstText(selectedText)
            } else if (mainViewModel.checkTime(hr = hr, min = min, sec = sec) == 1) {
                Toast.makeText(context, "Please enter data in all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter time less than 2 hrs", Toast.LENGTH_SHORT)
                    .show()
            }
        }) {
            Text("Save")
        }
    }, dismissButton = {
        Button(onClick = {
            mainViewModel.showGrantOverlayDialog.value = false
        }) {
            Text("Cancel")
        }
    }, title = {
        Text("Set Timer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }, text = {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = hr,
                singleLine = true,
                onValueChange = {
                    if (it.isEmpty()) {
                        hr = it
                    } else if (it.isNotEmpty() && mainViewModel.checkHr(it.toInt())) {
                        hr = it
                    }
                },
                textStyle = TextStyle(color = Black, fontSize = 20.sp),
                label = { Text("hr") },
                placeholder = { Text(text = "hh") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = White,
                    focusedLabelColor = Black,
                    unfocusedLabelColor = Black,
                    cursorColor = Black,
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black
                )
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = min,
                singleLine = true,
                onValueChange = {
                    if (it.isEmpty()) {
                        min = it
                    } else if (it.isNotEmpty() && mainViewModel.checkMin(it.toInt())) {
                        min = it
                    }
                },
                textStyle = TextStyle(color = Black, fontSize = 20.sp),
                label = { Text("min") },
                placeholder = { Text(text = "mm") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = White,
                    focusedLabelColor = Black,
                    unfocusedLabelColor = Black,
                    cursorColor = Black,
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black
                )
            )
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = sec,
                singleLine = true,
                onValueChange = {
                    if (it.isEmpty()) {
                        sec = it
                    } else if (it.isNotEmpty() && mainViewModel.checkSec(it.toInt())) {
                        sec = it
                    }

                },
                textStyle = TextStyle(color = Black, fontSize = 20.sp),
                label = { Text("sec") },
                placeholder = { Text(text = "ss") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = White,
                    focusedLabelColor = Black,
                    unfocusedLabelColor = Black,
                    cursorColor = Black,
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black
                )
            )
        }
    })
}

fun handleButtonClick(mainViewModel: MainViewModel, navController: NavHostController, mSelectedText: String) {
    if (mainViewModel.buttonText.value.equals("New Goal",true)) {
        mainViewModel.exit()
        navController.navigate(Screen.MainScreen.route)
    } else {
        mainViewModel.changeSecondText()
        mainViewModel.changeThirdText(mSelectedText)
        mainViewModel.setTimer(navController.context)
    }
}




