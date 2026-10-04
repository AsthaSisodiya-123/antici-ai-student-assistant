package com.example.dailymart.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailymart.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WishListFragment extends Fragment {
LinearLayout tvWishListNetworkStatus;
    TextView tvSongName,tvStartTime,tvTotalTime;
    ImageView ivCoverPage,ivPrevious,ivBackward,ivPlayPause,ivForward,ivNext,ivVolumn;
    SeekBar sbSongPlay,sbVolumn;
    private static int currentIndex=0;
    MediaPlayer mMediaPlayer;
    private static int stime=0,ttime=0,otime=0,ftime=5000,btime=5000;
    Handler handler=new Handler();
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_wish_list, container, false);
        Toast.makeText(getActivity(),"My Wish List",Toast.LENGTH_SHORT).show();

        tvWishListNetworkStatus=view.findViewById(R.id.tvWishListNetworkStatus);
        checkNetworkStatus();

        tvSongName=view.findViewById(R.id.tvSongName);
        tvStartTime=view.findViewById(R.id.tvStartTime);
        tvTotalTime=view.findViewById(R.id.tvTotalTime);
        ivCoverPage=view.findViewById(R.id.ivCoverPage);
        ivPrevious=view.findViewById(R.id.ivPrevious);
        ivBackward=view.findViewById(R.id.ivBackward);
        ivPlayPause=view.findViewById(R.id.ivPause);
        ivForward=view.findViewById(R.id.ivForward);
        ivNext=view.findViewById(R.id.ivNext);
        ivVolumn=view.findViewById(R.id.ivVolumn);
        sbSongPlay=view.findViewById(R.id.sbSongPlay);
        sbVolumn=view.findViewById(R.id.sbVolumn);

        ArrayList<Integer> songArrayList=new ArrayList<>();
        songArrayList.add(0,R.raw.papakehtehain);
        songArrayList.add(1,R.raw.shri_krishna_govind);
        songArrayList.add(2,R.raw.radharanilage);
        songArrayList.add(3,R.raw.meet);
        songArrayList.add(4,R.raw.damru_bajaya);

        mMediaPlayer=MediaPlayer.create(getActivity(),songArrayList.get(currentIndex));
        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer!=null && mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.pause();
                    ivPlayPause.setImageResource(R.drawable.icon_play);
                }
                else
                {
                    mMediaPlayer.start();
                    ivPlayPause.setImageResource(R.drawable.icon_pause);
                }
                ttime=mMediaPlayer.getDuration();
                stime=mMediaPlayer.getCurrentPosition();

                if (otime==0)
                {
                    sbSongPlay.setMax(ttime);
                    otime=1;
                }

                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(ttime),
                        TimeUnit.MILLISECONDS.toSeconds(ttime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ttime))));

                tvStartTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(stime),
                        TimeUnit.MILLISECONDS.toSeconds(stime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stime))));

                handler.postDelayed(UpdateSongProgress,1000);
                songDetails();
            }
        });

        sbSongPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {
                    mMediaPlayer.seekTo(progress);
                    sbSongPlay.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex>0)
                {
                    currentIndex--;
                }
                else
                {
                    currentIndex=songArrayList.size()-1;
                }
                if (mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.stop();
                }
                if (mMediaPlayer!=null)
                {
                    ivPlayPause.setImageResource(R.drawable.icon_pause);
                }

                mMediaPlayer=MediaPlayer.create(getActivity(),songArrayList.get(currentIndex));

                ttime=mMediaPlayer.getDuration();
                stime=mMediaPlayer.getCurrentPosition();

                if (otime==0)
                {
                    sbSongPlay.setMax(ttime);
                    otime=1;
                }

                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(ttime),
                        TimeUnit.MILLISECONDS.toSeconds(ttime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ttime))));

                tvStartTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(stime),
                        TimeUnit.MILLISECONDS.toSeconds(stime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stime))));

                handler.postDelayed(UpdateSongProgress,1000);
                songDetails();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < songArrayList.size()-1)
                {
                    currentIndex++;
                }
                else
                {
                    currentIndex=0;
                }

                if (mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.stop();
                }
                if (mMediaPlayer!=null)
                {
                    ivPlayPause.setImageResource(R.drawable.icon_pause);
                }

                mMediaPlayer=MediaPlayer.create(getActivity(),songArrayList.get(currentIndex));

                ttime=mMediaPlayer.getDuration();
                stime=mMediaPlayer.getCurrentPosition();

                if (otime==0)
                {
                    sbSongPlay.setMax(ttime);
                    otime=1;
                }

                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(ttime),
                        TimeUnit.MILLISECONDS.toSeconds(ttime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ttime))));

                tvStartTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(stime),
                        TimeUnit.MILLISECONDS.toSeconds(stime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stime))));

                handler.postDelayed(UpdateSongProgress,1000);
                songDetails();
            }
        });


        ivBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((stime - btime) > 0)
                {
                   stime=stime-btime;
                   mMediaPlayer.seekTo(stime);

                }
                else
                {
                    Toast.makeText(getActivity(),"Cannot jump backward for 5 sec",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((stime + ftime) < ttime)
                {
                    stime=stime+ftime;
                    mMediaPlayer.seekTo(stime);

                }
                else
                {
                    Toast.makeText(getActivity(),"Cannot jump forward for 5 sec",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    private void songDetails()
    {
        if (currentIndex==0)
        {
            tvSongName.setText("Papa Kehte Hain");
            ivCoverPage.setImageResource(R.drawable.song);

        }
       else if (currentIndex==1)
        {
            tvSongName.setText("Shri Krishna Govind");
            ivCoverPage.setImageResource(R.drawable.shrikrishnagovind);

        }
        else if (currentIndex==2)
        {
            tvSongName.setText("Radha Rani");
            ivCoverPage.setImageResource(R.drawable.radharani);

        }
        else if (currentIndex==3)
        {
            tvSongName.setText("Meet");
            ivCoverPage.setImageResource(R.drawable.meet);

        }
        else if (currentIndex==4)
        {
            tvSongName.setText("Damru Bajaya");
            ivCoverPage.setImageResource(R.drawable.damrubajaya);

        }

    }

    private Runnable UpdateSongProgress= new Runnable() {
        @Override
        public void run() {

            stime=mMediaPlayer.getCurrentPosition();
            tvStartTime.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(stime),
                    TimeUnit.MILLISECONDS.toSeconds(stime),
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stime))));

            sbSongPlay.setProgress(stime);
            handler.postDelayed(this,1000);

        }
    };
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            tvWishListNetworkStatus.setVisibility(View.GONE); // hide banner
        } else {
            tvWishListNetworkStatus.setVisibility(View.VISIBLE); // show banner
        }
    }
}
