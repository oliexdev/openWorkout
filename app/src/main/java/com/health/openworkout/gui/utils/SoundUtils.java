/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.utils;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;

import com.health.openworkout.core.OpenWorkout;

import java.io.IOException;

import timber.log.Timber;

public class SoundUtils {
    public enum SOUND {WORKOUT_START, WORKOUT_STOP}
    private static TextToSpeech ttS;

    public SoundUtils() {
    }

    public static void playSound(SOUND sound) {
        Context context = OpenWorkout.getInstance().getContext();

        AssetManager assetManager = context.getAssets();
        MediaPlayer mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd;

        try {
            switch (sound) {
                case WORKOUT_START:
                    afd = assetManager.openFd("sound/workout_start.mp3");
                    break;
                case WORKOUT_STOP:
                    afd = assetManager.openFd("sound/workout_stop.mp3");
                    break;
                default:
                    afd = assetManager.openFd("sound/workout.mp3");
                    break;
            }

            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            Timber.e(ex);
        }
    }

    public static void textToSpeech(final String speech) {
        Context context = OpenWorkout.getInstance().getContext();

        ttS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.ERROR) {
                    Timber.e("Can't initialize text to speech");
                }

                if (status == TextToSpeech.SUCCESS) {
                    //ttS.setLanguage(Locale.GERMAN);
                    ttS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "textToSpeech");
                }
            }
        });
    }

}
