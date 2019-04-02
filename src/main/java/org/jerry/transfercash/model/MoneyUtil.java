package org.jerry.transfercash.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.apache.log4j.Logger;

/**
 * Utilities class to operate on money
 */
public enum MoneyUtil {
	//singleton pattern
    INSTANCE;

    static Logger log = Logger.getLogger(MoneyUtil.class);

    //zero amount with scale 4 and financial rounding mode
    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);


    /**
     * @param inputCurrencyCode String Currency code to be validated
     * @return true if currency code is valid ISO code, false otherwise
     */
    public boolean validateCurrencyCode(String inputCurrencyCode) {
        try {
            Currency instance = Currency.getInstance(inputCurrencyCode);
            if(log.isDebugEnabled()){
                log.debug("Validate Currency Code: " + instance.getSymbol());
            }
            return instance.getCurrencyCode().equals(inputCurrencyCode);
        } catch (Exception e) {
            log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
        }
        return false;
    }

}
