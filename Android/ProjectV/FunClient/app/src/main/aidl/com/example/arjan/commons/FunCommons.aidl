// FunCommons.aidl
package com.example.arjan.commons;

// Declare any non-default types here with import statements

interface FunCommons {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Bitmap getPicture(int index);
    void playSong(int i);
    void stopSong();
         void pauseSong();
         void resumeSong();
}
