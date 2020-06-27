package com.mastercard.codetest.jerseystore.file;

public abstract class FileLoader {

    /**
     * Receives a path to a csv file and loads the data to the database
     * @param path - the file path
     */
    abstract void load(String path);
}
