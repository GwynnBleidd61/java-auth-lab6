package org.example.auth;

public class App {
    static void main() {
        System.out.println("Simple Auth System starting...");
        Database.init();
        System.out.println("Готово, можно двигаться дальше.");
    }
}
