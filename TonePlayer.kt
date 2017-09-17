package com.example.timo.toneplayer

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import org.jetbrains.anko.async

/**
 * Created by Timo on 17.9.2017.
 *
 * Based on morse player in "Radio Tool"
 * 
 * Copy and use as you wish
 * 
 * Example usage:
 *      Play 400Hz for 3.2 seconds:
 *          TonePlayer.playHzForS(400, 3.2)
 *
 *      Play 300Hz for 600ms:
 *          TonePlayer.playHzForMs(300, 600)
 * 
 * 
 *
 */
object TonePlayer {


    //Default values
    var sampleRate = 48000
    var toneLen = 1000
    var frequency = 1000






    fun playHzForS(givenFreq: Int, givenS: Double){

        val durationInMs = (givenS*1000).toInt()

        playHzForMs(givenFreq, durationInMs)

    }

    //FINISHED
    fun playHzForMs(givenFreq: Int, givenLen: Int){
        //Main function, builds an array full of sine values and passes it onto playPCMArray function

        async {
            toneLen = givenLen
            frequency = givenFreq

            var sineArray = getSinePCMArray()

            playPCMArray(sineArray)

        }


    }

    fun setCustomSampleRate(givenSampleRate: Int){
        //IMPORTANT, 44100 and 48000 are the recommended sample rates
        sampleRate = givenSampleRate
    }


    private fun getSinePCMArray(): ShortArray{

        val totalSamples = (sampleRate * toneLen/1000) -1 //Calculating total sample count

        var sinArray = ShortArray(totalSamples) //Initializing the sine array


        val samplesPerWave = sampleRate/ frequency
        var degrees = 0.0
        val deltaDegrees = Math.PI/samplesPerWave *10


        //for i in 0..totalsamples-1
        for(i in 0..totalSamples-1){

            sinArray[i] = (Math.sin(degrees)*32767).toShort() //Max multiplier is 32767
            degrees += deltaDegrees

        }
        //Now sineArray should be filled, values from -32767 to 32767
        

        var posExamined = sinArray.size-1

        while (true){ //End smoothing for pop removal, looping of a single 0 to 0 sine wave should be used instead

            if (sinArray[posExamined] > -12 && sinArray[posExamined] < 12){

                break
            } else {
                sinArray[posExamined] = 0
                posExamined--

            }
        }

        return sinArray

    }

    fun playPCMArray(sineArray: ShortArray){

        val shortMorse = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                sineArray.size,
                AudioTrack.MODE_STREAM
        )
        shortMorse.play()
        shortMorse.write(sineArray, 0, sineArray.size)
        shortMorse.stop()
        shortMorse.release()
        Thread.sleep(toneLen.toLong())

    }

    

}