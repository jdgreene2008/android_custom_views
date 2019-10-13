package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.rails.domain.MovableObject;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollingRailsView extends AbsCustomScrollingView<ScrollingRailsPage> {
    private static final int[] COLORS_OBJECTS =
            new int[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    private static final int[] COLORS_BACKGROUNDS =
            new int[] {Color.MAGENTA, Color.CYAN, Color.LTGRAY};

    public ScrollingRailsView(Context context) {
        super(context);
    }

    public ScrollingRailsView(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingRailsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initializePages() {
        setInitializedPages(true);
        setupPages();
    }

    private void setupPages() {
        final int pageCount = 3;
        mPages = new ArrayList<>(pageCount);

        // Assumes fixed-size pages for now.
        int startPosition = 0;
        for (int i = 1; i <= pageCount; i++) {
            ScrollingRailsPage page = new ScrollingRailsPage();
            page.setHeight(getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
            page.setWidth(getMeasuredWidth() - getPaddingStart() - getPaddingEnd());
            page.setXPosition(0);
            if (startPosition == 0) {
                startPosition = getContentBoundsBottom();
            } else {
                startPosition += page.getHeight();
            }
            page.setYPosition(startPosition);
            mPages.add(page);
        }

        final int gapY = 100;
        final int gapX = 200;

        for (int g = 0; g < pageCount; g++) {
            ScrollingRailsPage currentPage = mPages.get(g);

            final int railX = ((g == 0) ? getPaddingStart() + gapX :
                    ((g == pageCount - 1) ? (getPaddingStart() + currentPage.getWidth() - gapX) :
                            getPaddingStart() + (gapX + currentPage.getWidth() / pageCount)));

            for (int i = 0; i < 5; i++) {
                int color = COLORS_OBJECTS[(i % COLORS_OBJECTS.length)];
                currentPage.addObject(new MovableObject(railX, gapY * i,
                        "1", color));
            }

            ColorInterpolator interpolator = new ColorInterpolator(currentPage.getHeight());
            interpolator.setColor(COLORS_BACKGROUNDS[g]);
            currentPage.setColorInterpolator(interpolator);
        }

        setContentHeight(
                (getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + gapY * 5) * (pageCount + 1));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupPages();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPages(canvas);
        super.onDraw(canvas);
    }

    private void drawPages(Canvas canvas) {
        if (mPages != null) {
            for (ScrollingRailsPage page : mPages) {
                drawBackground(canvas, page);
                drawPageRail(page, canvas);
            }
        }
    }

    private void drawPageRail(ScrollingRailsPage page, Canvas canvas) {
        if (page.isVisible()) {
            final int offset = 25;
            final int radius = 50;

            List<MovableObject> pageObjects = page.getMovableObjectRails();
            Paint paint = new Paint();

            int count = 0;
            for (MovableObject object : pageObjects) {
                paint.setColor(object.getColor());
                if ((page.getYPosition() + object.getYOffset()) > getContentBoundsBottom()) {
                    // Object is off-screen and any objects that follow it is off-screen. No need to
                    // continue traversal.
                    break;
                }
                if ((page.getYPosition() + object.getYOffset()) <= getContentBoundsTop()) {
                    /* Means we've scrolled to the top of the visible part of the object to draw. */
                    canvas.drawCircle(object.getXOffset(),
                            getContentBoundsTop() + (offset * count), radius, paint);
                } else {
                    int yPosition = page.getYPosition() + object.getYOffset();
                    canvas.drawCircle(object.getXOffset(), yPosition, radius, paint);
                }

                count++;
            }
        }
    }

    private void drawBackground(Canvas canvas, ScrollingRailsPage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getColorInterpolator();
            interpolator
                    .updateValue(getContentBoundsBottom() - page.getYPosition());
            drawShadedBackground(canvas, interpolator, page);
        }
    }
}
