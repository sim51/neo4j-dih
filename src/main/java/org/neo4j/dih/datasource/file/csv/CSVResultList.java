package org.neo4j.dih.datasource.file.csv;

import au.com.bytecode.opencsv.CSVReader;
import org.neo4j.dih.datasource.AbstractResultList;
import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResultSet for {@link org.neo4j.dih.datasource.file.csv.CSVDataSource} object.
 *
 * @author bsimard
 * @version $Id: $Id
 */
public class CSVResultList extends AbstractResultList {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(CSVResultList.class);

    /**
     * Stream of the CSV file.
     */
    private InputStream stream;

    /**
     * Buffer reader of the CSV file.
     */
    private BufferedReader bufferedReader;

    /**
     * CSVreader.
     */
    private CSVReader csvReader;

    /**
     * CSV Encoding.
     */
    private String encoding;

    /**
     * With CSV headers.
     */
    private Boolean withHeaders = Boolean.FALSE;

    /**
     * CSV header columns.
     */
    private List<String> headers;

    /**
     * The current row cursor.
     */
    private String[] current;

    /**
     * Default constructor.
     *
     * @param url        Url of the CSV file
     * @param timeout    Timeout
     * @param encoding   Encoding of the CSV file.
     * @param separator  Separator of the CSV file.
     * @param withHeaders Is there a header in CSV file
     * @throws org.neo4j.dih.exception.DIHException if any.
     */
    public CSVResultList(String url, BigInteger timeout, String encoding, String separator, Boolean withHeaders) throws DIHException {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(timeout.intValue());
            this.stream = connection.getInputStream();
            this.encoding = encoding;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.stream));
            this.csvReader = new CSVReader(bufferedReader, separator.charAt(0));
            this.withHeaders = withHeaders;

            step();

            if(withHeaders) {
                readHeaders();
            }
        } catch (IOException e) {
            throw new DIHException(e);
        }
    }

    /**
     * Read CSV Header.
     */
    private void readHeaders() throws IOException {
        headers = new ArrayList<>();
        for (int i = 0; i < current.length; i++) {
            headers.add(i, current[i]);
        }
        step();
    }

    /**
     * Doing a step forward into the result list.
     */
    private void step() {
        try {
            current = csvReader.readNext();
        } catch (IOException e) {
            current = null;
            throw new DIHRuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return current != null;
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, String> next() {
        Map<Object, String> rs = new HashMap();

        for (int i = 0; i < current.length; i++) {
            if(withHeaders) {
                String name = headers.get(i);
                rs.put(name, current[i]);
            }
            else {
                rs.put(i, current[i]);
            }
        }
        step();
        return rs;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        this.stream.close();
        this.bufferedReader.close();
        this.csvReader.close();
    }

}
