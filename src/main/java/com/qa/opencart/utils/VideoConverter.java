package com.qa.opencart.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for video operations and conversions
 * Note: This class provides suggestions for video conversion but actual conversion
 * requires external tools like FFmpeg that need to be installed separately
 */
public class VideoConverter {

    /**
     * Creates a copy of the video file with a .mp4 extension
     * This doesn't actually convert the video, but helps with MIME type issues in some browsers
     * 
     * @param webmVideoPath Path to the WebM video file
     * @return Path to the "MP4" file (just a renamed copy)
     */
    public static String createMp4Copy(String webmVideoPath) {
        try {
            if (webmVideoPath == null || !webmVideoPath.toLowerCase().endsWith(".webm")) {
                return null;
            }
            
            // Create the MP4 file path by replacing .webm with .mp4
            String mp4VideoPath = webmVideoPath.substring(0, webmVideoPath.lastIndexOf(".")) + ".mp4";
            
            // Copy the WebM file to an MP4 file
            Path source = Paths.get(webmVideoPath);
            Path target = Paths.get(mp4VideoPath);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Created MP4 copy at: " + mp4VideoPath);
            return mp4VideoPath;
        } catch (IOException e) {
            System.err.println("Error creating MP4 copy: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Suggests how to convert WebM to MP4 using FFmpeg
     * This method just prints the command, it doesn't perform the conversion
     * 
     * @param webmVideoPath Path to the WebM video file
     */
    public static void suggestFfmpegConversion(String webmVideoPath) {
        if (webmVideoPath == null || !webmVideoPath.toLowerCase().endsWith(".webm")) {
            return;
        }
        
        // Create the MP4 file path by replacing .webm with .mp4
        String mp4VideoPath = webmVideoPath.substring(0, webmVideoPath.lastIndexOf(".")) + ".mp4";
        
        System.out.println("\n====== FFmpeg Conversion Instructions ======");
        System.out.println("To convert WebM to MP4, install FFmpeg (https://ffmpeg.org/) and run:");
        System.out.println("ffmpeg -i \"" + webmVideoPath + "\" -c:v libx264 -crf 23 -preset medium -c:a aac -b:a 128k \"" + mp4VideoPath + "\"");
        System.out.println("==============================================\n");
    }
    
    /**
     * Checks if FFmpeg is installed on the system
     * 
     * @return true if FFmpeg is available, false otherwise
     */
    public static boolean isFFmpegAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("ffmpeg -version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Attempts to convert WebM to MP4 using FFmpeg if available
     * 
     * @param webmVideoPath Path to the WebM video file
     * @return Path to the converted MP4 file or null if conversion failed
     */
    public static String convertWithFfmpeg(String webmVideoPath) {
        if (!isFFmpegAvailable() || webmVideoPath == null || !webmVideoPath.toLowerCase().endsWith(".webm")) {
            suggestFfmpegConversion(webmVideoPath);
            return null;
        }
        
        try {
            // Create the MP4 file path by replacing .webm with .mp4
            String mp4VideoPath = webmVideoPath.substring(0, webmVideoPath.lastIndexOf(".")) + ".mp4";
            
            // Build FFmpeg command
            String[] command = {
                "ffmpeg", 
                "-i", webmVideoPath,
                "-c:v", "libx264",
                "-crf", "23",
                "-preset", "medium",
                "-c:a", "aac",
                "-b:a", "128k",
                mp4VideoPath
            };
            
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Successfully converted WebM to MP4: " + mp4VideoPath);
                return mp4VideoPath;
            } else {
                System.err.println("FFmpeg conversion failed with exit code: " + exitCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error converting video: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 