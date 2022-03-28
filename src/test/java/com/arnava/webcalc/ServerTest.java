package com.arnava.webcalc;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ServerTest {

    @Test
    void checkForConnection() throws IOException {
        runServer();
        Process clientProcess = runClientProcess();
        PrintWriter printWriter = new PrintWriter(clientProcess.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientProcess.getInputStream()));
        printWriter.println("45+5");
        assertThat(toArrayFrom(bufferedReader)).contains("result = 50");
    }

    private ArrayList<String> toArrayFrom(BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> result = new ArrayList<>();
        do  {
            line = reader.readLine();
            result.add(line);
        } while (!line.startsWith("result"));
        return result;
    }

    void runServer() {
        new Thread(new ServerImpl()).start();
    }

    Process runClientProcess() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./libs/ClientForCalc.jar");
        return pb.start();
    }

    class ServerImpl implements Runnable {

        @Override
        public void run() {
            Server server = new Server(6060);
            server.start();
        }
    }
}