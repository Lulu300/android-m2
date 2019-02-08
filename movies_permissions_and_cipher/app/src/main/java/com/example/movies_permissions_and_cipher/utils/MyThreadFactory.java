package com.example.movies_permissions_and_cipher.utils;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setPriority(5);
        return thread;
    }
}
