package com.faheemsaleem.sensorsend

import android.hardware.Sensor
import android.provider.Telephony.Carriers.PORT
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.zip.GZIPOutputStream

data class GravityData(val x: Float, val y: Float, val z: Float) : Serializable {
    override fun toString(): String {
        return "GravityData(x=$x, y=$y, z=$z)"
    }
}
data class GyroscopeData(val x: Float, val y: Float, val z: Float) : Serializable {
    override fun toString(): String {
        return "GyroscopeData(x=$x, y=$y, z=$z)"
    }
}
data class AccelerometerData(val x: Float, val y: Float, val z: Float)

data class SensorData(
    val gravityX: Float,
    val gravityY: Float,
    val gravityZ: Float,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float
) : Serializable {
    override fun toString(): String {
        return "SensorData(gravityX=$gravityX, gravityY=$gravityY, gravityZ=$gravityZ, gyroX=$gyroX, gyroY=$gyroY, gyroZ=$gyroZ)"
    }
}

class SensorViewModel() : ViewModel() {
    private val _ipAddress = mutableStateOf("192.168.0.151")
    val ipAddress: String
        get() = _ipAddress.value

    private val _connected = mutableStateOf(false)
    val connected: Boolean
        get() = _connected.value

    private val _sensorList = mutableStateOf(emptyList<Sensor>())
    val sensorList: List<Sensor>
        get() = _sensorList.value

    fun onSensorListChange(newSensorList: List<Sensor>) {
        _sensorList.value = newSensorList
    }

    fun onIpAddressChange(newIpAddress: String) {
        _ipAddress.value = newIpAddress
    }

    fun disconnect() {
        _connected.value = false
    }

    fun connect() {
        if (ipAddress.isBlank()) {
            return
        }
        _connected.value = true
    }

    private val gravityScope = CoroutineScope(Dispatchers.IO)
    private val gyroScope = CoroutineScope(Dispatchers.IO)
    private val socket by lazy { DatagramSocket() }

    fun sendData(sensorData: SensorData) {
        if (!connected) {
            return
        }

        gravityScope.launch {
            try {
                val address = InetAddress.getByName(ipAddress)

                // **Serialize sensor data:**
                val byteArray = sensorData.toString().toByteArray()
                val packet = DatagramPacket(byteArray, byteArray.size, address, 1593)
                socket.send(packet)

                Log.d("SensorDataSent", "Sent: $sensorData")
            } catch (e: IOException) {
                Log.e("SendDataError", "Error sending sensor data", e)
            }
        }
    }

    fun sendDataGravity(sensorDataList: List<GravityData>) {
        if (!connected || sensorDataList.isEmpty()) {
            return
        }

        gravityScope.launch {
            try {
                val address = InetAddress.getByName(ipAddress)
                val outputStream = ByteArrayOutputStream()
                val objectOutputStream = ObjectOutputStream(GZIPOutputStream(outputStream))

                // Write sensor data list to the compressed stream
                objectOutputStream.writeObject(sensorDataList)
                objectOutputStream.close()

                val compressedData = outputStream.toByteArray()
                val packet = DatagramPacket(compressedData, compressedData.size, address, 1593)
                socket.send(packet)

                Log.d("SensorDataSent", "Sent: ${sensorDataList.size} sensor readings")
            } catch (e: IOException) {
                Log.e("SendDataError", "Error sending sensor data", e)
            }
        }
    }

    fun sendDataGyro(sensorDataList: List<GyroscopeData>) {
        if (!connected || sensorDataList.isEmpty()) {
            return
        }

        gravityScope.launch {
            try {
                val address = InetAddress.getByName(ipAddress)
                val outputStream = ByteArrayOutputStream()
                val objectOutputStream = ObjectOutputStream(GZIPOutputStream(outputStream))

                // Write sensor data list to the compressed stream
                objectOutputStream.writeObject(sensorDataList)
                objectOutputStream.close()

                val compressedData = outputStream.toByteArray()
                val packet = DatagramPacket(compressedData, compressedData.size, address, 1593)
                socket.send(packet)

                Log.d("SensorDataSent", "Sent: ${sensorDataList.size} sensor readings")
            } catch (e: IOException) {
                Log.e("SendDataError", "Error sending sensor data", e)
            }
        }
    }

    fun sendData(sensorData: GravityData) {
        if (!connected) {
            return
        }

        gravityScope.launch {
            try {
                val address = InetAddress.getByName(ipAddress)
                val data = sensorData.toString().toByteArray()
                val packet = DatagramPacket(data, data.size, address, 1593)
                socket.send(packet)

                Log.d("SensorDataSent", "Sent: $sensorData")
            } catch (e: IOException) {
                Log.e("SendDataError", "Error sending sensor data", e)
            }
        }
    }

    fun sendData(sensorData: GyroscopeData) {
        if (!connected) {
            return
        }

        gyroScope.launch {
            try {
                val address = InetAddress.getByName(ipAddress)
                val data = sensorData.toString().toByteArray()
                val packet = DatagramPacket(data, data.size, address, 1593)
                socket.send(packet)

                Log.d("SensorDataSent", "Sent: $sensorData")
            } catch (e: IOException) {
                Log.e("SendDataError", "Error sending sensor data", e)
            }
        }
    }


}
