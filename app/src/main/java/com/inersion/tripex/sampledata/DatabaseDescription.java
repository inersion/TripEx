// DatabaseDescription.java
// Describes the table name and column names for this app's database,
// and other information required by the ContentProvider
package com.inersion.tripex.sampledata;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
   // ContentProvider's name: typically the package name
   public static final String AUTHORITY = "com.inersion.tripex.sampledata";

   // base URI used to interact with the ContentProvider
   private static final Uri BASE_CONTENT_URI =
      Uri.parse("content://" + AUTHORITY);

   // nested class defines contents of the trips table
   public static final class Trip implements BaseColumns {
      public static final String TABLE_NAME = "trips"; // table's name

      // Uri for the trips table
      public static final Uri CONTENT_URI =
         BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

      // column names for trips table's columns
      public static final String COLUMN_NAME = "name";
      public static final String COLUMN_FROM = "fromwhere";
      public static final String COLUMN_TO = "towhere";
      public static final String COLUMN_DEPART = "depart";
      public static final String COLUMN_RETURN = "return";
      public static final String COLUMN_AIRFARE = "airfare";
      public static final String COLUMN_HOTEL = "hotel";
      public static final String COLUMN_RENTAL = "rental";

      // creates a Uri for a specific trip
      public static Uri buildTripUri(long id) {
         return ContentUris.withAppendedId(CONTENT_URI, id);
      }
   }
}
