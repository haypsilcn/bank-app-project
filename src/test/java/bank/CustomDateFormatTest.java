package bank;

import bank.exceptions.customDateFormat.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

class CustomDateFormatTest {

    @ParameterizedTest
    @CsvSource({"31, 1, 1990", "28, 2, 2000", "30, 6, 1997"})
    public void validDateFormat(int day, int month, int year) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(day, month, year)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 200, 1979, 1000})
    public void invalidYearFormat(int year) {
        Exception e = assertThrows(YearFormatInvalidException.class,
                () -> new CustomDateFormat(3, 4, year)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1980, 2000, 1999, 2050})
    public void validYearFormat(int year) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(3, 4, year)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -2, 13})
    public void invalidMonthFormat(int month) {
        Exception e = assertThrows(MonthFormatInvalidException.class,
                () -> new CustomDateFormat(3, month, 1990)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    public void validMonthFormat(int month) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(3, month, 2000)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 29, 30, 31})
    public void invalidDayFebruaryFormat(int day) {
        Exception e = assertThrows(DayFormatInvalidException.class,
                () -> new CustomDateFormat(day, 2, 1990)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 28, 20, 15})
    public void validDayFebruaryFormat(int day) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(day, 2, 1990)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    public void invalidDayZeroFormat(int month) {
        Exception e = assertThrows(DayFormatInvalidException.class,
                () -> new CustomDateFormat(0, month, 1990)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    public void invalidDay32Format(int month) {
        Exception e = assertThrows(DayFormatInvalidException.class,
                () -> new CustomDateFormat(32, month, 1990)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    public void validDayFirstFormat(int month) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(1, month, 1990)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 6, 9, 11})
    public void invalidDay31thFormat(int month) {
        Exception e = assertThrows(DayFormatInvalidException.class,
                () -> new CustomDateFormat(31, month, 1990)
        );
        System.out.println(e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 8, 10, 12})
    public void validDay31stFormat(int month) {
        assertDoesNotThrow(
                () -> new CustomDateFormat(31, month, 1990)
        );
    }
}