package simbot.cycle.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageTagService {
    String getImageUrl(String name) throws IOException;

    InputStream getImageInputStream(String name) throws IOException;

    String getDuitangUrl(String name) throws IOException;

    String ranDom() throws IOException;
    String ranDomR18(String word) throws IOException;

    void ClearData();

    Boolean initRank();
}
