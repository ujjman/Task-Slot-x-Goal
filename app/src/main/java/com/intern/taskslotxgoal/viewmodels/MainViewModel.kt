package com.intern.taskslotxgoal.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intern.taskslotxgoal.MainActivity
import com.intern.taskslotxgoal.R
import com.intern.taskslotxgoal.ui.theme.EnabledButton
import com.intern.taskslotxgoal.ui.theme.RedButton

class MainViewModel : ViewModel() {

    private val _time = MutableLiveData(0)
    val time: MutableLiveData<Int> = _time
    private val _remainingTime = MutableLiveData(0)
    val remainingTime: MutableLiveData<Int> = _remainingTime

    private val _remainingHour = MutableLiveData(0)
    val remainingHour: MutableLiveData<Int> = _remainingHour
    private val _remainingMin = MutableLiveData(0)
    val remainingMin: MutableLiveData<Int> = _remainingMin
    private val _remainingSec = MutableLiveData(0)
    val remainingSec: MutableLiveData<Int> = _remainingSec

    private val _isCompleted = MutableLiveData(false)
    val isCompleted: MutableLiveData<Boolean> = _isCompleted
    private val _showGrantOverlayDialog = MutableLiveData(false)
    val showGrantOverlayDialog: MutableLiveData<Boolean> = _showGrantOverlayDialog
    private val _tagSelected = MutableLiveData(false)
    val tagSelected: MutableLiveData<Boolean> = _tagSelected

    private val _firstText = MutableLiveData("")
    val firstText: MutableLiveData<String> = _firstText
    private val _firstTextContinued = MutableLiveData("")
    val firstTextContinued: MutableLiveData<String> = _firstTextContinued
    private val _secondText = MutableLiveData("")
    val secondText: MutableLiveData<String> = _secondText
    private val _thirdText = MutableLiveData("")
    val thirdText: MutableLiveData<String> = _thirdText

    private val _maxProgress = MutableLiveData(100f)
    val maxProgress: MutableLiveData<Float> = _maxProgress
    private val _progressValue = MutableLiveData(100f)
    val progressValue: MutableLiveData<Float> = _progressValue

    private val _buttonColor = MutableLiveData(EnabledButton)
    val buttonColor: MutableLiveData<Color> = _buttonColor
    private val _buttonText = MutableLiveData("Start")
    val buttonText: MutableLiveData<String> = _buttonText


    private var twentyFivePercentOfTotalTime: Int = 0
    private var fiftyPercentOfTotalTime: Int = 0
    private var seventyFivePercentOfTotalTime: Int = 0


    private var countDownTimer: CountDownTimer? = null
    private var player: MediaPlayer = MediaPlayer()

    fun checkHr(hr: Int): Boolean {
        return hr <= 99
    }

    fun checkMin(min: Int): Boolean {
        return min <= 59
    }

    fun checkSec(sec: Int): Boolean {
        return sec <= 59
    }

    fun checkTime(hr: String, min: String, sec: String): Int {
        if (hr.isEmpty() || min.isEmpty() || sec.isEmpty()) {
            return 1
        }
        val totSec = (hr.toInt() * 3600) + (min.toInt() * 60) + (sec.toInt())
        return if (totSec <= 7200) 2
        else 3
    }

    fun setTime(hr: Int, min: Int, sec: Int) {
        val totSec = (hr * 3600) + (min * 60) + (sec)
        remainingHour.value = hr
        remainingMin.value = min
        remainingSec.value = sec
        time.value = totSec
        twentyFivePercentOfTotalTime = time.value!!.toInt() - (0.25 * time.value!!).toInt()
        fiftyPercentOfTotalTime = time.value!!.toInt() - (0.50 * time.value!!).toInt()
        seventyFivePercentOfTotalTime = time.value!!.toInt() - (0.75 * time.value!!).toInt()
        maxProgress.value = totSec * 1f
        progressValue.value = totSec * 1f
    }

