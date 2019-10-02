package com.jarvis.dragdropresearch.views;

import com.jarvis.dragdropresearch.rails.domain.ColorInterpolator;
import com.jarvis.dragdropresearch.rails.domain.MovableObject;

import java.util.ArrayList;
import java.util.List;

public class RailPage extends ScrollPage {

    private List<MovableObject> mMovableObjectRails;

    private ColorInterpolator mColorInterpolator;

    public List<MovableObject> getMovableObjectRails() {
        return mMovableObjectRails;
    }

    public void setMovableObjectRails(
            List<MovableObject> movableObjectRails) {
        mMovableObjectRails = movableObjectRails;
    }

    public ColorInterpolator getColorInterpolator() {
        return mColorInterpolator;
    }

    public void setColorInterpolator(
            ColorInterpolator colorInterpolator) {
        mColorInterpolator = colorInterpolator;
    }

    public void addObject(MovableObject object) {
        if (mMovableObjectRails == null) {
            mMovableObjectRails = new ArrayList<>();
        }

        mMovableObjectRails.add(object);
    }
}
