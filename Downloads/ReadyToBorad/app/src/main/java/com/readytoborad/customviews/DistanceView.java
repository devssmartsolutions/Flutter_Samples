package com.readytoborad.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.readytoborad.R;


/**
 * Created by harendrasinghbisht on 22/02/17.
 */

public class DistanceView extends View {
    private Paint primaryPaint;
    private Paint secondryPaint;
    private Paint textPaint;
    private long maxDistance;
    private long progress = 0;
    private Context context;

    public DistanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialise();
    }

    public DistanceView(Context context) {
        super(context);
        this.context = context;
        initialise();
    }

    private void initialise() {
        primaryPaint = new Paint();
        secondryPaint = new Paint();
        textPaint = new Paint();
        primaryPaint.setColor(Color.TRANSPARENT);
        primaryPaint.setStyle(Paint.Style.FILL);
        secondryPaint.setColor(Color.TRANSPARENT);
        secondryPaint.setStyle(Paint.Style.FILL);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.min_left_image);

        int height = getHeight() / 2;
        canvas.drawBitmap(b, progress - b.getWidth() / 2, height - b.getHeight(), primaryPaint);

        canvas.drawRect(0, height, getWidth(), height + 5, primaryPaint);
        canvas.drawRect(0, height, progress, height + 5, secondryPaint);

        canvas.drawText(String.valueOf(progress), progress - 30, height - b.getHeight() / 2, textPaint);
    }

    public void setMaxDistance(long maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setProgress(long progress) {

        this.progress = this.progress + progress;
        this.progress = (progress * getWidth()) / maxDistance;
        invalidate();
    }
}
