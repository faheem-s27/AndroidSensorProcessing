package com.faheemsaleem.sensorsend

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faheemsaleem.sensorsend.ui.theme.SensorSendTheme

private lateinit var sensorManager: SensorManager
private lateinit var gravitySensor: Sensor
private lateinit var gyroSensor: Sensor
private var sensorModel: SensorViewModel? = null

class MainActivity : ComponentActivity(), SensorEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!!
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!

        super.onCreate(savedInstanceState)
        setContent {
            SensorSendTheme {
                sensorModel = viewModel<SensorViewModel>()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   EnterIPScreen(sensorModel!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gravitySensor.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroSensor.also { gyroscope ->
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // do a single string with all the values
        if (event == null) return

        val sensorData = when (event.sensor.type) {
            Sensor.TYPE_GRAVITY -> SensorData(
                event.values[0], event.values[1], event.values[2],
                0f, 0f, 0f // Placeholder for gyroscope values
            )
            Sensor.TYPE_GYROSCOPE -> SensorData(
                0f, 0f, 0f, // Placeholder for gravity values
                event.values[0], event.values[1], event.values[2]
            )
            else -> return
        }

        // Write to logcat (optional)
        // Log.d("SensorData", sensorData.toString())

        sensorModel?.sendData(sensorData)

//        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
//            val sensorData = event.values.let { GravityData(it[0], it[1], it[2]) }
//            // write to logcat
//            //Log.d("SensorData", sensorData.toString())
//            sensorData.let { sensorModel?.sendData(it) }
//        }
//        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
//            val sensorData = event.values.let { GyroscopeData(it[0], it[1], it[2]) }
//            // write to logcat
//            //Log.d("SensorData", sensorData.toString())
//            sensorData.let { sensorModel?.sendData(it) }
//        }
//        event?.let { sensorEvent ->
//            when (sensorEvent.sensor.type) {
//                Sensor.TYPE_GRAVITY -> {
//                    val sensorData = GravityData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
//                    sensorData.let { sensorModel?.sendDataGravity(listOf(it)) }
//                }
//                Sensor.TYPE_GYROSCOPE -> {
//                    val sensorData = GyroscopeData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
//                    sensorData.let { sensorModel?.sendDataGyro(listOf(it)) }
//                }
//
//                else -> {
//                    Log.d("SensorData", "Unknown sensor type: ${sensorEvent.sensor.type}")
//                }
//            }
//        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do nothing
    }
}

@Composable
fun EnterIPScreen(viewModel: SensorViewModel = SensorViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        if (viewModel.connected) {
            Text(
                "Connected to ${viewModel.ipAddress}",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = { viewModel.disconnect() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Disconnect")
            }

            // at the bottom add the values
        }
        else {
            Text(
                "Enter IP Address",
                modifier = Modifier
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = viewModel.ipAddress,
                onValueChange = viewModel::onIpAddressChange,
                label = { Text("IP Address") },
                placeholder = { Text("192.168.x.xx") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            FilledTonalButton(
                onClick = viewModel::connect,
                modifier = Modifier.padding(16.dp),
                enabled = !viewModel.connected
            ) {
                Text(text = "Connect")
            }
        }
    }
}


@PreviewDynamicColors()
@PreviewLightDark()
@Preview(showBackground = true,
    showSystemUi = true)
@Composable
fun GreetingPreview() {
    SensorSendTheme {
        EnterIPScreen()
    }
}
