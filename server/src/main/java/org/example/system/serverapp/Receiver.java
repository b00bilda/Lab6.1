package org.example.system.serverapp;

import org.example.managers.ServerEnvironment;
import org.example.system.Request;

public class Receiver {
    public static String clear(Request request) {
        if (request.getMessage().split("").length == 1) {
            ServerEnvironment.getInstance().getCollectionManager().getCollection().clear();
            return "Collection is cleared";
        } else {
            throw new IllegalArgumentException("command parameter");
        }
    }
    public static String exit(Request request) {
        if (request.getMessage().split("").length == 1 ) {
            System.exit(0);
            return "";
        } else {
            throw new IllegalArgumentException("command parameter");
        }
    }
    public static String filterLessThanWeight(Request request){
        if (request.getMessage().split("").length == 2){
            ServerEnvironment.getInstance().getCollectionManager().filterLessThanWeight(request);
            return "Collection was changed";
        } else {
            throw new IllegalArgumentException("command parameter");
        }
    }
    public static String help(Request request){
        if (request.getMessage().split("").length == 1){
            ServerEnvironment.getInstance().getCommandManager().getCommandList().forEach((s, command) -> {
                System.out.println(s + ": " + command.getDescription());
            });
            return "";
        }else {
            throw new IllegalArgumentException("command parameter");
        }
    }
    public static String info()
}



