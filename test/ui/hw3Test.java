package ui;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Tzu-Chi Kuo on 2017/3/2.
 * Purpose:
 *  - test UI and query engine
 */
public class hw3Test {
    @Test
    public void startQueryEngine() throws Exception {
        hw3 queryEngine = new hw3();
        queryEngine.startQueryEngine();
    }

}