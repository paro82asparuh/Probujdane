package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 13/11/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;


public class besedaTextView extends android.support.v7.widget.AppCompatTextView {

    private int mLineY;
    private int mViewWidth;
    private boolean firstLineOfParagraph;

    public besedaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mViewWidth = getMeasuredWidth();
        String text = getText().toString();
        mLineY = 0;
        mLineY += getTextSize();
        Layout layout = getLayout();
        firstLineOfParagraph=true;
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String line = text.substring(lineStart, lineEnd);

            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
            if (needScale(line)) {
                drawScaledText(canvas, lineStart, line, width);
            } else {
                if (firstLineOfParagraph==true) {
                    canvas.drawText(line, 100, mLineY, paint);
                } else {
                    canvas.drawText(line, 0, mLineY, paint);
                }
            }

            mLineY += getLineHeight();

            if (line.length()>0) {
                firstLineOfParagraph = (line.charAt(line.length() - 1) == '\n');
            } else {
                firstLineOfParagraph = true;
            }
        }
    }

    private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;
        float d = (mViewWidth - lineWidth) / line.length() - 1;

        if (isFirstLineOfParagraph()) {
            x += 100;
            d = ( (mViewWidth - 100 ) - lineWidth) / line.length() - 1;
        }

        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph() {
        return firstLineOfParagraph;
    }

    private boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

}