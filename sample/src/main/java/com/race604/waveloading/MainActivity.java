package com.race604.waveloading;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.race604.drawable.wave.WaveDrawable;
import com.race604.drawable.wave.WaveDrawable2;
import com.race604.drawable.wave.WaveDrawable3;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private WaveDrawable mWaveDrawable;
    private SeekBar mLevelSeekBar;
    private SeekBar mAmplitudeSeekBar;
    private SeekBar mSpeedSeekBar;
    private SeekBar mLengthSeekBar;
    private RadioGroup mRadioGroup;
    private WaveDrawable2 waveDrawable2;
    WaveDrawable3 colorWave3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveDrawable2 = new WaveDrawable2(Color.BLUE);
        findViewById(R.id.activity_main).setBackground(waveDrawable2);
        waveDrawable2.setProgress(0.5f);

        mImageView = (ImageView) findViewById(R.id.image);
        mWaveDrawable = new WaveDrawable(this, R.drawable.android_robot);
        mImageView.setImageDrawable(mWaveDrawable);

        mLevelSeekBar = (SeekBar) findViewById(R.id.level_seek);
        mLevelSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveDrawable.setLevel(progress);
                waveDrawable2.setProgress((float)progress / seekBar.getMax());
                colorWave3.setProgress((float)progress / seekBar.getMax());
            }
        });

        mAmplitudeSeekBar = (SeekBar) findViewById(R.id.amplitude_seek);
        mAmplitudeSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveDrawable.setWaveAmplitude(progress);
                waveDrawable2.setWaveAmplitude(progress);
            }
        });

        mLengthSeekBar = (SeekBar) findViewById(R.id.length_seek);
        mLengthSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveDrawable.setWaveLength(progress);
                waveDrawable2.setWaveLength(progress);
            }
        });

        mSpeedSeekBar = (SeekBar) findViewById(R.id.speed_seek);
        mSpeedSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaveDrawable.setWaveSpeed(progress);
                waveDrawable2.setWaveSpeed(progress);
            }
        });

        mRadioGroup = (RadioGroup) findViewById(R.id.modes);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final boolean indeterminate = checkedId == R.id.rb_yes;
                setIndeterminateMode(indeterminate);
            }
        });
        setIndeterminateMode(mRadioGroup.getCheckedRadioButtonId() == R.id.rb_yes);

        ImageView imageView2 = (ImageView) findViewById(R.id.image2);
        WaveDrawable chromeWave = new WaveDrawable(this, R.drawable.chrome_logo);
        imageView2.setImageDrawable(chromeWave);

        // Set customised animator here
//        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setDuration(4000);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        chromeWave.setIndeterminateAnimator(animator);
        chromeWave.setIndeterminate(true);

        View view = findViewById(R.id.view);
        int color = getResources().getColor(R.color.colorAccent);
        WaveDrawable colorWave = new WaveDrawable(new ColorDrawable(color));
        view.setBackground(colorWave);
        colorWave.setIndeterminate(true);

        View view1 = findViewById(R.id.view1);
        colorWave3 = new WaveDrawable3(color);
        view1.setBackground(colorWave3);

    }

    private void setIndeterminateMode(boolean indeterminate) {
        mWaveDrawable.setIndeterminate(indeterminate);
        mLevelSeekBar.setEnabled(!indeterminate);

        if (!indeterminate) {
            mWaveDrawable.setLevel(mLevelSeekBar.getProgress());
        }
        mWaveDrawable.setWaveAmplitude(mAmplitudeSeekBar.getProgress());
        mWaveDrawable.setWaveLength(mLengthSeekBar.getProgress());
        mWaveDrawable.setWaveSpeed(mSpeedSeekBar.getProgress());
    }

    private static class SimpleOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Nothing
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Nothing
        }
    }
}
