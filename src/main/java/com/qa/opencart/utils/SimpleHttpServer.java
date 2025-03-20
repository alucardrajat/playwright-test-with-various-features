package com.qa.opencart.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * A simple HTTP server to serve test reports and videos
 * This helps avoid browser security restrictions when accessing local files
 */
public class SimpleHttpServer {
    private HttpServer server;
    private int port;
    private String rootDirectory;
    private boolean isRunning = false;

    /**
     * Create a new HTTP server
     * 
     * @param port Port to listen on
     * @param rootDirectory Directory to serve files from
     */
    public SimpleHttpServer(int port, String rootDirectory) {
        this.port = port;
        this.rootDirectory = rootDirectory;
    }

    /**
     * Start the HTTP server
     * 
     * @return URL to access the server
     * @throws IOException If the server cannot be started
     */
    public String start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new FileHandler(rootDirectory));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        isRunning = true;
        
        String serverUrl = "http://localhost:" + port;
        System.out.println("HTTP Server started at: " + serverUrl);
        System.out.println("To view test reports, open: " + serverUrl + "/reports/TestExecutionReport.html");
        
        return serverUrl;
    }

    /**
     * Stop the HTTP server
     */
    public void stop() {
        if (server != null && isRunning) {
            server.stop(0);
            isRunning = false;
            System.out.println("HTTP Server stopped");
        }
    }

    /**
     * Handler for serving files
     */
    static class FileHandler implements HttpHandler {
        private String rootDirectory;

        public FileHandler(String rootDirectory) {
            this.rootDirectory = rootDirectory;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            
            // Normalize the path and prevent directory traversal
            if (requestPath.contains("..")) {
                exchange.sendResponseHeaders(403, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            // Default to index.html for root path
            if (requestPath.equals("/")) {
                requestPath = "/reports/TestExecutionReport.html";
            }
            
            // Create the full path to the requested file
            Path filePath = Paths.get(rootDirectory, requestPath);
            
            // Check if the file exists
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                String response = "File not found: " + requestPath;
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
                return;
            }
            
            // Determine the MIME type
            String mimeType = getMimeType(filePath.toString());
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            
            // Send the file
            exchange.sendResponseHeaders(200, Files.size(filePath));
            Files.copy(filePath, exchange.getResponseBody());
            exchange.getResponseBody().close();
        }
        
        /**
         * Get the MIME type for a file
         * 
         * @param path Path to the file
         * @return MIME type
         */
        private String getMimeType(String path) {
            String lowerPath = path.toLowerCase();
            if (lowerPath.endsWith(".html") || lowerPath.endsWith(".htm")) {
                return "text/html";
            } else if (lowerPath.endsWith(".css")) {
                return "text/css";
            } else if (lowerPath.endsWith(".js")) {
                return "application/javascript";
            } else if (lowerPath.endsWith(".png")) {
                return "image/png";
            } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (lowerPath.endsWith(".gif")) {
                return "image/gif";
            } else if (lowerPath.endsWith(".webm")) {
                return "video/webm";
            } else if (lowerPath.endsWith(".mp4")) {
                return "video/mp4";
            } else if (lowerPath.endsWith(".webp")) {
                return "image/webp";
            } else if (lowerPath.endsWith(".svg")) {
                return "image/svg+xml";
            } else if (lowerPath.endsWith(".json")) {
                return "application/json";
            } else if (lowerPath.endsWith(".xml")) {
                return "application/xml";
            } else if (lowerPath.endsWith(".pdf")) {
                return "application/pdf";
            } else {
                return "application/octet-stream";
            }
        }
    }
    
    /**
     * Main method to start the server
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Start server on port 8000 serving from the current directory
            SimpleHttpServer server = new SimpleHttpServer(8000, ".");
            server.start();
            
            System.out.println("Server started. Press Ctrl+C to stop.");
            
            // Keep the server running until the JVM is shut down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
            }));
            
            // Wait for the JVM to be shut down
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 