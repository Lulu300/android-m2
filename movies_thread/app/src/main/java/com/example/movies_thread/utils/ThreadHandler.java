package com.example.movies_thread.utils;

import android.os.Handler;
import android.os.Message;

public class ThreadHandler extends Handler {
    @Override
    public void handleMessage(Message inputMessage) {
        ImageDownloaderThread imageDownloaderThread = (ImageDownloaderThread) inputMessage.obj;

    }

}
