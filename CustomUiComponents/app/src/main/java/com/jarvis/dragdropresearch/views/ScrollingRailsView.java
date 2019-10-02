package com.jarvis.dragdropresearch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jarvis.dragdropresearch.rails.domain.ColorInterpolator;
import com.jarvis.dragdropresearch.rails.domain.MovableObject;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class ScrollingRailsView extends CustomScrollingView<RailPage> {

    private List<List<MovableObject>> mMovableObjectRails;
    private List<ColorInterpolator> mColorInterpolators;

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
        // TODO: Current positionObjects(getMeasuredWidth(), getMeasuredHeight());
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
    }

    private void positionObjects(int viewWidth, int viewHeight) {
        final int railCount = 3;
        mMovableObjectRails = new ArrayList<>(railCount);

        final int gapY = 100;
        final int gapX = 200;

        for (int g = 0; g < railCount; g++) {
            ArrayList<MovableObject> rail = new ArrayList<>();

            final int railY = (g + 1) * viewHeight - 100 * g;
            final int railX = ((g == 0) ? gapX :
                    ((g == railCount - 1) ? (viewWidth - gapX) :
                            (gapX + viewWidth / railCount)));
            for (int i = 0; i < 5; i++) {
                int color = COLORS_OBJECTS[(i % COLORS_OBJECTS.length)];
                rail.add(new MovableObject(railX, railY + gapY * i,
                        "1", color));
            }
            mMovableObjectRails.add(rail);
        }

        setContentHeight(viewHeight * (2 + railCount) - (100 * railCount));
        setContentWidth(viewWidth * 2);

        mColorInterpolators = new ArrayList<>(railCount);

        for (int i = 0; i < railCount; i++) {
            ColorInterpolator interpolator = new ColorInterpolator(viewHeight);
            interpolator.setColor(COLORS_BACKGROUNDS[i]);
            mColorInterpolators.add(interpolator);
        }
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
            }

            for (RailPage page : mPages) {
                drawPageRail(page, canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        positionObjects(w, h);
        invalidate();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void drawPageRail(RailPage page, Canvas canvas) {
        if (!page.isVisible()) return;

        final int offset = 25;
        final int radius = 50;

        List<MovableObject> pageObjects =page.getMovableObjectRails();
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
        int shade = ColorUtils.setAlphaComponent(interpolator.getColor(),
                (int)(interpolator.getInterpolatedValue() * 255));

        Paint paint = new Paint();
        paint.setColor(shade);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(shadeRect, paint);
    }
}
