package org.neo4j.dih.datasource.csv;

import org.neo4j.dih.datasource.AbstractResult;
import org.neo4j.dih.exception.DIHException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CSVResult extends AbstractResult{

    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String separator;
    private String encoding;
    private String current;

    public CSVResult(String url, String encoding, String separator) throws DIHException {
        try {
            this.inputStream = new URL(url).openStream();
        } catch (IOException e) {
            throw new DIHException("Error when trying to retrieve file %s", url);
        }
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        this.separator = separator;
        this.encoding = encoding;
        step();
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public Map<String, Object> next() {
        Map<String, Object> rs = new HashMap<String, Object>();

        String[] columns = current.split(separator);
        for(int i= 0; i < columns.length; i++) {
            rs.put(String.valueOf(i), columns[i]);
        }
        step();
        return rs;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        this.bufferedReader.close();
    }

    private void step() {
        try {
            current = bufferedReader.readLine();
        }
        catch (IOException e) {
            current = null;
        }
    }
}
