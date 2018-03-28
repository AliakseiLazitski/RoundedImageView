package jork.juger.roundedimageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private RoundedImageView mRoundedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoundedImageView = findViewById(R.id.imageView);
        ((SeekBar)findViewById(R.id.radius)).setOnSeekBarChangeListener(this);
        ((SeekBar)findViewById(R.id.topLeftRadius)).setOnSeekBarChangeListener(this);
        ((SeekBar)findViewById(R.id.topRightRadius)).setOnSeekBarChangeListener(this);
        ((SeekBar)findViewById(R.id.bottomRightRadius)).setOnSeekBarChangeListener(this);
        ((SeekBar)findViewById(R.id.bottomLeftRadius)).setOnSeekBarChangeListener(this);
        findViewById(R.id.circle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mRoundedImageView.setIsCircle(!mRoundedImageView.isCircle());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.radius:
                mRoundedImageView.setCornersRadius(progress);
                break;
            case R.id.topLeftRadius:
                mRoundedImageView.setTopLeftCorner(progress);
                break;
            case R.id.topRightRadius:
                mRoundedImageView.setTopRightCorner(progress);
                break;
            case R.id.bottomRightRadius:
                mRoundedImageView.setBottomRightCorner(progress);
                break;
            case R.id.bottomLeftRadius:
                mRoundedImageView.setBottomLeftCorner(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
