package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import ai.onnxruntime.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class SileroVadOnnxModel implements AutoCloseable {

    // Model supports 8k and 16k; we run 16k for mic. :contentReference[oaicite:1]{index=1}
    public static final int SAMPLE_RATE = 16_000;

    // For 16k model: 512 window + 64 context (as in Silero examples) :contentReference[oaicite:2]{index=2}
    public static final int WINDOW_SAMPLES = 512;
    public static final int CONTEXT_SAMPLES = 64;

    // state: [2, batch, 128]
    private static final int STATE_D0 = 2;
    private static final int STATE_D2 = 128;

    private final OrtEnvironment env;
    private final OrtSession session;

    // Batch=1 implementation (easy + matches mic streaming)
    private final float[] stateFlat = new float[STATE_D0 * 1 * STATE_D2];
    private final float[] context = new float[CONTEXT_SAMPLES];

    public SileroVadOnnxModel(Path modelPath) throws OrtException {
        this.env = OrtEnvironment.getEnvironment();

        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        opts.setInterOpNumThreads(1);
        opts.setIntraOpNumThreads(1);
        opts.addCPU(true);

        this.session = env.createSession(modelPath.toString(), opts);

        // sanity prints
        System.out.println("VAD inputs : " + session.getInputNames());
        System.out.println("VAD outputs: " + session.getOutputNames());
        for (var e : session.getInputInfo().entrySet()) {
            System.out.println("Input " + e.getKey() + " -> " + e.getValue().getInfo());
        }
    }

    /** Reset streaming state/context (call after each utterance if you want clean boundaries). */
    public void reset() {
        Arrays.fill(stateFlat, 0f);
        Arrays.fill(context, 0f);
    }

    /**
     * Feed exactly 512 float samples (mono) in [-1..1], returns speech probability [0..1].
     */
    public float infer(float[] chunk512) throws OrtException {
        if (chunk512.length != WINDOW_SAMPLES) {
            throw new IllegalArgumentException("Expected " + WINDOW_SAMPLES + " samples, got " + chunk512.length);
        }

        // Build input = [context + chunk] => 576 samples
        int effective = CONTEXT_SAMPLES + WINDOW_SAMPLES;
        float[] input = new float[effective];
        System.arraycopy(context, 0, input, 0, CONTEXT_SAMPLES);
        System.arraycopy(chunk512, 0, input, CONTEXT_SAMPLES, WINDOW_SAMPLES);

        // Update context = last 64 samples of current chunk (matches Silero example idea) :contentReference[oaicite:3]{index=3}
        System.arraycopy(chunk512, WINDOW_SAMPLES - CONTEXT_SAMPLES, context, 0, CONTEXT_SAMPLES);

        // ---- Create tensors with explicit shapes to avoid rank surprises ----
        // input: float [1, 576]
        OnnxTensor inputTensor = null;
        // state: float [2, 1, 128]
        OnnxTensor stateTensor = null;
        // sr: int64 scalar []
        OnnxTensor srTensor = null;

        OrtSession.Result out = null;
        try {
            inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(input), new long[]{1, effective});
            stateTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(stateFlat), new long[]{2, 1, 128});

            // sr scalar (shape []) — IMPORTANT for your model signature
            LongBuffer srBuf = LongBuffer.allocate(1);
            srBuf.put(SAMPLE_RATE);
            srBuf.flip();
            srTensor = OnnxTensor.createTensor(env, srBuf, new long[]{});

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("input", inputTensor);
            inputs.put("state", stateTensor);
            inputs.put("sr", srTensor);

            out = session.run(inputs);

            // output: usually float[][] shape [1,1]
            Object ov = out.get("output").get().getValue();
            float prob;
            if (ov instanceof float[][] f2) prob = f2[0][0];
            else if (ov instanceof float[] f1) prob = f1[0];
            else throw new IllegalStateException("Unexpected output type: " + ov.getClass());

            // stateN: float[2][1][128] OR sometimes flat float[]
            Object sv = out.get("stateN").get().getValue();
            if (sv instanceof float[][][] s3) {
                int idx = 0;
                for (int a = 0; a < 2; a++) {
                    for (int b = 0; b < 1; b++) {
                        for (int c = 0; c < 128; c++) {
                            stateFlat[idx++] = s3[a][b][c];
                        }
                    }
                }
            } else if (sv instanceof float[] s1) {
                if (s1.length != stateFlat.length) {
                    throw new IllegalStateException("stateN length " + s1.length + " != " + stateFlat.length);
                }
                System.arraycopy(s1, 0, stateFlat, 0, stateFlat.length);
            } else {
                throw new IllegalStateException("Unexpected stateN type: " + sv.getClass());
            }

            return prob;
        } finally {
            if (out != null) out.close();
            if (inputTensor != null) inputTensor.close();
            if (stateTensor != null) stateTensor.close();
            if (srTensor != null) srTensor.close();
        }
    }

    @Override
    public void close() throws OrtException {
        session.close();
        env.close();
    }

    /** Copy model from classpath to a temp file so ORT can load it. */
    public static Path materializeFromResources(String resourcePath) throws IOException {
        try (InputStream is = SileroVadOnnxModel.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new FileNotFoundException("Resource not found: " + resourcePath);
            Path tmp = Files.createTempFile("silero_vad_", ".onnx");
            Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
            tmp.toFile().deleteOnExit();
            System.out.println("Model file: " + tmp + " bytes=" + Files.size(tmp));
            return tmp;
        }
    }
}
