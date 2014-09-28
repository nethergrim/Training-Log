package com.nethergrim.combogymdiary.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.image.SmartImageView;
import com.nethergrim.combogymdiary.Constants;
import com.nethergrim.combogymdiary.DB;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.DayOfWeek;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.view.FAB;
import com.viewpagerindicator.CirclePageIndicator;


import java.util.ArrayList;
import java.util.List;

public class CreateTrainingDayFragment extends Fragment implements View.OnClickListener {

    private static final String NEW_ID = "id";
    private Long id;
    private OnFragmentInteractionListener mListener;
    private Spinner spinner;
    private ViewPager viewPager;
    private FAB fabSave;
    private CirclePageIndicator pageIndicator;


    public static CreateTrainingDayFragment newInstance(Long param1) {
        CreateTrainingDayFragment fragment = new CreateTrainingDayFragment();
        Bundle args = new Bundle();
        args.putLong(NEW_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(NEW_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_training_day, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        DB db = new DB(getActivity());
        TrainingDay trainingDay = db.getTrainingDay(id);
        trainingDay.setDayOfWeek(DayOfWeek.getByCode(spinner.getSelectedItemPosition() + 1));
        trainingDay.setImageUrl(Constants.partsOfBodyURLs.get(viewPager.getCurrentItem()));
        db.updateTrainingDay(trainingDay);
        mListener.onTrainingDaySaved(id);
    }

    public interface OnFragmentInteractionListener {
        public void onTrainingDaySaved(Long id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (Spinner) view.findViewById(R.id.day_of_week_spinner);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        fabSave = (FAB) view.findViewById(R.id.fabSave);
        fabSave.setOnClickListener(this);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        initSpinner();
        initPager();
    }

    private void initPager() {
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
        pageIndicator.setCentered(true);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setStrokeColor(getResources().getColor(R.color.material_blue_a400));
        pageIndicator.setFillColor(getResources().getColor(R.color.material_blue_a400));
    }

    private void initSpinner() {
        List<String> daysOfWeek = new ArrayList<String>();
        for (int i = 1; i < 8; i++){
            daysOfWeek.add(DayOfWeek.getByCode(i).getName(getActivity()));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, daysOfWeek);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SpinnerFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return Constants.partsOfBodyURLs.size();
        }
    }

    public static class SpinnerFragment extends Fragment{

        public static final String BUNDLE_POSITION = "position";
        private int position = 0;

        public static SpinnerFragment getInstance(int position){
            SpinnerFragment spinnerFragment = new SpinnerFragment();
            Bundle args = new Bundle();
            args.putInt(BUNDLE_POSITION, position);
            spinnerFragment.setArguments(args);
            return spinnerFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null){
                position = getArguments().getInt(BUNDLE_POSITION, 0);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_spinner, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SmartImageView smartImageView = (SmartImageView) view.findViewById(R.id.image);
            smartImageView.setImageUrl(Constants.partsOfBodyURLs.get(position));
        }
    }
}
