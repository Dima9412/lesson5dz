package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345; // Порт для сервера
    private static Set<PrintWriter> clientWriters = new HashSet<>(); // Список потоков вывода для клиентов

    public static void main(String[] args) {
        System.out.println("Сервер запущен...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Ожидаем подключение нового клиента
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Вложенный класс для обработки каждого клиента в отдельном потоке
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Поток для чтения сообщений клиента
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Поток для отправки сообщений клиенту
                out = new PrintWriter(socket.getOutputStream(), true);

                // Добавляем поток клиента в список
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Получено: " + message);
                    broadcast(message); // Отправляем сообщение всем клиентам
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединение и удаляем клиента из списка
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        // Метод для рассылки сообщений всем клиентам
        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}

