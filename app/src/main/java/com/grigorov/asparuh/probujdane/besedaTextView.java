package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 13/11/2017.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import java.util.ArrayList;

import static com.grigorov.asparuh.probujdane.R.styleable.View;


public class besedaTextView extends android.support.v7.widget.AppCompatTextView {

    private int mLineY;
    private int mViewWidth;
    private boolean firstLineOfParagraph;
    private int charCounter;
    private ArrayList<besedaTextViewMarker> listMarkers= new ArrayList<besedaTextViewMarker>();
    private int scrollToY;
    private int scrollToChar;
    private BesedaActivity bActivity;

    public besedaTextView(Context context) {
        super(context);
        init(context);
    }

    public besedaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public besedaTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public besedaTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //super(context, attrs, defStyleAttr, defStyleRes);
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        bActivity = (BesedaActivity) context;
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

        TextPaint normalPaint = new TextPaint(getPaint());
        normalPaint.setColor(getCurrentTextColor());
        normalPaint.drawableState = getDrawableState();

        TextPaint markedPaint = new TextPaint(getPaint());
        markedPaint.setColor(getCurrentTextColor());
        markedPaint.drawableState = getDrawableState();
        markedPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        markedPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null));

        mViewWidth = getMeasuredWidth();
        String text = getText().toString();
        //Spannable text = (Spannable) getText();
        mLineY = 0;
        mLineY += getTextSize();
        Layout layout = getLayout();
        firstLineOfParagraph=true;
        charCounter = 0;
        scrollToY = 0;

        float emLength = StaticLayout.getDesiredWidth("М", getPaint());
        float sl = (float) (0.02 * emLength);

        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String line = text.substring(lineStart, lineEnd);
            float lineWidth = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());

            String [] words = line.split(" ");
            float x = 0;
            if (isFirstLineOfParagraph()) {
                x += 100;
            }

            float sw = 0;
            if (    (words.length>1) &&
                    needScale(line) &&
                    (i!=layout.getLineCount()-1) ) {
                sw = ((mViewWidth - x) - lineWidth - (sl*(line.length()-1)) ) / (words.length - 1);
            }

            for (int j = 0; j < line.length(); j++) {
                String c = String.valueOf(line.charAt(j));
                TextPaint currentTextPaint = getTextPaint(charCounter, normalPaint, markedPaint);
                float cw = StaticLayout.getDesiredWidth(c, currentTextPaint);
                canvas.drawText(c, x, mLineY, currentTextPaint);
                x = x + cw + sl;
                if (c.compareTo(" ")==0) {
                    x = x + sw;
                }
                charCounter++;
            }

            if ( (scrollToChar>=lineStart) && (scrollToChar<lineEnd) ) {
                scrollToY = mLineY-getLineHeight();
            }
            mLineY += getLineHeight();

            if (line.length()>0) {
                firstLineOfParagraph = (line.charAt(line.length() - 1) == '\n');
            } else {
                firstLineOfParagraph = true;
            }
        }

        bActivity.scrollToTarget();

    }

    private boolean isFirstLineOfParagraph() {
        return firstLineOfParagraph;
    }

    private boolean needScale(String line) {
    //private boolean needScale(Spannable line) {
            if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

    private TextPaint getTextPaint(int charCount, TextPaint normPaint, TextPaint markPaint) {
        if (listMarkers.size()>0) {
            for (int m_loop=0;m_loop<listMarkers.size();m_loop=m_loop+1) {
                if (
                        (listMarkers.get(m_loop).getStartIndex() <= charCount) &&
                        (listMarkers.get(m_loop).getEndIndex() >= charCount) ) {
                    return markPaint;
                }
            }
        }
        return normPaint;
    }

    public void setMarkersString (String inputString) {
        listMarkers.clear();
        if (inputString.equals("")==false) {
            String[] inputMarkers = inputString.split(" "); // Split to " " to read integers
            for (int marker_loop = 0; marker_loop < inputMarkers.length; marker_loop = marker_loop + 2) {
                listMarkers.add(
                        new besedaTextViewMarker(
                                Integer.parseInt(inputMarkers[marker_loop]),
                                Integer.parseInt(inputMarkers[marker_loop + 1])
                        )
                );
            }
        }
    }

    public void setScrollToChar (int inputScrollToChar) {
        scrollToChar = inputScrollToChar;
    }

    public int getScrollToY () { return scrollToY; }

    private class besedaTextViewMarker {
        private int startIndex;
        private int endIndex;

        public besedaTextViewMarker (int inputStartIndex, int inputEndIndex) {
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }

}