package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;

import java.io.File;

/**
 * Service for video recording using JavaCV
 */
@Slf4j
public class VideoRecordingService {
    private FrameGrabber grabber;
    private FFmpegFrameRecorder recorder;
    private boolean isRecording = false;
    private Thread recordingThread;
    private String outputPath;
    private int frameRate = 30;
    private int width = 640;
    private int height = 480;
    
    /**
     * Initialize video recording
     */
    public void initialize(String outputDirectory, int width, int height, int frameRate) throws Exception {
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        outputPath = outputDirectory + "/video_" + timestamp + ".mp4";
        
        log.info("Initializing video recording to: {}", outputPath);
        
        // Initialize webcam grabber
        grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(width);
        grabber.setImageHeight(height);
        grabber.setFrameRate(frameRate);
        grabber.start();
        
        // Initialize recorder
        recorder = new FFmpegFrameRecorder(outputPath, width, height);
        recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);
        recorder.setVideoQuality(0); // Highest quality
        recorder.start();
        
        log.info("Video recording initialized successfully");
    }
    
    /**
     * Start recording
     */
    public void startRecording() {
        if (isRecording) return;
        
        isRecording = true;
        recordingThread = new Thread(() -> {
            try {
                log.info("Recording started");
                while (isRecording) {
                    Frame frame = grabber.grab();
                    if (frame != null && frame.image != null) {
                        recorder.record(frame);
                    }
                }
                log.info("Recording stopped");
            } catch (Exception e) {
                log.error("Error during recording", e);
            }
        });
        recordingThread.start();
    }
    
    /**
     * Stop recording
     */
    public String stopRecording() throws Exception {
        isRecording = false;
        
        if (recordingThread != null) {
            recordingThread.join(5000);
        }
        
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
        
        if (grabber != null) {
            grabber.stop();
            grabber.release();
        }
        
        log.info("Video saved to: {}", outputPath);
        return outputPath;
    }
    
    /**
     * Get preview frame
     */
    public Frame captureFrame() throws Exception {
        if (grabber != null) {
            return grabber.grab();
        }
        return null;
    }
    
    /**
     * Check if webcam is available
     */
    public boolean isWebcamAvailable() {
        try {
            FrameGrabber testGrabber = new OpenCVFrameGrabber(0);
            testGrabber.start();
            Frame frame = testGrabber.grab();
            testGrabber.stop();
            testGrabber.release();
            return frame != null;
        } catch (Exception e) {
            log.warn("Webcam not available: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
}

