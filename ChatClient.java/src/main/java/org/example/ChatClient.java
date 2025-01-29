package org.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера
    private static final int PORT = 12345; // Порт сервера

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Введите ваше имя: ");
            String username = scanner.nextLine();
            out.println(username + " присоединился к чату!");

            System.out.println("Подключено к серверу. Введите ваше сообщение:");

            // Поток для получения сообщений от сервера
            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("\n" + message);
                    }
                } catch (IOException e) {
                    System.out.println("Соединение закрыто.");
                }
            });
            listener.start();

            // Чтение сообщений от пользователя и отправка их на сервер
            while (true) {
                System.out.print(username + ": ");
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("exit")) {
                    out.println(username + " покинул чат.");
                    break;
                }
                out.println(username + ": " + userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
