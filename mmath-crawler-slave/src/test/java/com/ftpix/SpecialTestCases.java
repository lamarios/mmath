package com.ftpix;

import com.ftpix.mmath.DaoConfiguration;
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


public class SpecialTestCases {

    @Test
    public void testSpecialCases() throws IOException, ParseException, SherdogParserException {
        Sherdog sherdog = new Sherdog.Builder().build();

        MmathFighter fighter = MmathFighter.fromSherdong(sherdog.getFighter("https://www.sherdog.com/fighter/Alistair-Overeem-461"));
        System.out.println(fighter);


    }

}
