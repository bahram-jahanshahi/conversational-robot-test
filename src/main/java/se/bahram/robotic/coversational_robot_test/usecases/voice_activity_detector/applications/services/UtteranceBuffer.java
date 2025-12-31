package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import java.io.*;
import java.util.*;

public class UtteranceBuffer {

    private final int maxPreRollFrames;
    private final ArrayDeque<byte[]> preRoll = new ArrayDeque<>();
    private ByteArrayOutputStream current;

    public UtteranceBuffer(int maxPreRollFrames) {
        this.maxPreRollFrames = maxPreRollFrames;
    }

    public void clearPreRoll() {
        preRoll.clear();
    }

    /** Always call this for every frame to keep pre-roll warm. */
    public void addToPreRoll(byte[] framePcm) {
        // ✅ IMPORTANT: copy, don't store the same mutable array reference
        preRoll.addLast(Arrays.copyOf(framePcm, framePcm.length));
        while (preRoll.size() > maxPreRollFrames) {
            preRoll.removeFirst();
        }
    }

    public void start() {
        current = new ByteArrayOutputStream(64_000);
        for (byte[] f : preRoll) {
            current.writeBytes(f);
        }
    }

    public void append(byte[] framePcm) {
        if (current != null) {
            // this is already safe because writeBytes copies immediately,
            // but copying is also fine:
            current.writeBytes(framePcm);
        }
    }

    public byte[] stopAndGetPcm() {
        if (current == null) return new byte[0];
        byte[] pcm = current.toByteArray();
        current = null;
        preRoll.clear();
        return pcm;
    }

    public boolean isRecording() {
        return current != null;
    }
}
