package org.example.auth;

public class App {
    static void main() {
        System.out.println("Simple Auth System starting...");
        Database.init();
        System.out.println("Готово, можно двигаться дальше.");
        String hash = PasswordHasher.hash("hello");
        System.out.println(hash);
        System.out.println(PasswordHasher.verify("hello", hash));
        System.out.println(PasswordHasher.verify("wrong", hash));
    }
}
