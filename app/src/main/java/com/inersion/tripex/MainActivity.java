// MainActivity.java
// Parthiv Shah
// A Trip Application to add/view/delete custom trips
// Hosts the app's fragments and handles communication between them

package com.inersion.tripex;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity
        implements tripexFragment.TripsFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    // key for storing a trip's Uri in a Bundle passed to a fragment
    public static final String TRIP_URI = "trip_uri";

    private tripexFragment tripsFragment; // displays trips list

    // display tripexFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Track your trip expenses!");

        // if layout contains fragmentContainer, the phone layout is in use;
        // create and display a tripexFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create tripexFragment
            tripsFragment = new tripexFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, tripsFragment);
            transaction.commit(); // display tripexFragment
        }
        else {
            tripsFragment =
                    (tripexFragment) getSupportFragmentManager().
                            findFragmentById(R.id.tripsFragment);
        }
    }


    // display DetailFragment for selected trip
    @Override
    public void onTripSelected(Uri tripUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayTrip(tripUri, R.id.fragmentContainer);
        else { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            displayTrip(tripUri, R.id.rightPaneContainer);
        }
    }

    // display AddEditFragment to add a new trip
    @Override
    public void onAddTrip() {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // display a trip
    private void displayTrip(Uri tripUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify trip's Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(TRIP_URI, tripUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }

    // display fragment for adding a new or editing an existing trip
    private void displayAddEditFragment(int viewID, Uri tripUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing trip, provide tripUri as an argument
        if (tripUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(TRIP_URI, tripUri);
            addEditFragment.setArguments(arguments);
        }

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // return to trip list when displayed trip deleted
    @Override
    public void onTripDeleted() {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        tripsFragment.updateTripList(); // refresh trips
    }

    // display the AddEditFragment to edit an existing trip
    @Override
    public void onEditTrip(Uri tripUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, tripUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, tripUri);
    }

    // update GUI after new trip or updated trip saved
    @Override
    public void onAddEditCompleted(Uri tripUri) {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        tripsFragment.updateTripList(); // refresh trips

        if (findViewById(R.id.fragmentContainer) == null) { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            // on tablet, display trip that was just added or edited
            displayTrip(tripUri, R.id.rightPaneContainer);
        }
    }
}
