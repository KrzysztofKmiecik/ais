package pl.kmiecik.ais.Config;

import java.util.NoSuchElementException;

public class RestApiGetException extends NoSuchElementException {
    public RestApiGetException(String s) {
        super(s);
    }
}
