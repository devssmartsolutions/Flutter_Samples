package com.readytoborad.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.readytoborad.R;

/**
 * Created by anchal.kumar on 10/30/2017.
 */

public class SeekBarHint extends android.support.v7.widget.AppCompatSeekBar {

    private Paint mTextPaint;
    private Rect mTextBounds = new Rect();
    private Bitmap mBitmapIconArrowDown;

    /**
     * for this class yPosition must be as minHeight in layoutFile for this seekBar
     */
    private static int sTextYPositionIndent = 20;
    private float mTextSizeDecrease = 1.75f;

    public static void setTextYPositionIndent(int textYPositionIndent) {
        sTextYPositionIndent = textYPositionIndent;
    }

    public SeekBarHint(Context context) {
        super(context);
        mBitmapIconArrowDown = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bus);
        mTextPaint = new Paint();
        mTextPaint.setColor(getResources().getColor(R.color.colorPrimary));

    }

    public SeekBarHint(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBitmapIconArrowDown = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bus);
        mTextPaint = new Paint();
        mTextPaint.setColor(getResources().getColor(R.color.colorPrimary));

    }

    public SeekBarHint(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBitmapIconArrowDown = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bus);
        mTextPaint = new Paint();
        mTextPaint.setColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //  first draw the regular progress bar, then custom draw our textString
        super.onDraw(canvas);

        //  now progress position and convert to textString.
        String textString = Integer.toString(getProgress()) + "%";

        //  now get size of seek bar.
        float width = getWidth();
        float height = getHeight();

        //  set textString size.
        mTextPaint.setTextSize(height / mTextSizeDecrease);

        //  get size of textString.
        mTextPaint.getTextBounds(textString, 0, textString.length(), mTextBounds);

        //  calculate where to start printing textString.
        float position = (width / getMax()) * getProgress();

        //  get start and end points of where textString will be printed.
        float textXStart = position - mTextBounds.centerX();
        float textXEnd = position + mTextBounds.centerX();

        //  check does not start drawing text outside seek bar.
        if (textXStart < 0)
            textXStart = 0;

        if (textXEnd > width)
            textXStart -= (textXEnd - width);

        //  calculate y textString print position.
        float yPosition = (height / 2) - mTextBounds.centerY();
        //   canvas.drawText(textString, textXStart, yPosition - sTextYPositionIndent, mTextPaint);

        //  arrow draw logic
        //  check does not start drawing arrow outside seek bar
        int seekBarAbsoluteWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int thumbPos = (getPaddingLeft() / 2) + (seekBarAbsoluteWidth * getProgress() / getMax());

        //  set height and width for new bitmap
        int arrowHeight = Math.round(mTextBounds.height() / 2f);
        int arrowWidth = 80;


        Bitmap scaledBitmapIconArrowDown = Bitmap
                .createScaledBitmap(mBitmapIconArrowDown, arrowWidth, arrowHeight, true);


        System.out.println(" thumbPos " + thumbPos + " padding " + (getPaddingLeft() / 2) + " seek bar width" + (seekBarAbsoluteWidth + "progress" + getProgress() + " max progress " + getMax()));

        if (getProgress() < getMax() - 10) {
            canvas.drawBitmap(scaledBitmapIconArrowDown, thumbPos, 1, null);
        }else
        {
            canvas.drawBitmap(scaledBitmapIconArrowDown, thumbPos-30, 1, null);
        }
    }
}