    fun changeFirstText(subject: String) {
        firstText.value = "You'll be studying ${subject.substring(6)} for "
        firstTextContinued.value =
            " ${remainingHour.value} hr ${remainingMin.value} min ${remainingSec.value} sec"
    }

    fun changeSecondText() {
        secondText.value = "Goal"
    }

    fun changeThirdText(subject: String) {
        thirdText.value =
            "$subject for ${remainingHour.value} hr ${remainingMin.value} min ${remainingSec.value} sec"
    }

    fun setTimer(context: Context) {

        if (buttonText.value.equals("End", true)) {
            countDownTimer?.cancel()
            time.value = 0
            buttonColor.value = EnabledButton
            buttonText.value = "New Goal"
            progressValue.value = 0f
            remainingTime.value = 0
            remainingSec.value = 0
            remainingMin.value = 0
            remainingHour.value = 0
            firstText.value = "Timer was cancelled in between."
            firstTextContinued.value = ""
            cancelNotification(context,1398)
            return
        }
        remainingTime.value = time.value
        buttonColor.value = RedButton
        buttonText.value = "End"
        countDownTimer = object : CountDownTimer((time.value!!) * 1000L, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                remainingTime.value = remainingTime.value!! - 1
                remainingHour.value = remainingTime.value!! / 3600
                remainingMin.value = (remainingTime.value!! / 60) - (remainingHour.value!! * 60)
                if (remainingSec.value!! - 1 == -1 && remainingMin.value!! > 0) remainingSec.value =
                    59
                else remainingSec.value = remainingSec.value!! - 1
                progressValue.value = progressValue.value?.minus(1f)
                when (remainingTime.value) {
                    twentyFivePercentOfTotalTime -> {
                        firstText.value = "Great going! You've completed 25% of your goal."
                        firstTextContinued.value = ""
                    }
                    fiftyPercentOfTotalTime -> {
                        firstText.value = "Great going! You've completed 50% of your goal."
                        firstTextContinued.value = ""
                    }
                    seventyFivePercentOfTotalTime -> {
                        firstText.value = "Great going! You've completed 75% of your goal."
                        firstTextContinued.value = ""
                    }
                }
            }


            override fun onFinish() {
                createNotificationChannel(context)
                player.start()
                time.value = 0
                firstText.value = "Well done! You've achieved your goal."
                buttonColor.value = EnabledButton
                buttonText.value = "New Goal"
                isCompleted.value = true
            }
        }.start()
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task App"
            val descriptionText = "Timer ended"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_ENDED_NOTIFICATION", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder =
            NotificationCompat.Builder(context, "TASK_ENDED_NOTIFICATION").setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Timer ended")
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(1398, builder.build())
        }
    }

    private fun cancelNotification(ctx: Context, notifyId: Int) {
        val ns = Context.NOTIFICATION_SERVICE
        val nMgr = ctx.getSystemService(ns) as NotificationManager
        nMgr.cancel(notifyId)
    }

    fun createMediaPlayer(context: Context)
    {
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        player = MediaPlayer.create(context, alarmSound)
        player.isLooping = true
    }

    fun exit() {
        player.pause()
        countDownTimer?.cancel()

        time.value = 0
        remainingTime.value = 0


        remainingHour.value = 0
        remainingMin.value = 0
        remainingSec.value = 0

        isCompleted.value = false
        showGrantOverlayDialog.value = false
        tagSelected.value = false

        firstText.value = ""
        firstTextContinued.value = ""
        secondText.value = ""
        thirdText.value = ""

        maxProgress.value = 100f
        progressValue.value = 100f

        buttonColor.value = EnabledButton
        buttonText.value = "Start"


        twentyFivePercentOfTotalTime = 0
        fiftyPercentOfTotalTime = 0
        seventyFivePercentOfTotalTime = 0


        countDownTimer = null
        player=MediaPlayer()

    }
}