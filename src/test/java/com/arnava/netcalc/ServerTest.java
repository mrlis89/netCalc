package com.arnava.netcalc;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ServerTest {

    @Test
    void run5000ClientThreads() throws InterruptedException {
        runServerThread(6060, 0);
        ArrayList<Thread> threads = new ArrayList<>(11);
        for (int i = 0; i < 11; i++) {
            threads.add(new Thread(new ClientImpl(6060, "45+5")));
        }
        ;
        while (true) {
            for (Thread currentThread: threads) {
                {
                    if (!currentThread.isAlive()) {
                        currentThread.start();
                    } else currentThread.interrupt();
                }
            }
        }
    }

    @Test
    void getCalculationResultFromServerForStartedClientProcess() throws IOException {
        runServerThread(6060, 0);
        Process clientProcess = runClientProcess();
        PrintWriter printWriter = new PrintWriter(clientProcess.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientProcess.getInputStream()));
        printWriter.println("45+5");
        assertThat(toArrayFrom(bufferedReader)).contains("result = 50");
    }

    private ArrayList<String> toArrayFrom(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> result = new ArrayList<>();
        do {
            line = reader.readLine();
            result.add(line);
        } while (!line.startsWith("result"));
        return result;
    }

    void runServerThread(int port, int delay) {
        new Thread(new ServerImpl(port, delay)).start();
    }

    void runClientThread() throws InterruptedException {
        Thread thread = new Thread(new ClientImpl(6060, "45+5"));
        thread.start();
        thread.join();
    }

    Process runClientProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./libs/ClientForCalc.jar");
        return pb.start();
    }

    class ServerImpl implements Runnable {
        private final int port;
        private final int delay;

        ServerImpl(int port, int delay) {
            this.port = port;
            this.delay = delay;
        }

        @Override
        public void run() {
            Server server = new Server(port, delay);
            server.start();
        }
    }

    class ClientImpl implements Runnable {
        private final int port;
        private final String expression;

        ClientImpl(int port, String expression) {
            this.port = port;
            this.expression = expression;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket("localhost", port)) {
//                System.out.println("Connected to server");
                OutputStream output = socket.getOutputStream();
                    PrintWriter sender = new PrintWriter(output, true);
                    InputStream input = socket.getInputStream();
                    BufferedReader receiver = new BufferedReader(new InputStreamReader(input));

                    sender.println(expression);
                    String result = receiver.readLine();
                    System.out.println(result);
                    assertThat(result).isEqualTo("result = 50");
                    sender.println("exit");
            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }
            Thread.currentThread().interrupt();
        }
    }
}
