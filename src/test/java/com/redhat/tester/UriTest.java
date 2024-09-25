package com.redhat.tester;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.Test;

import io.quarkus.logging.Log;

public class UriTest {

    String[] params = new String[]{"src/test/example1.yaml","http://website/example.yaml","https://httpbin.org/get", "file://tmp/test.yaml"};

    @Test
    public void testUri() throws URISyntaxException, IOException {
        for (String s : params) {
            URI uri = new URI(s);
            URL url;
            if(uri.isAbsolute()) {
                url= uri.toURL();
            } else {
                url = new URI("file",uri.toString(),null).toURL();
            }
            Log.infof("%s -> %s",uri,url);
        }
    }
}
