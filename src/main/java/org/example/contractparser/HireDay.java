package org.example.contractparser;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

import static org.example.contractparser.ContractService.DATE_FORMAT;

public class HireDay {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    
    public static String getHireDay(){
        LocalDate localDate = LocalDate.now();
        int increment = 1;
        if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
            increment += 2;
        }

        return LocalDate.now().plusDays(increment).format(formatter);
    }
}