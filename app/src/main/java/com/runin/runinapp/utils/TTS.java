package com.runin.runinapp.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * A TTS (Text-To-Speech) wrapper with extended functionality, designed to be robust and easy to use.
 *
 * @author Lukas Knuth
 * @author Modified by Samuel Kobelkowsky
 * @version 1.0
 */
public final class TTS {
    private final static String TAG = TTS.class.getSimpleName();
    private final AudioManager audioManager;
    private final Bundle ttsParams = new Bundle();
    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // React to audio-focus changes here!
        }
    };
    private TextToSpeech tts;
    private int speech_count = 0;

    /**
     * Creates a new Text-To-Speech engine.
     *
     * @param callback will be called, once the engine is initialised and ready for usage.
     * @throws java.lang.IllegalStateException you can't initialize this object with a
     *                                         {@code context} from an Activity that has not yet completed it's {@link android.app.Activity#onCreate(android.os.Bundle)}
     *                                         method. Maybe do it in {@link android.app.Activity#onStart()} instead?
     */
    public TTS(Context context, final InitCallback callback) {
        // TTS Parameters:
        ttsParams.putString(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));

        // Initialise Audio Manager to provides access to volume and ringer mode control:
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Initialize TTS
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utterance_id) {
                            TTS.this.onUtteranceCompleted(utterance_id);
                        }
                    });

                    int result = tts.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e(TAG, "Unsupported language");
                        callback.initFail(status);
                        return;
                    }

                    callback.initSuccess(TTS.this);
                }
                else if (status == TextToSpeech.ERROR) {
                    callback.initFail(status);
                }
            }
        });
    }

    public void stop() {
        tts.stop();
    }

    /**
     * Shutdown the TTS-Engine.
     *
     * @param stop_immediately whether the TTS-engine should let any previously queued speeches
     *                         finish, or stop them (and the engine) immediatly.
     */
    @SuppressWarnings("unused")
    public void shutdown(boolean stop_immediately) {
        if (stop_immediately) {
            tts.stop();
        }
        tts.shutdown();
    }

    /**
     * <p>Queue a text for reading out. This will only queue this text and waits, until any earlier
     * text's are done playing. Also, Music volume will be lowered (if supported by the current
     * media-player) while the text is spoken.</p>
     * <p>This method returns immediately after queuing the text.</p>
     *
     * @param text the text to read out.
     * @return whether the text was successfully queued for reading out, or not.
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public boolean queueSpeech(String text, int queue, @SuppressWarnings("SameParameterValue") String utteranceId) {
        // Media-Player should lower volume:
        int focus_res = audioManager.requestAudioFocus(
                audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        );
        // Talk:
        if (focus_res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Add the text to the queue:
            int queue_res = tts.speak(text, queue, this.ttsParams, utteranceId);
            if (queue_res == TextToSpeech.SUCCESS) {
                // Successfully queued:
                this.speech_count++;
                return true;
            }
        }
        return false;
    }

    public void speak(String text) {
        queueSpeech(text, TextToSpeech.QUEUE_ADD, "UTTERANCE_ID");
    }

    public void speakFlush(String text) {
        queueSpeech(text, TextToSpeech.QUEUE_FLUSH, "UTTERANCE_ID");
    }

    private void onUtteranceCompleted(@SuppressWarnings("unused") String utteranceId) {
        this.speech_count--;
        if (speech_count == 0) {
            // No more speeches are queued, give focus back:
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    public interface InitCallback {
        /**
         * Initialisation was successful, work with the TTS.
         */
        void initSuccess(TTS tts);

        /**
         * There was an error while initialising the engine.
         *
         * @param reason error-number, as returned by {@link android.speech.tts.TextToSpeech.OnInitListener#onInit(int)}.
         */
        void initFail(int reason);
    }
}
