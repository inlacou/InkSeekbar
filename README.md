# InkSeekbar

[![](https://jitpack.io/v/inlacou/InkSeekbar.svg)](https://jitpack.io/#inlacou/InkSeekbar)

Library to make fancy seekbars. Has seekbar or progressbar modes.

# Parametrization

## By Code

Example:

```Kt
inkseekbar_top_down?.generalCornerRadii = listOf(32f)
inkseekbar_top_down?.primaryMargin = 10f
inkseekbar_top_down?.secondaryMargin = 15f
inkseekbar_top_down?.maxProgress = maxProgress
inkseekbar_top_down?.backgroundColors?.apply {
  clear()
  add(resources.getColorCompat(R.color.basic_black))
  add(resources.getColorCompat(R.color.basic_grey))
}
it.setPrimaryProgress(it.primaryProgress+1, fireListener = false)
```

## By XML

Example:

```XML
app:backgroundColor="@color/basic_grey"
app:primaryColor="@color/colorPrimary"
app:secondaryColor="@color/colorPrimaryDark"
app:primaryColors="@array/blue_gradient_reverse"
app:primaryGradientOrientation="LEFT_RIGHT"
app:markerColor="@color/basic_red"
app:markerHeight="50dp"
app:markerWidth="50dp"
app:markerCorners="160dp"
app:corners="16dp"
app:secondaryMargin="4dp"
```
Those are pretty basic. Some notes:
  - *lineWidth* is the width of the background line on vertical modes, but the height on horizontal modes.
  - *corners* is a general variable, and more precise variables would take precedence over it (for example, *markerCorners*).
  - *primaryColors* would take precedence over *primaryColor*.
  - *primaryMargin* and *secondaryMargin* allows you to have some space between layers.

```XML
app:orientation="LEFT_RIGHT"
```
*app:orientation* values
  - **TOP_DOWN**: vertical, from top to down
  - **DOWN_TOP**: vertical, from down to top
  - **LEFT_RIGHT**: horizontal, from left to right
  - **RIGHT_LEFT**: not working at the moment (v1.1.0)
  
```XML
app:mode="SEEKBAR"
``` 
*app:mode* values
  - **SEEKBAR**: allows to se primary progress by hand, and shows the marker
  - **PROGRESS**: can only set values by code, and can't show the marker

# Listeners

```kt
  /**
	 * Fired on any value change, primary or secondary. But only if fired by user (or fromUser==true), either for primary or for secondary value change.
	 */
	var onValueChangeListener: ((primary: Int, secondary: Int) -> Unit)? = null
	/**
	 * Fired on any primary value change.
	 */
	var onValuePrimaryChangeListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired on any primary value change.
	 */
	var onValueSecondaryChangeListener: ((secondary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired when user releases touch or when progress is set programmatically
	 */
	var onValuePrimarySetListener: ((primary: Int, fromUser: Boolean) -> Unit)? = null
	/**
	 * Fired when progress is set programmatically
	 */
	var onValueSecondarySetListener: ((secondary: Int, fromUser: Boolean) -> Unit)? = null
```
