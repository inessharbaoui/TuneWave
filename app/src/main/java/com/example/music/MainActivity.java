package com.example.music;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView songName;
    private ImageButton playPauseButton;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;
    private boolean isPlaying = false;
    private boolean isBookmarked = false;
    private int bookmarkedPosition = 0;
    private ImageButton heartButton; // Declare the heart button
    private TextView elapsedTimeTextView, songDurationTextView;
    private ImageButton prevButton, nextButton;



    private String[] songNames = {
            "Song 1", "Song 2", "Song 3", "Song 4", "Song 5",
            "Song 6", "Song 7", "Song 8", "Song 9", "Song 10"
    };
    private int[] songFiles = {
            R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5,
            R.raw.song6, R.raw.song7, R.raw.song8, R.raw.song9, R.raw.song10
    };
    private int[] songImages = {
            R.drawable.song1_image, R.drawable.song2_image, R.drawable.song3_image,
            R.drawable.img1, R.drawable.img21,
            R.drawable.img66, R.drawable.img5, R.drawable.img20,
            R.drawable.img2, R.drawable.img5
    };

    private ImageView songImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songName = findViewById(R.id.songName);
        playPauseButton = findViewById(R.id.playPauseButton);
        seekBar = findViewById(R.id.seekBar);
        songImage = findViewById(R.id.songImage);
        heartButton = findViewById(R.id.heartButton);
        elapsedTimeTextView = findViewById(R.id.elapsedTime);
        songDurationTextView = findViewById(R.id.totalTime);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int selectedSongIndex = -1;

            if (menuItem.getItemId() == R.id.song1) {
                selectedSongIndex = 0;
            } else if (menuItem.getItemId() == R.id.song2) {
                selectedSongIndex = 1;
            } else if (menuItem.getItemId() == R.id.song3) {
                selectedSongIndex = 2;
            } else if (menuItem.getItemId() == R.id.song4) {
                selectedSongIndex = 3;
            } else if (menuItem.getItemId() == R.id.song5) {
                selectedSongIndex = 4;
            } else if (menuItem.getItemId() == R.id.song6) {
                selectedSongIndex = 5;
            } else if (menuItem.getItemId() == R.id.song7) {
                selectedSongIndex = 6;
            } else if (menuItem.getItemId() == R.id.song8) {
                selectedSongIndex = 7;
            } else if (menuItem.getItemId() == R.id.song9) {
                selectedSongIndex = 8;
            } else if (menuItem.getItemId() == R.id.song10) {
                selectedSongIndex = 9;
            }

            if (selectedSongIndex != -1) {
                currentSongIndex = selectedSongIndex;
                playNewSong();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        mediaPlayer = MediaPlayer.create(this, songFiles[currentSongIndex]);
        updateSongDetails();




        heartButton.setOnClickListener(v -> {
            if (heartButton.getTag() == null || heartButton.getTag().equals("empty")) {
                heartButton.setImageResource(R.drawable.ic_heart_filled);
                heartButton.setTag("filled");
                bookmarkSong();
            } else {
                heartButton.setImageResource(R.drawable.ic_heart);
                heartButton.setTag("empty");
                isBookmarked = false;
            }
        });

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                pauseSong();
            } else {
                playSong();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateSeekBar();
        prevButton.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
            } else {
                currentSongIndex = songNames.length - 1;
            }
            playNewSong();
        });

        nextButton.setOnClickListener(v -> {
            if (currentSongIndex < songNames.length - 1) {
                currentSongIndex++;
            } else {
                currentSongIndex = 0;
            }
            playNewSong();
        });

    }

    private void playSong() {
        mediaPlayer.start();
        isPlaying = true;
        playPauseButton.setImageResource(R.drawable.ic_pause);
        updateSeekBar();
    }

    private void pauseSong() {
        mediaPlayer.pause();
        isPlaying = false;
        playPauseButton.setImageResource(R.drawable.ic_play);
    }

    private void updateSongDetails() {
        songName.setText(songNames[currentSongIndex]);
        songImage.setImageResource(songImages[currentSongIndex]);
    }

    private void playNewSong() {
        mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(this, songFiles[currentSongIndex]);
        playSong();
        updateSongDetails();
    }

    private void updateSeekBar() {

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

         int currentPosition = mediaPlayer.getCurrentPosition();
        elapsedTimeTextView.setText(formatTime(currentPosition));

         int totalDuration = mediaPlayer.getDuration();
        songDurationTextView.setText(formatTime(totalDuration));

         if (isPlaying) {
            seekBar.postDelayed(this::updateSeekBar, 1000);
        }
    }



    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


     private void bookmarkSong() {
        bookmarkedPosition = mediaPlayer.getCurrentPosition();
        isBookmarked = true;
    }

     private void jumpToBookmark() {
        if (isBookmarked) {
            mediaPlayer.seekTo(bookmarkedPosition);
            playSong();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }



}
