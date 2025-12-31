package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import java.io.*;
import java.util.*;

public class UtteranceBuffer {

    private final int maxPreRollFrames;
    private final ArrayDeque<byte[]> preRoll = new ArrayDeque<>();
    private ByteArrayOutputStream current; // null when not recording

    public UtteranceBuffer(int maxPreRollFrames) {
        this.maxPreRollFrames = maxPreRollFrames;
    }

    /** Always call this for every frame to keep pre-roll warm. */
    public void addToPreRoll(byte[] framePcm) {
        preRoll.addLast(framePcm);
        while (preRoll.size() > maxPreRollFrames) {
            preRoll.removeFirst();
        }
    }

    /** Call when speech starts. */
    public void start() {
        current = new ByteArrayOutputStream(64_000);
        // dump pre-roll first
        for (byte[] f : preRoll) {
            current.writeBytes(f);
        }
    }

    /** Append speech frame while recording. */
    public void append(byte[] framePcm) {
        if (current != null) current.writeBytes(framePcm);
    }

    /** Call when speech ends; returns full utterance PCM, and stops recording. */
    public byte[] stopAndGetPcm() {
        if (current == null) return new byte[0];
        byte[] pcm = current.toByteArray();
        current = null;
        return pcm;
    }

    public boolean isRecording() {
        return current != null;
    }
}
