package com.example.skillbrigde;

import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillbrigde.Adapter.LectureAdapter;
import com.example.skillbrigde.Model.LectureModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;



public class Video_Lecture_Activity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lecture);

        // Initialize views
        playerView = findViewById(R.id.playerView);
        RecyclerView recyclerView = findViewById(R.id.lectureRecycler);

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Lecture list
        List<LectureModel> lectures = new ArrayList<>();
        lectures.add(new LectureModel(
                "Introduction",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "10 min"
        ));
        lectures.add(new LectureModel(
                "HTML Basics",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "15 min"
        ));
        lectures.add(new LectureModel(
                "CSS Styling",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                "12 min"
        ));

        // Play first lecture by default
        playVideo(lectures.get(0).videoUrl);

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LectureAdapter(lectures, this::playVideo));
    }

    // Plays selected video in the same player
    private void playVideo(String url) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
