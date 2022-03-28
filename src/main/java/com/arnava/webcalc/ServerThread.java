package com.arnava.webcalc;

import com.arnava.interpreter.calculator.Calculator;
import com.arnava.interpreter.exceptions.SyntaxErrorException;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
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
                writer.println("result = " + calculator.calculate(expression));
            }
            socket.close();
        } catch (IOException | SyntaxErrorException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}

