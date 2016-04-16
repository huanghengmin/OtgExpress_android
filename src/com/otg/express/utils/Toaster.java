package com.otg.express.utils;

import android.content.Context;
import android.widget.Toast;

public final class Toaster {
  public static void show(Context paramContext, int paramInt)
  {
    if (paramContext == null) {
      return;
    }
    show(paramContext, paramContext.getApplicationContext().getString(paramInt), 0);
  }
  
  public static void show(Context paramContext, String paramString)
  {
    show(paramContext, paramString, 0);
  }
  
  public static void show(Context paramContext, String paramString, int paramInt) {
    if (paramContext == null) {
      return;
    }
    Toast.makeText(paramContext.getApplicationContext(), paramString, paramInt).show();
  }
  
  public static void showLong(Context paramContext, int paramInt) {
    if (paramContext == null) {
      return;
    }
    show(paramContext, paramContext.getApplicationContext().getString(paramInt), 1);
  }
  
  public static void showLong(Context paramContext, String paramString)
  {
    show(paramContext, paramString, 1);
  }
}



