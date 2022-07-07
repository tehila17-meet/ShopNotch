package com.example.shopnotch.Utilities.ShoppingUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class CheckoutUtils {
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    private static final int MAXLEN = 5;

    public static String getRandomString()
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(MAXLEN);
        for(int i=0;i<MAXLEN;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String getOrderArrivalDate(){
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);  // number of days to add
        String tomorrow = (formattedDate.format(c.getTime()));
        return tomorrow;
    }
}
