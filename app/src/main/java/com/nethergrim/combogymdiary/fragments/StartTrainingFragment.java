package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BaseActivity;
import com.nethergrim.combogymdiary.activities.CreatingTrainingDayActivity;
import com.nethergrim.combogymdiary.dialogs.DialogGoToMarket;
import com.nethergrim.combogymdiary.view.FAB;
import com.shamanland.fab.ShowHideOnScroll;

public class StartTrainingFragment extends Fragment implements
        LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 3;
    private static final int CM_EDIT_ID = 4;
    private ListView lvMain;
    private DB db;
    private SimpleCursorAdapter scAdapter;
    private OnSelectedListener mCallback;
    private int LOADER_ID = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        db = new DB(getActivity());
        db.open();
        String[] from = new String[]{DB.TRA_NAME};
        int[] to = new int[]{R.id.tvText};
        scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.my_list_item, null, from, to, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(lvMain);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_training, null);
        lvMain = (ListView) v.findViewById(R.id.lvStartTraining);
        getActivity().getActionBar().setTitle(R.string.startTrainingButtonString);
        lvMain.setAdapter(scAdapter);
        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goToTraining((int) id);
            }
        });
        ((FragmentActivity) getActivity()).getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        FAB fabAdd = (FAB) v.findViewById(R.id.fabAddTrainings);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoAddingProgramActivity = new Intent(getActivity(), CreatingTrainingDayActivity.class);
                startActivity(gotoAddingProgramActivity);
            }
        });
        lvMain.setOnTouchListener(new ShowHideOnScroll(fabAdd));
        return v;
    }

    public void onResume() {
        super.onResume();
        ((FragmentActivity) getActivity()).getSupportLoaderManager()
                .getLoader(LOADER_ID).forceLoad();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp.contains(BaseActivity.TRAININGS_DONE_NUM)
                && sp.getInt(BaseActivity.TRAININGS_DONE_NUM, 0) > 5
                && !sp.contains(BaseActivity.MARKET_LEAVED_FEEDBACK)) {
            DialogFragment dialog = new DialogGoToMarket();
            dialog.show(getActivity().getFragmentManager(), "dialog_goto_market");
            dialog.setCancelable(false);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(1, CM_EDIT_ID, 0, R.string.edit);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == CM_DELETE_ID) {
            db.deleteTrainingProgram((int) acmi.id);
            ((FragmentActivity) getActivity()).getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
            Toast.makeText(getActivity(), getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == CM_EDIT_ID) {
            Intent intent = new Intent(getActivity(), CreatingTrainingDayActivity.class);
            intent.putExtra(CreatingTrainingDayActivity.BUNDLE_ID_KEY, (int) acmi.id);
            startActivity(intent);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void goToTraining(int id) {
        mCallback.onTrainingSelected(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(getActivity(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scAdapter.swapCursor(null);
    }

    public interface OnSelectedListener {
        public void onTrainingSelected(int id);
    }

    static class MyCursorLoader extends CursorLoader {
        DB db;
        Cursor cursor;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            cursor = db.getDataTrainings(null, null, null, null, null, null);
            return cursor;
        }
    }
}
