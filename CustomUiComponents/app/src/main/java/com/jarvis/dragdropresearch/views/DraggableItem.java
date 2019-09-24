package com.jarvis.dragdropresearch.views;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.jarvis.dragdropresearch.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Base class representing a view that implements drag and drop functionality to drag items onto
 * other items.
 */
public class DraggableItem extends AppCompatImageView {
    public static final String DATA_KEY = "draggable_data";
    private static final String DEFAULT_DRAG_LABEL = "dragging";

    @DrawableRes
    private int mImageResId;
    private String mImageLabel;

    private Parcelable mData;

    public DraggableItem(Context context) {
        super(context);
    }

    public DraggableItem(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DraggableItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.DraggableItem, 0, 0);
        mImageResId = attributes.getResourceId(R.styleable.DraggableItem_image, 0);
        int labelResourceId = attributes.getResourceId(R.styleable.DraggableItem_label, 0);
        mImageLabel = getResources().getString(labelResourceId);
        setImageResource(mImageResId);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                beginDrag();
                return true;
            }
        });
    }

    public Parcelable getData() {
        return mData;
    }

    /**
     * Sets the data that will be passed when a drag event occurs. The data is passed inside of
     * an intent and should be Parcelable.
     *
     * @param data {@link Parcelable} object representing the data that can be passed in a drag
     * and drop operation performed on this view.
     */
    public void setData(Parcelable data) {
        mData = data;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        beginDrag();
        return true;
    }

    private void beginDrag() {
        Intent dataIntent = new Intent();
        if (mData != null) {
            dataIntent.putExtra(DATA_KEY, new Gson().toJson(mData));
        }

        ClipData.Item item = new ClipData.Item(dataIntent);
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_INTENT};

        ClipData dragData = new ClipData(DEFAULT_DRAG_LABEL, mimeTypes, item);

        startDrag(dragData, new CustomShadowBuilder(), null, 0);
    }

    private class CustomShadowBuilder extends View.DragShadowBuilder {

        private final int dragShadowWidth = 300;
        private final int dragShadowHeight = 300;

        CustomShadowBuilder() {
            super();
        }

        @Override
        public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
            outShadowSize.set(dragShadowWidth, dragShadowHeight);
            super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            super.onDrawShadow(canvas);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), mImageResId);
            canvas.drawBitmap(bm, 0, 0, null);
        }
    }
}

