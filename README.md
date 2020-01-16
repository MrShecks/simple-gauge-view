# simple-gauge-view
A simple custom gauge view for Android

Just a pretty basic gauge view control I wrote for my Road Trip Tracker application. I thought it might be useful as
as starting point if anyone wants to use it to build something a bit more advanced.

![Simple Gauge View](docs/screenshots/simple-gauge-view-000.png)

## Configuration
The view currently supports the following XML configuration attributes.


```xml
<resources>
    <declare-styleable name="SimpleGaugeView">

        <!-- Width of the background bar arc -->
        <attr name="gaugeView_barWidth" format="dimension" />

        <!-- Width of the foreground filled bar arc -->
        <attr name="gaugeView_fillBarWidth" format="dimension" />

        <!-- Solid fill color for background bar arc -->
        <attr name="gaugeView_barColor" format="color" />

        <!-- Solid fill color for foreground bar arc -->
        <attr name="gaugeView_fillColor" format="color" />

        <!-- Start and end colors for gradient fill used to fill the foreground bar arc -->
        <!-- Note: When these are both defined, the "fillColor" attribute is ignored    -->
        <attr name="gaugeView_fillColorStart" format="color" />
        <attr name="gaugeView_fillColorEnd" format="color" />

        <!-- Stoke end cap used when drawing both the foreground and background bars -->
        <attr name="gaugeView_strokeCap" format="enum">
            <enum name="butt" value="0" />
            <enum name="round" value="1" />
            <enum name="square" value="2" />
        </attr>

        <!-- Start angle and sweep amount (both degrees) of the foreground and background bar arcs -->
        <!-- 0 degrees is at 3 o'clock increasing clockwise with the defaults set to a startAngle  -->
        <!-- of 135 degrees and a sweep of 270 degrees                                             -->
        <attr name="gaugeView_startAngle" format="float" />
        <attr name="gaugeView_sweepAngle" format="float" />

        <!-- Default value for the gauge -->
        <attr name="gaugeView_value" format="integer" />

        <!-- Minimum and maximum values of the gauges range -->
        <attr name="gaugeView_minValue" format="integer" />
        <attr name="gaugeView_maxValue" format="integer" />

        <!-- Flag to control whether or not the gauge value is displayed, default = True -->
        <attr name="gaugeView_showValue" format="boolean" />

        <!-- Text size and color for the gauge value label -->
        <attr name="gaugeView_textSize" format="dimension" />
        <attr name="gaugeView_textColor" format="color" />

        <!-- Vertical offset for both the value text and label text, default is 0 pixels -->
        <!-- The gauge value and labels are displayed above and below the horizontal     -->
        <!-- center line of the view, this value can be used as an offset to raise or    -->
        <!-- lower the text                                                              -->
        <attr name="gaugeView_textOffset" format="dimension" />

        <!-- Text, color and size for option label displayed before the gauge value -->
        <attr name="gaugeView_labelSize" format="dimension" />
        <attr name="gaugeView_labelColor" format="color" />
        <attr name="gaugeView_labelText" format="string" />

    </declare-styleable>
</resources>
```

## Usage

```XML
<ie.justonetech.simplegaugeview.SimpleGaugeView
    android:id="@+id/speedGaugeView0"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:padding="10dp"

    app:gaugeView_barWidth="12dp"
    app:gaugeView_fillColorEnd="#ff00ffff"
    app:gaugeView_fillColorStart="#ff4d6ea3"
    app:gaugeView_labelSize="24sp"

    app:gaugeView_labelText="MPH"
    app:gaugeView_maxValue="100"
    app:gaugeView_strokeCap="butt"
    app:gaugeView_textSize="52sp"
    app:gaugeView_value="25"
 />
```
