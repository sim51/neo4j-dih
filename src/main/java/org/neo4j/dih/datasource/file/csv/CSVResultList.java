package org.neo4j.dih.datasource.file.csv;

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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Result for a CSVDataSource.
 */
public class CSVResultList extends AbstractResultList {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(CSVResultList.class);

    /**
     * Regex to split CSV row
     */
    private String csvRowRegex;

    /**
     * Stream of the CSV file.
     */
    private InputStream stream;

    /**
     * Buffer reader of the CSV file.
     */
    private BufferedReader bufferedReader;

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
     * @param url       Url of the CSV file
     * @param timeout   Timeout
     * @param encoding  Encoding of the CSV file.
     * @param separator Separator of the CSV file.
     * @throws DIHException
     */
    public CSVResultList(String url, BigInteger timeout, String encoding, String separator) throws DIHException {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(timeout.intValue());
            this.stream = connection.getInputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.stream));
            this.encoding = encoding;

            // Create regex for row parsing
            String otherThanQuote = " [^\\\"] ";
            String quotedString = String.format(" \" %s* \" ", otherThanQuote);
            this.csvRowRegex = String.format("(?x) " + // enable comments, ignore white spaces
                            "%s                 " + // match separator
                            "(?=               " + // start positive look ahead
                            "  (               " + //   start group 1
                            "    %s*           " + //     match 'otherThanQuote' zero or more times
                            "    %s            " + //     match 'quotedString'
                            "  )*              " + //   end group 1 and repeat it zero or more times
                            "  %s*             " + //   match 'otherThanQuote'
                            "  $               " + // match the end of the string
                            ")                 ",  // stop positive look ahead
                    separator, otherThanQuote, quotedString, otherThanQuote);

            step();
        } catch (IOException e) {
            throw new DIHException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public List<Object> next() {
        List<Object> rs = new ArrayList<>();

        String[] columns = current.split(csvRowRegex, -1);
        for (int i = 0; i < columns.length; i++) {
            rs.add(columns[i]);
        }
        step();
        return rs;
    }

    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
        this.stream.close();
    }

    /**
     * Doing a step forward into the result list.
     */
    private void step() {
        try {
            current = bufferedReader.readLine();
        } catch (IOException e) {
            current = null;
            throw new DIHRuntimeException(e);
        }
    }
}
