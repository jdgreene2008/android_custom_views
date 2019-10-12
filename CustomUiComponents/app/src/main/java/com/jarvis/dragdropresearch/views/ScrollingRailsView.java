package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.interpolators.ColorInterpolator;
import com.jarvis.dragdropresearch.rails.domain.MovableObject;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollingRailsView extends AbsCustomScrollingView<RailPage> {
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
        for (int i = 1; i <= pageCount; i++) {
            RailPage page = new RailPage();
            page.setHeight(getMeasuredHeight());
            page.setWidth(getMeasuredWidth());
            page.setXPosition(0);
            page.setYPosition(i * getMeasuredHeight());
            mPages.add(page);
        }

        final int gapY = 100;
        final int gapX = 200;

        for (int g = 0; g < pageCount; g++) {
            RailPage currentPage = mPages.get(g);

            final int railY = (g + 1) * currentPage.getHeight();
            final int railX = ((g == 0) ? gapX :
                    ((g == pageCount - 1) ? (currentPage.getWidth() - gapX) :
                            (gapX + currentPage.getWidth() / pageCount)));
            for (int i = 0; i < 5; i++) {
                int color = COLORS_OBJECTS[(i % COLORS_OBJECTS.length)];
                currentPage.addObject(new MovableObject(railX, railY + gapY * i,
                        "1", color));
            }

            ColorInterpolator interpolator = new ColorInterpolator(currentPage.getHeight());
            interpolator.setColor(COLORS_BACKGROUNDS[g]);
            currentPage.setColorInterpolator(interpolator);
        }

        setContentHeight(getMeasuredHeight() * (pageCount + 1));
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
            for (RailPage page : mPages) {
                drawBackground(canvas, page);
                drawPageRail(page, canvas);
            }
        }
    }

    private void drawPageRail(RailPage page, Canvas canvas) {
        if (!page.isVisible()) return;

        final int offset = 25;
        final int radius = 50;

        List<MovableObject> pageObjects = page.getMovableObjectRails();
        Paint paint = new Paint();

        int count = 0;
        for (MovableObject object : pageObjects) {
            paint.setColor(object.getColor());
            if (object.getYPos() > (getMeasuredHeight() + getScrollY())) {
                // Object is off-screen and any objects that follow it is off-screen. No need to
                // continue traversal.
                break;
            }
            if (getScrollY() >= object.getYPos()) {
                /* Means we've scrolled to the top of the visible part of the object to draw. */
                canvas.drawCircle(object.getXPos(),
                        getScrollY() + (offset * count), radius, paint);
            } else {
                int yPosition = object.getYPos();
                canvas.drawCircle(object.getXPos(), yPosition, radius, paint);
            }

            count++;
        }
    }

    private void drawBackground(Canvas canvas, RailPage page) {
        if (page.isVisible()) {
            ColorInterpolator interpolator = page.getColorInterpolator();
            interpolator
                    .updateValue(interpolator.getMaxValue() - (page.getYPosition() - getScrollY()));
            drawShadedBackground(canvas, interpolator, page.getYPosition());
        }
    }

    private void drawShadedBackground(Canvas canvas, ColorInterpolator interpolator,
            int yPosition) {
        // Determine bounds of the shaded region.
        int rectTop = yPosition;
        int rectLeft = 0;
        int rectRight = getMeasuredWidth();
        int rectBottom = interpolator.getValue() + rectTop + 50;
        Rect shadeRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        // Compute shade based on interpolation.
        Paint paint = new Paint();
        paint.setColor(interpolator.getInterpolatedShade());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadeRect, paint);
    }
}
