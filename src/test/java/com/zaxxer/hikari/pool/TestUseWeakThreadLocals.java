package com.zaxxer.hikari.pool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestUseWeakThreadLocals {

   @Test
   public void useWeakThreadLocals() {
      System.out.println(useWeakThreadLocals0());
   }

   private boolean useWeakThreadLocals0()
   {
      try {
         if (System.getProperty("com.zaxxer.hikari.useWeakReferences") != null) {   // undocumented manual override of WeakReference behavior
            return Boolean.getBoolean("com.zaxxer.hikari.useWeakReferences");
         }

         return getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
      }
      catch (SecurityException se) {
         return true;
      }
   }

}
