package com.arnava.netcalc;

import com.arnava.interpreter.calculator.Calculator;
import com.arnava.interpreter.exceptions.SyntaxErrorException;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;
    private final int delay;

    public ServerThread(Socket socket, int delay) {
        this.socket = socket;
        this.delay = delay;
    }

    public void run(){
        Calculator calculator = new Calculator();
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String expression;

            while (true) {
                expression = reader.readLine();
                if (expression.equals("exit")) {
                    System.out.println("The client was disconnected");
                    break;
                }
                Thread.sleep(delay);
                writer.println("result = " + calculator.calculate(expression));
            }
            socket.close();
        } catch (IOException | SyntaxErrorException | InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}

