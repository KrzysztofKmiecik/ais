package pl.kmiecik.ais.model;

import java.util.NoSuchElementException;

public class RestApiGetException extends NoSuchElementException {
    public RestApiGetException(String s) {
        super(s);
    }
}
