package com.ftpix;

import com.ftpix.mmath.DaoConfiguration;
import com.ftpix.mmath.dao.MySQLDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.ParseException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoConfiguration.class})
public class SpecialTestCases {
    @Autowired
    MySQLDao dao;

    @Test
    public void testSpecialCases() throws IOException, ParseException, SherdogParserException {
        Sherdog sherdog = new Sherdog.Builder().build();

        MmathFighter fighter = MmathFighter.fromSherdong(sherdog.getFighter("http://www.sherdog.com/fighter/Johil-de-Oliveira-6"));


    }

}
