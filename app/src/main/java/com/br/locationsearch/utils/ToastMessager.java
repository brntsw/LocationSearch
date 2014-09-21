package com.br.locationsearch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by BRUNO on 19/09/2014.
 */
public class ToastMessager {

    public void showMessage(Context ctx, String message, int period){
        switch(period){
            case 0:
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            break;
            case 1:
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
            break;
        }
    }

}
