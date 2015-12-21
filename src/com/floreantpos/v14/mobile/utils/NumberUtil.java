package com.floreantpos.v14.mobile.utils;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtil {
    private final static NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private final static NumberFormat currencyFormat = NumberFormat.getNumberInstance();

    static {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
    }

	/*public static double roundToTwoDigit(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}*/

    public static String formatNumber(Double number) {
        if(number == null) {
            return numberFormat.format(0);
        }

        String value = numberFormat.format(number);

        if(value.startsWith("-")) { //$NON-NLS-1$
            return numberFormat.format(0);
        }

        return value;
    }

    public static String formatToCurrency(Double number) {
        if(number == null) {
            return currencyFormat.format(0);
        }

        String value = currencyFormat.format(number);

        if(value.startsWith("-")) { //$NON-NLS-1$
            return currencyFormat.format(0);
        }

        return value;
    }

    public static Double parseNumber(String number) {

        Double result = 0.0;

        if (number == null || "".equals(number)) {
            result = 0.0;
        } else {


            try {
                result = (Double) currencyFormat.parse(number);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
