package com.example.mybooks.Model.Service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.UseCase
import com.example.mybooks.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch


class Notificacion : IntentService("MyService") {


    private val PROGRESS_MAX             = 100
    private var PROGRESS_CURRENT         = 0
    private var NOTIFICATION_CHANNEL_ID  = ""
    private val notificationId           = 1

    lateinit var builder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        NOTIFICATION_CHANNEL_ID = applicationContext.getString(R.string.app_name)
        builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle (applicationContext.getString(R.string.titleNotificacion))
            setSilent       (true)
            setContentText  (applicationContext.getString(R.string.contentNotificacion))
            setSmallIcon    (R.drawable.ic_logo)
            priority        =NotificationCompat.PRIORITY_LOW
        }

        NotificationManagerCompat.from(this).apply {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var mChannel = getNotificationChannel(
                    NOTIFICATION_CHANNEL_ID // default_channel_id
                )
                if (mChannel == null) {
                    mChannel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID, // default_channel_id, title, importance
                        NOTIFICATION_CHANNEL_ID,
                        NotificationManager.IMPORTANCE_LOW
                    )
                    mChannel.enableVibration(false)
                    createNotificationChannel(mChannel)
                }
            }

            builder.setProgress(
                PROGRESS_MAX,
                PROGRESS_CURRENT,
                false)
            notify(notificationId, builder.build()
            )


        }
        startForeground(1, builder.build())
    }


    @SuppressLint("WrongThread")
    override fun onHandleIntent(p0: Intent?) {
        val countDownLatch = CountDownLatch(1)
        val useCase        = UseCase(this)
        val preferences    = SharedPreferences.getInstance()

        GlobalScope.launch(Dispatchers.Main) {
            val books= useCase.getAllBooks(preferences.getIdUser(this@Notificacion))
            if (books.isNotEmpty()) {
                NotificationManagerCompat.from(this@Notificacion).apply {

                    useCase.saveBooksFirebase(this@Notificacion,books)
                    val listThemes      = mutableListOf<ThemeEntity>()
                    val listContents    = mutableListOf<ContentEntity>()
                    val listTexts       = mutableListOf<TextEntity>()

                    books.forEach { bookEntity ->
                        val themes = useCase.getThemes(bookEntity.id_book)
                        themes.forEach { theme ->
                            listThemes.add(theme)
                            val contents = useCase.getContentById(theme.idTheme)
                            contents.forEach { content ->
                                listContents.add(content)
                                val texts = useCase.getDataContent(content.idContent)
                                texts.forEach { text ->
                                    listTexts.add(text)
                                }
                            }
                        }
                        delay(500)
                        increaseProgress(
                            notificacion = this,
                            progress     = (100 / books.size)
                        )
                    }
                    useCase.saveThemesFirebase (this@Notificacion,listThemes)
                    useCase.saveContentFirebase(this@Notificacion,listContents)
                    useCase.saveTextFirebase   (this@Notificacion,listTexts)
                    countDownLatch.countDown()

                }

            }else{
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()

    }


    fun increaseProgress(notificacion: NotificationManagerCompat, progress: Int): Unit {
        PROGRESS_CURRENT += progress
        builder.setProgress(
            PROGRESS_MAX,
            PROGRESS_CURRENT,
            false
        )

        notificacion.notify(notificationId, builder.build())

    }
}