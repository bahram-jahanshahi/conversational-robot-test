package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

public class Resample48kTo16k {
    private Resample48kTo16k() {}

    /** Input: PCM16LE mono @48k. Output: PCM16LE mono @16k (decimate by 3). */
    public static byte[] pcm16le48kToPcm16le16k(byte[] pcm48k) {
        int samples48k = pcm48k.length / 2;
        int samples16k = samples48k / 3;

        byte[] out = new byte[samples16k * 2];

        int outByte = 0;
        for (int i = 0; i + 2 < samples48k; i += 3) {
            int inByte = i * 2;
            // copy 2 bytes (little endian) of the kept sample
            out[outByte++] = pcm48k[inByte];
            out[outByte++] = pcm48k[inByte + 1];
        }
        return out;
    }

    /** Input: PCM16LE mono @48k. Output: float mono @16k in [-1..1]. */
    public static float[] pcm16le48kToFloat16k(byte[] pcm48k) {
        int samples48k = pcm48k.length / 2;
        int samples16k = samples48k / 3;
        float[] out = new float[samples16k];

        int outIdx = 0;
        for (int i = 0; i + 2 < samples48k; i += 3) {
            int bi = i * 2;
            int lo = pcm48k[bi] & 0xFF;
            int hi = pcm48k[bi + 1];
            short s = (short) ((hi << 8) | lo);
            out[outIdx++] = s / 32768.0f;
        }
        return out;
    }
}
