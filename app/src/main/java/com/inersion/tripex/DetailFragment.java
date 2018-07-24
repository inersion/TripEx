// DetailFragment.java
// Fragment subclass that displays one contact's details
package com.inersion.tripex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inersion.tripex.sampledata.DatabaseDescription.Trip;

public class DetailFragment extends Fragment
   implements LoaderManager.LoaderCallbacks<Cursor> {

   // callback methods implemented by MainActivity
   public interface DetailFragmentListener {
      void onTripDeleted(); // called when a trip is deleted

      // pass Uri of trip to edit to the DetailFragmentListener
      void onEditTrip(Uri tripUri);
   }

   private static final int TRIP_LOADER = 0; // identifies the Loader

   private DetailFragmentListener listener; // MainActivity
   private Uri tripURI; // Uri of selected trip

   private TextView nameTextView; // displays trip's name
   private TextView fromTextView; // displays trip's origin
   private TextView toTextView; // displays trip's destination
   private TextView departTextView; // displays trip's departure date
   private TextView returnTextView; // displays trip's return date
   private TextView airfareTextView; // displays trip's airfare cost
   private TextView hotelTextView; // displays trip's hotel cost
   private TextView rentalTextView; // displays trip's rental cost
   private TextView totalcostTextView; // displays trip's total cost


   // set DetailFragmentListener when fragment attached
   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      listener = (DetailFragmentListener) context;
   }

   // remove DetailFragmentListener when fragment detached
   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }

   // called when DetailFragmentListener's view needs to be created
   @Override
   public View onCreateView(
           LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      setHasOptionsMenu(true); // this fragment has menu items to display

      // get Bundle of arguments then extract the trip's Uri
      Bundle arguments = getArguments();

      if (arguments != null)
         tripURI = arguments.getParcelable(MainActivity.TRIP_URI);

      // inflate DetailFragment's layout
      View view =
              inflater.inflate(R.layout.fragment_detail, container, false);

      // get the EditTexts
      nameTextView = (TextView) view.findViewById(R.id.nameTextView);
      fromTextView = (TextView) view.findViewById(R.id.fromTextView);
      toTextView = (TextView) view.findViewById(R.id.toTextView);
      departTextView = (TextView) view.findViewById(R.id.departTextView);
      returnTextView = (TextView) view.findViewById(R.id.returnTextView);
      airfareTextView = (TextView) view.findViewById(R.id.airfareTextView);
      hotelTextView = (TextView) view.findViewById(R.id.hotelTextView);
      rentalTextView = (TextView) view.findViewById(R.id.rentalTextView);
      totalcostTextView = (TextView) view.findViewById(R.id.totalcostTextView);

      // load the trip
      getLoaderManager().initLoader(TRIP_LOADER, null, this);
      return view;
   }

   // display this fragment's menu items
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_details_menu, menu);
   }

   // handle menu item selections
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_edit:
            listener.onEditTrip(tripURI); // pass Uri to listener
            return true;
         case R.id.action_delete:
            deleteTrip();
            return true;
      }

      return super.onOptionsItemSelected(item);
   }

   // delete a trip
   private void deleteTrip() {
      // use FragmentManager to display the confirmDelete DialogFragment
      confirmDelete.show(getFragmentManager(), "confirm delete");
   }

   // DialogFragment to confirm deletion of trip
   private final DialogFragment confirmDelete =
           new DialogFragment() {
              // create an AlertDialog and return it
              @Override
              public Dialog onCreateDialog(Bundle bundle) {
                 // create a new AlertDialog Builder
                 AlertDialog.Builder builder =
                         new AlertDialog.Builder(getActivity());

                 builder.setTitle(R.string.confirm_title);
                 builder.setMessage(R.string.confirm_message);

                 // provide an OK button that simply dismisses the dialog
                 builder.setPositiveButton(R.string.button_delete,
                         new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int button) {

                               // use Activity's ContentResolver to invoke
                               // delete on the TripExContentProvider
                               getActivity().getContentResolver().delete(
                                       tripURI, null, null);
                               listener.onTripDeleted(); // notify listener
                            }
                         }
                 );

                 builder.setNegativeButton(R.string.button_cancel, null);
                 return builder.create(); // return the AlertDialog
              }
           };

   // called by LoaderManager to create a Loader
   @Override
   public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      // create an appropriate CursorLoader based on the id argument;
      // only one Loader in this fragment, so the switch is unnecessary
      CursorLoader cursorLoader;

      switch (id) {
         case TRIP_LOADER:
            cursorLoader = new CursorLoader(getActivity(),
                    tripURI, // Uri of trip to display
                    null, // null projection returns all columns
                    null, // null selection returns all rows
                    null, // no selection arguments
                    null); // sort order
            break;
         default:
            cursorLoader = null;
            break;
      }

      return cursorLoader;
   }

   // called by LoaderManager when loading completes
   @Override
   public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      // if the trip exists in the database, display its data
      if (data != null && data.moveToFirst()) {
         // get the column index for each data item

         int nameIndex = data.getColumnIndex(Trip.COLUMN_NAME);
         int fromIndex = data.getColumnIndex(Trip.COLUMN_FROM);
         int toIndex = data.getColumnIndex(Trip.COLUMN_TO);
         int departIndex = data.getColumnIndex(Trip.COLUMN_DEPART);
         int returnIndex = data.getColumnIndex(Trip.COLUMN_RETURN);
         int airfareIndex = data.getColumnIndex(Trip.COLUMN_AIRFARE);
         int hotelIndex = data.getColumnIndex(Trip.COLUMN_HOTEL);
         int rentalIndex = data.getColumnIndex(Trip.COLUMN_RENTAL);


         // fill TextViews with the retrieved data
         nameTextView.setText(data.getString(nameIndex));
         fromTextView.setText(data.getString(fromIndex));
         toTextView.setText(data.getString(toIndex));
         departTextView.setText(data.getString(departIndex));
         returnTextView.setText(data.getString(returnIndex));
         airfareTextView.setText("(USD)$" + data.getString(airfareIndex));
         hotelTextView.setText("(USD)$" + data.getString(hotelIndex));
         rentalTextView.setText("(USD)$" + data.getString(rentalIndex));

         //parse the cost elements if not null
         double airfare = 0.0;
         double hotel = 0.0;
         double rental = 0.0;
         double total_cost = 0.0;

         if (!(data.getString(airfareIndex).isEmpty())){
         airfare = Double.parseDouble((data.getString(airfareIndex)));}

         if (!(data.getString(hotelIndex).isEmpty())){
         hotel = Double.parseDouble((data.getString(hotelIndex)));}

         if (!(data.getString(rentalIndex)).isEmpty()){
         rental = Double.parseDouble((data.getString(rentalIndex)));}

         //calculate total cost of trip
         total_cost = airfare + hotel + rental;

         //display total cost of trip
         totalcostTextView.setText("(USD)$" + total_cost);
      }
   }

   // called by LoaderManager when the Loader is being reset
   @Override
   public void onLoaderReset(Loader<Cursor> loader) {
   }
}