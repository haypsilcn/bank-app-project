package bank;

import bank.exceptions.customDateFormat.DayFormatInvalidException;
import bank.exceptions.customDateFormat.MonthFormatInvalidException;
import bank.exceptions.customDateFormat.YearFormatInvalidException;

/**
 * custom class of date format for transaction
 */
public class CustomDateFormat {

    private final int day;
    private final int month;
    private final int year;

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public CustomDateFormat(int newDay, int newMonth, int newYear) throws DayFormatInvalidException, MonthFormatInvalidException, YearFormatInvalidException {

        if (newYear < 1980)
            throw new YearFormatInvalidException("=> ERROR! YEAR CANNOT BE AFTER 1980!");
        else {
            this.year = newYear;
            if (newMonth < 1 || newMonth > 12)
                throw new MonthFormatInvalidException("=> ERROR! MONTH FORMAT IS NOT CORRECT!");
            else {
                this.month = newMonth;
                if (newDay < 1)
                    throw new DayFormatInvalidException("=> ERROR! DAY FORMAT IS NOT CORRECT!");
                else {
                    if (newMonth == 4 || newMonth == 6 || newMonth == 9 || newMonth == 11) {
                        if (newDay <= 30)
                            this.day = newDay;
                        else
                            throw new DayFormatInvalidException("=> ERROR! DAY FORMAT IS NOT CORRECT!");
                    }
                    else if (newMonth == 2) {
                        if (newDay <= 28)
                            this.day = newDay;
                        else
                            throw new DayFormatInvalidException("=> ERROR! DAY FORMAT IS NOT CORRECT!");
                    } else {
                        if (newDay <= 31)
                            this.day = newDay;
                        else
                            throw new DayFormatInvalidException("=> ERROR! DAY FORMAT IS NOT CORRECT!");
                    }
                }
            }

        }
    }

    @Override
    public String toString() {
        return day + "-" + month + "-" + year;
    }
}
