package org.example.system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.recources.Dragon;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private static SocketChannel socket;
    private SocketChannel serverSocketChannel;
    String filePath = System.getenv("MY_FILE_PATH");
    private File file;
    BufferedReader consoleReader;



    public void initialize(String host, int port) throws IOException {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            socket = SocketChannel.open();
            socket.connect(address);
            socket.configureBlocking(false);
        } catch (RuntimeException e) {
            System.out.println("Server " + host + " on port " + port + " is not available");
            System.exit(1);
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonLocalDate()).create();
        DragonGenerator dragonGenerator = new DragonGenerator();
        System.out.println("Welcome to app!");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] commandLine = input.split(" ");
            String[] arguments = Arrays.copyOfRange(commandLine, 1, commandLine.length);
            System.out.println(arguments.toString());
            String command = commandLine[0];
            Request request = null;
            Dragon dragon = null;

            if (!command.isEmpty()) {
                switch (command) {
                    case "exit":
                        System.exit(1);
                        break;
                    case "save":
                        System.out.println("Save command is not available");
                        break;
                    case "update":
                        long id = Long.parseLong(arguments[0]);
                        dragon = dragonGenerator.createDragon(id);
                        break;
                    case "insert":
                        dragon = dragonGenerator.createDragon();
                        break;
                    case "execute_script":
                        if (arguments.length != 0) {
                            ExecuteScript.execute(arguments[0]);
                        } else {
                            System.out.println("Something wrong with arguments. Write script file name");
                        }
                        break;
                    default:
                        dragon = null;
                        break;
                }

                request = new Request(input, dragon, arguments);


                String jsonRequest = gson.toJson(request, Request.class) + "\n";
                try (SocketChannel channel = SocketChannel.open()) {
                    channel.connect(new InetSocketAddress("localhost", 6651));
                    ByteBuffer buffer = ByteBuffer.wrap(jsonRequest.getBytes(StandardCharsets.UTF_8));
                    channel.write(buffer);

                    ByteBuffer responseBuffer = ByteBuffer.allocate(8192);
                    channel.read(responseBuffer);
                    responseBuffer.flip();
                    String responseJson = StandardCharsets.UTF_8.decode(responseBuffer).toString();
                    Response response = gson.fromJson(responseJson.trim(), Response.class);
                    System.out.println(response.getMessage());

                } catch (IOException e) {
                    System.out.println("Server connection error " + e.getMessage());
                }
            }
        }
    }


    public static void sendRequest(Request request) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonLocalDate()).create();
        String jsonRequest = gson.toJson(request, Request.class) + "\n";
        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress("localhost", 6651));
            ByteBuffer buffer = ByteBuffer.wrap(jsonRequest.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);

            ByteBuffer responseBuffer = ByteBuffer.allocate(8192);
            channel.read(responseBuffer);
            responseBuffer.flip();
            String responseJson = StandardCharsets.UTF_8.decode(responseBuffer).toString();
            Response response = gson.fromJson(responseJson.trim(), Response.class);
            System.out.println(response.getMessage());

        } catch (IOException e) {
            System.out.println("Ошибка подключения к серверу: " + e.getMessage());
        }
    }
}
