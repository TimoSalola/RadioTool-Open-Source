package radiotool.example.timo.radiotool

import android.util.Log


/**
 * Created by Timo Salola on 4.7.2017.
 *
 * Based on Android application "Radio Tool"
 *
 * Copy and implement as you wish
 * Function for yagi uda director lengths is missing
 */
object AntennaPhysics {

    //Usable variables

    val SOL = 299793000.0

    private val spacingArray = arrayOf(0.0, 0.2, 0.075, 0.180, 0.215, 0.250, 0.280, 0.300, 0.315, 0.330, 0.345, 0.360, 0.375, 0.390, 0.400, 0.400)

    fun mhzToMeters(freq: Double): Double{

        val freqHZ = freq*1000*1000
        val wavelenght = SOL /freqHZ

        return wavelenght

    }


    fun mhzToHalfWave(value: Double): Double{
        //Returns total length for a half wave dipole 
        return mhzToMeters(value) /2
    }



    fun roundTo2Digits(value: Double): Double{
        //Rounds given value to 2 digits

        val output = ((value*100).toInt()).toDouble()/100

        return output

    }

    fun roundTo3Digits(value: Double): Double{
        //Rounds given value to 3 digits
        val output = ((value*1000).toInt()).toDouble()/1000
        return output
    }

    fun ShouldBeConvertedToCM(value: Double): Boolean{
        //Tests if value is under 1, used for checking if 0.4342m should be 43.42cm
        if (value < 1) { return true }
        return false
    }

    //Return in dBi
    fun calculateDishGain(freq: Double, diameter: Double, efficiency: Double): Double{

        val wavelength = mhzToMeters(freq)

        val middleValue = (efficiency/100)*(((Math.PI*diameter)/wavelength)*((Math.PI*diameter)/wavelength))

        val gainV2 = 10.0*(Math.log(middleValue)/Math.log(10.0))

        return gainV2


    }

    fun calculateBeamwidth(freq: Double, diam: Double): Double{
        //Never used, might not work
        
        return mhzToMeters(freq) /diam
        //Returned value in radians
    }
    

    //Returns element position in WL
    fun calculateYagiElementPosition(elements: Int): Double {

        var lengthInWavelengths = 0.0

        for (i in 0..elements - 1) {

            if (i > 13) {
                lengthInWavelengths += 0.3
            } else {
                lengthInWavelengths += spacingArray[i]
            }

        }

        return lengthInWavelengths
    }

    //Returns amount of elements on a boom with the given len
    fun calculateYagiElementsFitted(lenInWL :Double): Int{

        var elements = 0
        var atPos = 0.0

        while (true){
            if (atPos >= lenInWL){
                break
            } else {
                elements += 1
                atPos = calculateYagiElementPosition(elements)
            }
        }
        return elements -1
    }


    //FINISHED
    fun calculateYagiGain(amountOfelements: Int): Double {
        //Get distance from reflector to last director, calculate gain in dBi
        return 2.15 + 9.2 + 3.39 * Math.log(calculateYagiElementPosition(amountOfelements))
    }

    //FINISHED
    fun metersToWLofFreq(mhz: Double, meters:Double): Double{
        //Turns meters and frequencies to wl, 33 meters is ~3.x wavelengths of 27mhz
        return meters/ mhzToMeters(mhz)
    }

    //FINISHED
    fun calculateYagiReflector(elementDiameterInWaves: Double): Double{
        //Returns ref in WL from eleDia and wavelength in M
        return (((-20.0)/(186.77*Math.log(2.0/elementDiameterInWaves)-320.0)) + 1.0 )/2.0
    }

    //FINISHED
    fun calculateYagiDriver(elementDiameterInWaves: Double): Double{
        //Calculates the length of yagi-uda driver element.
        var driverLen = (0.4777-(1.0522*elementDiameterInWaves)+(0.43363*(Math.pow(elementDiameterInWaves, -0.014891))))/2
        driverLen *= 1.02
        return driverLen
        //Value seems to be right, some adjustment might be required, *1.02 is shady
    }


    //FINISHED
    fun calculatePowerMultiplierDistance(currentD: Double, desiredD: Double): Double{
        //Returns the ratio of squares
        return (desiredD*desiredD)/(currentD*currentD)
    }




}