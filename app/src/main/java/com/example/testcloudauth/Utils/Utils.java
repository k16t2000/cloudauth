package com.example.testcloudauth.Utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public Utils() {
    }

    /**
     * customizable toast
     */
    public void toastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
