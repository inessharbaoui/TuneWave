package com.example.music;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.music.R;

public class MusicService extends android.app.Service {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String songName = "Song 1";

    public static final String CHANNEL_ID = "MusicServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.song1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if ("TOGGLE_PLAY".equals(intent.getAction())) {
            if (isPlaying) {
                pauseSong();
            } else {
                playSong();
            }
            updateNotification();
        }

         Intent notificationIntent = new Intent(this, com.example.music.MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent playPausePendingIntent = PendingIntent.getService(this, 0, new Intent(this, MusicService.class).setAction("TOGGLE_PLAY"), PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(songName)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentIntent(pendingIntent)
                .addAction(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Pause" : "Play",
                        playPausePendingIntent
                )
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        if (!isPlaying) {
            playSong();
        }

        return START_STICKY;
    }

    private void playSong() {
        mediaPlayer.start();
        isPlaying = true;
    }

    private void pauseSong() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    private void updateNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(songName)
                .setSmallIcon(R.drawable.ic_music_note)
                .addAction(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Pause" : "Play",
                        getPlayPauseIntent()
                )
                .setOngoing(true)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notification);
    }

    private PendingIntent getPlayPauseIntent() {
        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction("TOGGLE_PLAY");
        return PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
