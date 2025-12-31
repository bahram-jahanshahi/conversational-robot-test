package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

public final class Endpointing {
    public enum Event { NONE, SPEECH_START, SPEECH_END }

    private final float startTh;
    private final float endTh;

    private final int startFrames;   // consecutive speech-like frames to start
    private final int endFrames;     // consecutive non-speech frames to end

    private boolean inSpeech = false;
    private int speechRun = 0;
    private int silenceRun = 0;

    public Endpointing(float startTh,
                       float endTh,
                       double startAfterSeconds,
                       double endAfterSeconds,
                       int frameSamples,
                       int sampleRate) {
        this.startTh = startTh;
        this.endTh = endTh;

        double frameSec = (double) frameSamples / (double) sampleRate;
        this.startFrames = Math.max(1, (int) Math.ceil(startAfterSeconds / frameSec));
        this.endFrames   = Math.max(1, (int) Math.ceil(endAfterSeconds   / frameSec));
    }

    public Event update(float p) {
        // hysteresis to avoid flip-flop
        boolean speechLike;
        if (p >= startTh) speechLike = true;
        else if (p <= endTh) speechLike = false;
        else speechLike = inSpeech;

        if (!inSpeech) {
            if (speechLike) {
                speechRun++;
                if (speechRun >= startFrames) {
                    inSpeech = true;
                    silenceRun = 0;
                    return Event.SPEECH_START;
                }
            } else {
                speechRun = 0;
            }
            return Event.NONE;
        } else {
            if (!speechLike) {
                silenceRun++;
                if (silenceRun >= endFrames) {
                    inSpeech = false;
                    speechRun = 0;
                    return Event.SPEECH_END;
                }
            } else {
                silenceRun = 0;
            }
            return Event.NONE;
        }
    }

    public boolean isInSpeech() { return inSpeech; }
    public int getEndFrames() { return endFrames; } // helpful for logging/tuning
}
