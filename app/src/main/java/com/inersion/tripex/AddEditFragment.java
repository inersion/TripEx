// AddEditFragment.java
// Fragment for adding a new trip or editing an existing one
package com.inersion.tripex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.inersion.tripex.sampledata.DatabaseDescription;

public class AddEditFragment extends Fragment
   implements LoaderManager.LoaderCallbacks<Cursor> {

   // defines callback method implemented by MainActivity
   public interface AddEditFragmentListener {
      // called when trip is saved
      void onAddEditCompleted(Uri tripUri);
   }

   // constant used to identify the Loader
   private static final int TRIP_LOADER = 0;

   private AddEditFragmentListener listener; // MainActivity
   private Uri tripUri; // Uri of selected trip
   private boolean addingNewTrip = true; // adding (true) or editing

   // EditTexts for trip information
   private TextInputLayout nameTextInputLayout;
   private TextInputLayout fromTextInputLayout;
   private TextInputLayout toTextInputLayout;
   private TextInputLayout departTextInputLayout;
   private TextInputLayout returnTextInputLayout;
   private TextInputLayout airfareTextInputLayout;
   private TextInputLayout hotelTextInputLayout;
   private TextInputLayout rentalTextInputLayout;

   private FloatingActionButton saveTripFAB;

   private CoordinatorLayout coordinatorLayout; // used with SnackBars

   // set AddEditFragmentListener when Fragment attached
   @Override
   public void onAttach(Context context) {
      super.onAttach(context);
      listener = (AddEditFragmentListener) context;
   }

   // remove AddEditFragmentListener when Fragment detached
   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }

   // called when Fragment's view needs to be created
   @Override
   public View onCreateView(
           LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      setHasOptionsMenu(true); // fragment has menu items to display

      // inflate GUI and get references to EditTexts
      View view =
         inflater.inflate(R.layout.fragment_add_edit, container, false);
      nameTextInputLayout =
         (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
      nameTextInputLayout.getEditText().addTextChangedListener(
         nameChangedListener);
      fromTextInputLayout =
         (TextInputLayout) view.findViewById(R.id.fromTextInputLayout);
      toTextInputLayout =
         (TextInputLayout) view.findViewById(R.id.toTextInputLayout);
      departTextInputLayout =
         (TextInputLayout) view.findViewById(R.id.departTextInputLayout);
      returnTextInputLayout =
              (TextInputLayout) view.findViewById(R.id.returnTextInputLayout);
      airfareTextInputLayout =
              (TextInputLayout) view.findViewById(R.id.airfareTextInputLayout);
      hotelTextInputLayout =
              (TextInputLayout) view.findViewById(R.id.hotelTextInputLayout);
      rentalTextInputLayout =
              (TextInputLayout) view.findViewById(R.id.rentalTextInputLayout);

      // set FloatingActionButton's event listener
      saveTripFAB = (FloatingActionButton) view.findViewById(
         R.id.saveFloatingActionButton);
      saveTripFAB.setOnClickListener(saveTripButtonClicked);
      updateSaveButtonFAB();

      // used to display SnackBars with brief messages
      coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
         R.id.coordinatorLayout);

      Bundle arguments = getArguments(); // null if creating new trip

      if (arguments != null) {
         addingNewTrip = false;
         tripUri = arguments.getParcelable(MainActivity.TRIP_URI);
      }

      // if editing an existing trip, create Loader to get the trip
      if (tripUri != null)
         getLoaderManager().initLoader(TRIP_LOADER, null, this);

      return view;
   }

   // detects when the text in the nameTextInputLayout's EditText changes
   // to hide or show saveButtonFAB
   private final TextWatcher nameChangedListener = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {}

      // called when the text in nameTextInputLayout changes
      @Override
      public void onTextChanged(CharSequence s, int start, int before,
                                int count) {
         updateSaveButtonFAB();
      }

      @Override
      public void afterTextChanged(Editable s) { }
   };

   // shows saveButtonFAB only if the name is not empty
   private void updateSaveButtonFAB() {
      String input =
         nameTextInputLayout.getEditText().getText().toString();

      // if there is a name for the trip, show the FloatingActionButton
      if (input.trim().length() != 0)
         saveTripFAB.show();
      else
         saveTripFAB.hide();
   }

   // responds to event generated when user saves a trip
   private final View.OnClickListener saveTripButtonClicked =
           new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                 // hide the virtual keyboard
                 ((InputMethodManager) getActivity().getSystemService(
                         Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                         getView().getWindowToken(), 0);
                 saveTrip(); // save trip to the database
              }
           };

   // saves trip information to the database
   private void saveTrip() {
      // create ContentValues object containing trip's key-value pairs
      ContentValues contentValues = new ContentValues();
      contentValues.put(DatabaseDescription.Trip.COLUMN_NAME,
              nameTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_FROM,
              fromTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_TO,
              toTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_DEPART,
              departTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_RETURN,
              returnTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_AIRFARE,
              airfareTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_HOTEL,
              hotelTextInputLayout.getEditText().getText().toString());
      contentValues.put(DatabaseDescription.Trip.COLUMN_RENTAL,
              rentalTextInputLayout.getEditText().getText().toString());


      if (addingNewTrip) {
         // use Activity's ContentResolver to invoke
         // insert on the TripExContentProvider
         Uri newTripUri = getActivity().getContentResolver().insert(
                 DatabaseDescription.Trip.CONTENT_URI, contentValues);

         if (newTripUri != null) {
            Snackbar.make(coordinatorLayout,
                    R.string.trip_added, Snackbar.LENGTH_LONG).show();
            listener.onAddEditCompleted(newTripUri);
         }
         else {
            Snackbar.make(coordinatorLayout,
                    R.string.trip_not_added, Snackbar.LENGTH_LONG).show();
         }
      }
      else {
         // use Activity's ContentResolver to invoke
         // insert on the TripExContentProvider
         int updatedRows = getActivity().getContentResolver().update(
                 tripUri, contentValues, null, null);

         if (updatedRows > 0) {
            listener.onAddEditCompleted(tripUri);
            Snackbar.make(coordinatorLayout,
                    R.string.trip_updated, Snackbar.LENGTH_LONG).show();
         }
         else {
            Snackbar.make(coordinatorLayout,
                    R.string.trip_not_updated, Snackbar.LENGTH_LONG).show();
         }
      }
   }

   // called by LoaderManager to create a Loader
   @Override
   public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      // create an appropriate CursorLoader based on the id argument;
      // only one Loader in this fragment, so the switch is unnecessary
      switch (id) {
         case TRIP_LOADER:
            return new CursorLoader(getActivity(),
               tripUri, // Uri of trip to display
               null, // null projection returns all columns
               null, // null selection returns all rows
               null, // no selection arguments
               null); // sort order
         default:
            return null;
      }
   }

   // called by LoaderManager when loading completes
   @Override
   public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      // if the trip exists in the database, display its data
      if (data != null && data.moveToFirst()) {
         // get the column index for each data item
         int nameIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_NAME);
         int fromIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_FROM);
         int toIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_TO);
         int departIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_DEPART);
         int returnIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_RETURN);
         int airfareIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_AIRFARE);
         int hotelIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_HOTEL);
         int rentalIndex = data.getColumnIndex(DatabaseDescription.Trip.COLUMN_RENTAL);


         // fill EditTexts with the retrieved data
         nameTextInputLayout.getEditText().setText(
            data.getString(nameIndex));
         fromTextInputLayout.getEditText().setText(
            data.getString(fromIndex));
         toTextInputLayout.getEditText().setText(
            data.getString(toIndex));
         departTextInputLayout.getEditText().setText(
            data.getString(departIndex));
         returnTextInputLayout.getEditText().setText(
                 data.getString(returnIndex));
         airfareTextInputLayout.getEditText().setText(
                 data.getString(airfareIndex));
         hotelTextInputLayout.getEditText().setText(
                 data.getString(hotelIndex));
         rentalTextInputLayout.getEditText().setText(
                 data.getString(rentalIndex));

         updateSaveButtonFAB();
      }
   }

   // called by LoaderManager when the Loader is being reset
   @Override
   public void onLoaderReset(Loader<Cursor> loader) { }
}
