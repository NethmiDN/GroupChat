package com.example.groupchat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 4000;
    private static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                out.println("Type HELP for commands.");

                String command;
                while ((command = in.readLine()) != null) {
                    command = command.trim().toUpperCase();
                    switch (command) {
                        case "TIME":
                            out.println("Current Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                            break;
                        case "DATE":
                            out.println("Current Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            break;
                        case "UPTIME":
                            long uptime = (System.currentTimeMillis() - startTime) / 1000;
                            out.println("Server Uptime: " + uptime + " seconds");
                            break;
                        case "BYE":
                            out.println("Disconnected. Goodbye!");
                            return;
                        case "HELP":
                            out.println("Commands: TIME, DATE, UPTIME, BYE");
                            break;
                        default:
                            out.println("Unknown command. Type HELP for a list.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + socket.getInetAddress());
            }
        }
    }
}