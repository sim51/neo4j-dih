package org.neo4j.dih.datasource.csv;

import org.neo4j.dih.datasource.AbstractResult;
import org.neo4j.dih.exception.DIHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Result for a CSVDataSource.
 */
public class CSVResult extends AbstractResult {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(CSVResult.class);

    /**
     * Inputstream of the CSV file.
     */
    private InputStream inputStream;

    /**
     * Buffer reader of the CSV file.
     */
    private BufferedReader bufferedReader;

    /**
     * CSV separator.
     */
    private String separator;

    /**
     * CSV Encoding.
     */
    private String encoding;

    /**
     * The current row cursor.
     */
    private String current;

    /**
     * Constructor.
     *
     * @param url       Url of the CSV file.
     * @param encoding  Encoding of the CSV file.
     * @param separator Separator of the CSV file.
     * @throws DIHException
     */
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
    public List<Object> next() {
        List<Object> rs = new ArrayList<>();

        String[] columns = current.split(separator);
        for (int i = 0; i < columns.length; i++) {
            rs.add(columns[i]);
        }
        step();
        return rs;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        this.bufferedReader.close();
    }

    /**
     * Doing a step forward into the result list.
     */
    private void step() {
        try {
            current = bufferedReader.readLine();
        } catch (IOException e) {
            current = null;
        }
    }
}
