package com.mycompany.chservicetime.presentation.addedittimeslot;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.di.component.AddEditTimeSlotComponent;
import com.mycompany.chservicetime.di.component.DaggerAddEditTimeSlotComponent;
import com.mycompany.chservicetime.di.module.AddEditTimeSlotPresenterModule;
import com.mycompany.chservicetime.model.TimeSlot;

import net.grandcentrix.thirtyinch.TiFragment;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditTimeSlotFragment extends TiFragment<AddEditTimeSlotPresenter, AddEditTimeSlotView>
        implements AddEditTimeSlotView {

    public static final String ARGUMENT_EDIT_TIME_SLOT_ID = "EDIT_TIME_SLOT_ID";

    private String mId;

    // Temporary variable to store value.
    boolean activationFlag;

    private char[] days = "0000000".toCharArray();

    private EditText nameEditText;
    private TimePicker beginTimeTP;
    private TimePicker endTimeTP;
    private CheckBox repeatFlagCheckBox;
    private ToggleButton day0ToggleButton;
    private ToggleButton day1ToggleButton;
    private ToggleButton day2ToggleButton;
    private ToggleButton day3ToggleButton;
    private ToggleButton day4ToggleButton;
    private ToggleButton day5ToggleButton;
    private ToggleButton day6ToggleButton;
    private RadioButton muteRB;
    private RadioButton vibrateRB;
    private RadioGroup serviceOptionRG;

    public AddEditTimeSlotFragment() {
        // Required empty public constructor
    }

    /**
     * @param editedTimeSlotId If value is NULL, then create a fragment which has not
     *                         ARGUMENT_EDIT_TIME_SLOT_ID parameter. It means this fragment is for adding a
     *                         new TimeSlot.
     * @return
     */
    public static AddEditTimeSlotFragment newInstance(String editedTimeSlotId) {
        AddEditTimeSlotFragment fragment = new AddEditTimeSlotFragment();
        if (editedTimeSlotId != null) {
            Bundle bundle = new Bundle();
            bundle.putString(AddEditTimeSlotFragment.ARGUMENT_EDIT_TIME_SLOT_ID, editedTimeSlotId);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @NonNull
    @Override
    public AddEditTimeSlotPresenter providePresenter() {
        /*
        * Inject the AddEditTimeSlotFragment instance to Dagger Graph,
        * then the injected fields can be instanced, in this case, create the presenter.
        */
        AddEditTimeSlotComponent addEditTimeSlotComponent = DaggerAddEditTimeSlotComponent.builder()
                .appRepositoryComponent(CHApplication.INSTANCE.getAppRepositoryComponent())
                .addEditTimeSlotPresenterModule(new AddEditTimeSlotPresenterModule(mId))
                .build();

        return addEditTimeSlotComponent.getAddEditTimeSlotPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Because providePresenter() uses this argument, the sentence must be before super.onCreate()
        setTimeSlotIdIfAny();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_add_edit_time_slot, container, false);

        initViews(root);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_edit_time_slot, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.time_slot_save: {
                saveTimeSlot();
                return true;
            }
            default:
                return false;
        }
    }

    ////// Contract implements //////

    @Override
    public void showMessage(int messageType) {
        int stringResId = R.string.time_slot_unknown_error;
        switch (messageType) {
            case AddEditTimeSlotPresenter.MESSAGE_TYPE_INPUT_NAME_ERROR:
                stringResId = R.string.input_name_error;
                break;
            case AddEditTimeSlotPresenter.MESSAGE_TYPE_INPUT_TIME_ERROR:
                stringResId = R.string.input_time_error;
                break;
            case AddEditTimeSlotPresenter.MESSAGE_TYPE_INPUT_DAY_ERROR:
                stringResId = R.string.input_day_error;
                break;
            case AddEditTimeSlotPresenter.MESSAGE_TYPE_FAIL_GET_TIME_SLOT_ERROR:
                stringResId = R.string.fail_get_time_slot_error;
                break;
            case AddEditTimeSlotPresenter.MESSAGE_TYPE_FAIL_SAVE_TIME_SLOT_ERROR:
                stringResId = R.string.fail_save_time_slot_error;
                break;
        }
        Snackbar.make(nameEditText, getString(stringResId), Snackbar.LENGTH_LONG).show();
    }

    /**
     * @param timeSlot If timeSlot is NULL, then show a new TimeSlot form.
     */
    @Override
    public void setTimeSlotFields(TimeSlot timeSlot) {
        if (timeSlot == null) {
            nameEditText.setText("");
            // fix android bug in 4.1
            int currentHourIn24 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            beginTimeTP.setCurrentHour(currentHourIn24);
            endTimeTP.setCurrentHour(currentHourIn24);
            // default is vibrate
            mapServiceOption(TimeSlot.ServiceOption.VIBRATION);
        } else {
            nameEditText.setText(timeSlot.name());
            beginTimeTP.setCurrentHour(timeSlot.begin_time_hour());
            beginTimeTP.setCurrentMinute(timeSlot.begin_time_minute());
            endTimeTP.setCurrentHour(timeSlot.end_time_hour());
            endTimeTP.setCurrentMinute(timeSlot.end_time_minute());
            days = timeSlot.days().toCharArray();
            day0ToggleButton.setChecked(days[0] == '1');
            day1ToggleButton.setChecked(days[1] == '1');
            day2ToggleButton.setChecked(days[2] == '1');
            day3ToggleButton.setChecked(days[3] == '1');
            day4ToggleButton.setChecked(days[4] == '1');
            day5ToggleButton.setChecked(days[5] == '1');
            day6ToggleButton.setChecked(days[6] == '1');
            repeatFlagCheckBox.setChecked(timeSlot.repeat_flag());
            mapServiceOption(timeSlot.service_option());

            activationFlag = timeSlot.activation_flag();
        }
    }

    private void mapServiceOption(TimeSlot.ServiceOption serviceOption) {
        if (serviceOption.equals(TimeSlot.ServiceOption.MUTE)) {
            muteRB.setChecked(true);
            vibrateRB.setChecked(false);
        } else if (serviceOption.equals(TimeSlot.ServiceOption.VIBRATION)) {
            muteRB.setChecked(false);
            vibrateRB.setChecked(true);
        }
    }

    private TimeSlot.ServiceOption mapServiceOption(int selectedId) {
        if (selectedId == muteRB.getId())
            return TimeSlot.ServiceOption.MUTE;
        else
            return TimeSlot.ServiceOption.VIBRATION;
    }

    @Override
    public void showTimeSlotList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void setTimeSlotIdIfAny() {
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_EDIT_TIME_SLOT_ID)) {
            mId = getArguments().getString(ARGUMENT_EDIT_TIME_SLOT_ID);
        }
    }

    private void initViews(View rootView) {
        nameEditText = (EditText) rootView.findViewById(R.id.timeSlotNameEditText);
        beginTimeTP = (TimePicker) rootView.findViewById(R.id.beginTimePicker);
        endTimeTP = (TimePicker) rootView.findViewById(R.id.endTimePicker);
        beginTimeTP.setIs24HourView(true);
        endTimeTP.setIs24HourView(true);
        day0ToggleButton = (ToggleButton) rootView.findViewById(R.id.day0InWeekToggleButton);
        day1ToggleButton = (ToggleButton) rootView.findViewById(R.id.day1InWeekToggleButton);
        day2ToggleButton = (ToggleButton) rootView.findViewById(R.id.day2InWeekToggleButton);
        day3ToggleButton = (ToggleButton) rootView.findViewById(R.id.day3InWeekToggleButton);
        day4ToggleButton = (ToggleButton) rootView.findViewById(R.id.day4InWeekToggleButton);
        day5ToggleButton = (ToggleButton) rootView.findViewById(R.id.day5InWeekToggleButton);
        day6ToggleButton = (ToggleButton) rootView.findViewById(R.id.day6InWeekToggleButton);
        repeatFlagCheckBox = (CheckBox) rootView.findViewById(R.id.repeatWeeklyCheckBox);
        serviceOptionRG = (RadioGroup) rootView.findViewById(R.id.serviceOptionRadioGroup);
        muteRB = (RadioButton) rootView.findViewById(R.id.radio_mute);
        vibrateRB = (RadioButton) rootView.findViewById(R.id.radio_vibrate);

        day0ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(0));
        day1ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(1));
        day2ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(2));
        day3ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(3));
        day4ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(4));
        day5ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(5));
        day6ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(6));
    }

    private void saveTimeSlot() {
        getPresenter().saveTimeSlot(nameEditText.getText().toString(),
                "", // TODO: description field.
                beginTimeTP.getCurrentHour(), beginTimeTP.getCurrentMinute(),
                endTimeTP.getCurrentHour(), endTimeTP.getCurrentMinute(), String.copyValueOf(days),
                repeatFlagCheckBox.isChecked(),
                mapServiceOption(serviceOptionRG.getCheckedRadioButtonId()), activationFlag);
    }

    /**
     * Listen the change of ToggleButton of days.
     */
    class daysOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int currentDayIndex;

        daysOnCheckedChangeListener(int dayIndex) {
            currentDayIndex = dayIndex;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                days[currentDayIndex] = '1';
            } else {
                days[currentDayIndex] = '0';
            }

        }
    }
}
