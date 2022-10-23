package simbot.cycle.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
    String getImageUrl(String name) throws IOException;

    InputStream getImageInputStream(String name) throws IOException;

    String getDuitangUrl(String name) throws IOException;

    InputStream ranDom() throws IOException;

    void ClearData();

    Boolean initRank();
}