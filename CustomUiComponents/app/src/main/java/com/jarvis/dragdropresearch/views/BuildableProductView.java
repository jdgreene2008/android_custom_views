package com.jarvis.dragdropresearch.views;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.FrameLayout;

public class BuildableProductView extends FrameLayout {

    private AddedItemListener mListener;

    private Object mProduct;

    public BuildableProductView(Context context) {
        super(context);
        init();
    }

    public BuildableProductView(Context context,
            @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BuildableProductView(Context context, @androidx.annotation.Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    public AddedItemListener getListener() {
        return mListener;
    }

    public void setListener(
            AddedItemListener listener) {
        mListener = listener;
    }

    public Object getProduct() {
        return mProduct;
    }

    public void setProduct(Object product) {
        mProduct = product;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawItem(mProduct, canvas);
    }

    /**
     * Draw the item under construction. Subclasses should override this method to handle
     * specific-drawing behaviors needed for the product type being constructed.
     *
     * @param item {@link Object} representing the item under construction.
     * @param canvas {@link Canvas} representing the platform upon which the drawing occurs.
     */
    protected void drawItem(Object item, Canvas canvas) {

    }



    /**
     * An added component is passed in the {@link DragEvent} through an {@link Intent}.
     * Subclasses should override this method to handle the specific
     * types of components expected.
     *
     * @param component {@link Object} item added to this buildable model.
     */
    protected void onComponentAdded(Object component) {
        invalidate();
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        if (mListener == null) return false;
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item item = event.getClipData().getItemAt(0);
            if (item.getIntent() != null && item.getIntent().hasExtra(DraggableItem.DATA_KEY)) {
                String toppingJson = item.getIntent().getStringExtra(DraggableItem.DATA_KEY);
                // TODO: Deserialize JSON into it's appropriate item.
            }
        }

        return true;
    }

    public interface AddedItemListener {

        void onItemAdded(Object item);
    }
}
