package com.hash.datapipeline.exception;

public class OutlierDetectedException extends RuntimeException{

    public OutlierDetectedException(String message) {
        super(message);
    }
}
