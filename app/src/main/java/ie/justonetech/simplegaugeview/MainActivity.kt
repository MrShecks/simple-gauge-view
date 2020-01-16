package ie.justonetech.simplegaugeview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speedGaugeView0.value = progress
                speedGaugeView1.value = progress
                speedGaugeView2.value = progress
                speedGaugeView3.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        speedGaugeView0.setOnValueChangeListener(object: SimpleGaugeView.OnValueChangeListener {
            override fun onValueChanged(view: SimpleGaugeView, value: Int) {
                Log.i(TAG, "onValueChanged(): view=$view, value=$value")
            }

        })

        button1.setOnClickListener {
            speedGaugeView0.animateTo(0, 1000)
            speedGaugeView1.animateTo(0, 1000)
            speedGaugeView2.animateTo(0, 1000)
            speedGaugeView3.animateTo(0, 1000)
        }

        button2.setOnClickListener {
            speedGaugeView0.animateTo(75, 1000)
            speedGaugeView1.animateTo(75, 1000)
            speedGaugeView2.animateTo(75, 1000)
            speedGaugeView3.animateTo(75, 1000)

        }

        button3.setOnClickListener {
            speedGaugeView0.animateTo(100, 1000)
            speedGaugeView1.animateTo(100, 1000)
            speedGaugeView2.animateTo(100, 1000)
            speedGaugeView3.animateTo(100, 1000)
        }
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG, "value=${speedGaugeView0.value}, minValue=${speedGaugeView0.minValue}, maxValue=${speedGaugeView0.maxValue}")

        seekBar.max = speedGaugeView0.maxValue
        seekBar.progress = speedGaugeView0.value
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//        //Log.i(TAG, "value=${speedGaugeView0.value}, minValue=${speedGaugeView0.minValue}, maxValue=${speedGaugeView0.maxValue}")
//
//        //seekBar.min = speedGaugeView0.minValue
//        seekBar.max = speedGaugeView0.maxValue
//        seekBar.progress = speedGaugeView0.value
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}