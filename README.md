# Custom Scrolling Demo #
Collection of extensions to common Android ViewGroups demonstrating customizations using MotionEvent handling and Scrollers.

The source code presented here demonstrates ways to extend the Android View framework to develop creative user interfaces bthat don't come right out of the box. 

## Demo App ##
*Demos each of the custom scroll-based views featured below.*
![Demo App](/images/custom_scrolling_app_full_flow.gif)




The below shows the CustomScrollingView.java custom UI component in action. The user scrolls up and down to reveal the circles. The primary purpose of this custom UI component is to serve as an intro to building customizable scrolling component outside of  the Android framework class of ScrollView.java. 

## ScrollingRailsView Example ##

![ScrollingRailsView](/images/3bwk2w.gif)


## ImageFlashView Example
*Uses ImageCacheHelper and ImageLoader, two classes created to faciliated loading Bitmaps efficiently. The ImageCacheHelper is backed by an LruCache. This ensures that the Bitmaps do not consume excessive amounts of memory when stored.*

![ImageFlashView](/images/image_flash_view.gif)


## FlashShapesView ##
*Draw different geometric shapes into view as the user scrolls. The shapes are drawn proportional to the view size so that it scales across any screen sizes.*

### Large View ###
![FlashShapesView-Large Window](/images/flash_shape_large_window.gif)


### Mini View ###
![FlashShapesView-Mini Window](/images/flash_shape_mini_window.gif)






