package com.flippbidd.Bottoms;

import static com.flippbidd.Others.Constants.PRO_VERSION;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Others.Constants;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import butterknife.OnClick;

public class MeetingScheduleSheet extends BaseBottomSheetDialogFragment {

    DialogListener dailogListener;
    JsonObject editJsonData;
    CommonData mCommonDetailsData = null;
    String created_type = "user";

    RelativeLayout relativeAudioCall, relativeVideoCall, relativeGetAccessCall, relativeToday, relativeChooseDate;
    RelativeLayout relativeStartDate, relativeEndDate;
    MaterialTextView txtDateSelected, txtStartTime, txtEndTime;
    MaterialTextView txtPropertyPrice, txtPropertyMatters, txtPropertyBath, txtPropertyBad, txtPropertyAddress1, txtPropertyAddress2;
    AppCompatEditText editSpecialInstruction;
    private AppCompatImageView imagePropertyView;

    Calendar calendarSelected = Calendar.getInstance();

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatter1 = new SimpleDateFormat("MM-dd-yyyy");

    private String date = "", start_time = "", end_time = "", instructio = "", type = "";
    private int selectedS_H = 0, selectedS_M = 0;
    private String AUDIO_CALL = "Audio Call", VIDEO_CALL = "Video Call", GET_ACCESS = "Get Access";

    public static MeetingScheduleSheet newInstance() {
        return new MeetingScheduleSheet();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //find by id
        relativeAudioCall = view.findViewById(R.id.relativeAudioCall);
        relativeVideoCall = view.findViewById(R.id.relativeVideoCall);
        relativeGetAccessCall = view.findViewById(R.id.relativeGetAccessCall);

        relativeToday = view.findViewById(R.id.relativeToday);
        relativeChooseDate = view.findViewById(R.id.relativeChooseDate);

        relativeStartDate = view.findViewById(R.id.relativeStartDate);
        relativeEndDate = view.findViewById(R.id.relativeEndDate);

        txtDateSelected = view.findViewById(R.id.txtDateSelected);

        txtStartTime = view.findViewById(R.id.txtStartTime);
        txtEndTime = view.findViewById(R.id.txtEndTime);

        txtPropertyAddress1 = view.findViewById(R.id.txtPropertyAddress1);
        txtPropertyAddress2 = view.findViewById(R.id.txtPropertyAddress2);
        txtPropertyBad = view.findViewById(R.id.txtPropertyBad);
        txtPropertyBath = view.findViewById(R.id.txtPropertyBath);
        txtPropertyMatters = view.findViewById(R.id.txtPropertyMatters);
        txtPropertyPrice = view.findViewById(R.id.txtPropertyPrice);

        imagePropertyView = view.findViewById(R.id.imagePropertyView);
        editSpecialInstruction = view.findViewById(R.id.editSpecialInstruction);

        editSpecialInstruction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check
                if (containCheck(s)) {
                    CommonDialogView.getInstance().showCommonDialog(getActivity(), getActivity().getResources().getString(R.string.app_name),
                            "Exchanging contact information in the user chat goes against FlippBidd Networks Terms/Conditions"
                            , ""
                            , getActivity().getResources().getString(R.string.string_ok)
                            , false, false, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    editSpecialInstruction.getText().clear();
                                    editSpecialInstruction.setText("");
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });

                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                } else {
                }
            }
        });

        //set property data
        if (editJsonData != null) {

            txtPropertyAddress1.setText(editJsonData.get("address").getAsString());
            txtPropertyBad.setText(editJsonData.get("no_of_beds").getAsString() + " bd");
            txtPropertyBath.setText(editJsonData.get("no_of_baths").getAsString() + " ba");
            txtPropertyMatters.setText(editJsonData.get("propery_area").getAsString() + " Sqft");
            String price = editJsonData.get("property_price").getAsString();
//            if (editJsonData.get("property_price").getAsString() != null && !editJsonData.get("property_price").getAsString().equalsIgnoreCase("")) {
//                price = editJsonData.get("property_price").getAsString();
//            } else {
//                price = mCommonDetailsData.getRentAmount();
//            }
            txtPropertyPrice.setText("$" + currencyFormat(price));

            if (editJsonData.get("property_image").getAsString() != null && !editJsonData.get("property_image").getAsString().equalsIgnoreCase("")) {
                Glide.with(getActivity())
                        .load(editJsonData.get("property_image").getAsString())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image_icon).error(R.drawable.no_image_icon))
                        .into(imagePropertyView);
            } else {
                imagePropertyView.setBackgroundResource(R.drawable.no_image_icon);
            }

            //date check
            if (dateFormatter.format(calendarSelected.getTime()).equalsIgnoreCase(editJsonData.get("date").getAsString())) {
                //today
                date = dateFormatter.format(calendarSelected.getTime());
                updateDateChoose(relativeChooseDate, relativeToday);
            } else {
                //other date
                date = editJsonData.get("date").getAsString();
                updateDateChoose(relativeChooseDate, relativeToday);
            }
            txtDateSelected.setText(date);
            //time start
            start_time = editJsonData.get("start_time").getAsString();
            txtStartTime.setText(DateUtils.timeFormat(date + " " + start_time));
            //time end
            end_time = editJsonData.get("end_time").getAsString();
            txtEndTime.setText(DateUtils.timeFormat(date + " " + end_time));


            instructio = editJsonData.get("instruction").getAsString();
            editSpecialInstruction.setText(instructio);

            if (editJsonData.get("type").getAsString().equalsIgnoreCase(AUDIO_CALL)) {
                type = AUDIO_CALL;
                updateUI(relativeVideoCall, relativeAudioCall, relativeGetAccessCall);
            }
            if (editJsonData.get("type").getAsString().equalsIgnoreCase(VIDEO_CALL)) {
                type = VIDEO_CALL;
                updateUI(relativeVideoCall, relativeAudioCall, relativeGetAccessCall);
            }
            if (editJsonData.get("type").getAsString().equalsIgnoreCase(GET_ACCESS)) {
                type = GET_ACCESS;
                updateUI(relativeVideoCall, relativeAudioCall, relativeGetAccessCall);
            }
        } else {
            if (mCommonDetailsData != null) {
                if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                    txtPropertyAddress1.setTextColor(getResources().getColor(R.color.grey_font));
                    txtPropertyAddress1.setText(mCommonDetailsData.getAddress1());
                    txtPropertyAddress2.setText(mCommonDetailsData.getAddress2());
                } else {
                    if (!BaseApplication.getInstance().getLoginID().equals(mCommonDetailsData.getData().getLoginId())) {
                        txtPropertyAddress1.setText("****");
                        txtPropertyAddress1.setTextSize(13);
                        txtPropertyAddress2.setText(mCommonDetailsData.getAddress2());
                    } else {
                        txtPropertyAddress1.setTextColor(getResources().getColor(R.color.grey_font));
                        txtPropertyAddress1.setText(mCommonDetailsData.getAddress1());
                        txtPropertyAddress2.setText(mCommonDetailsData.getAddress2());
                    }
                }
                  if (PreferenceUtils.getPlanVersionStatus()){
                      txtPropertyAddress1.setTextColor(getResources().getColor(R.color.grey_font));
                      txtPropertyAddress1.setText(mCommonDetailsData.getAddress1());
                      txtPropertyAddress2.setText(mCommonDetailsData.getAddress2());
                  }
                txtPropertyBad.setText(mCommonDetailsData.getBedNos() + " bd");
                txtPropertyBath.setText(mCommonDetailsData.getBathNos() + " ba");
                txtPropertyMatters.setText(mCommonDetailsData.getArea() + " Sqft");
                String price = "";
                if (mCommonDetailsData.getPriceOn() != null && !mCommonDetailsData.getPriceOn().equalsIgnoreCase("")) {
                    price = mCommonDetailsData.getPriceOn();
                } else {
                    price = mCommonDetailsData.getRentAmount();
                }
                txtPropertyPrice.setText("$" + currencyFormat(price));

                if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
                    RequestOptions myOptions = new RequestOptions()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    Glide.with(this)
                            .asBitmap()
                            .load(mCommonDetailsData.getImages().get(0).getImageUrl())
                            .apply(myOptions)
                            .into(imagePropertyView);

                } else {
                    imagePropertyView.setBackgroundResource(R.drawable.no_image_icon);
                }
            }

        }

    }

    private boolean containCheck(CharSequence values) {
        Pattern digitPattern = Pattern.compile("\\d{6}");
        Pattern digitthree = Pattern.compile("\\d{3}");

        if (values.toString().toLowerCase().contains("call me")
                || values.toString().toLowerCase().contains("contact me")
                || values.toString().toLowerCase().contains("gmail")
                || values.toString().toLowerCase().contains("email")
                || values.toString().toLowerCase().contains("mobile")
                || values.toString().toLowerCase().contains("phone")
                || values.toString().toLowerCase().contains("@")
                || values.toString().toLowerCase().contains("outlook")
                || values.toString().toLowerCase().contains("yahoo")
                || values.toString().toLowerCase().contains(".com")) {
            return true;
        }
        Matcher mThree = digitthree.matcher(values);
        if (mThree.find()) {
            return true;
        }

        Matcher m = digitPattern.matcher(values);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }

    @Override
    public int getLayoutResource() {
        return R.layout.metting_schedule_bottom_sheet_ui;
    }

    @OnClick({R.id.btnSubmitSchedule, R.id.ivScheduleCancel, R.id.relativeVideoCall, R.id.relativeAudioCall, R.id.relativeGetAccessCall, R.id.relativeChooseDate, R.id.relativeStartDate})
    void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ivScheduleCancel:
                if (dailogListener != null) {
                    dailogListener.onCancelClick();
                }
                break;
            case R.id.relativeVideoCall:
                if (!created_type.equalsIgnoreCase("admin")) {
                    type = VIDEO_CALL;
                    updateUI(relativeVideoCall, relativeAudioCall, relativeGetAccessCall);
                }
                break;
            case R.id.relativeAudioCall:
                if (!created_type.equalsIgnoreCase("admin")) {
                    type = AUDIO_CALL;
                    updateUI(relativeAudioCall, relativeVideoCall, relativeGetAccessCall);
                }
                break;
            case R.id.relativeGetAccessCall:
                if (!created_type.equalsIgnoreCase("admin")) {
                    type = GET_ACCESS;
                    updateUI(relativeGetAccessCall, relativeVideoCall, relativeAudioCall);
                }
                break;
            case R.id.relativeChooseDate:
                //selection date
                updateDateChoose(relativeChooseDate, relativeToday);
                setDateTimeField();
                break;
            case R.id.relativeStartDate:
                showStartTimePickerDialog(txtStartTime);
                break;
            case R.id.btnSubmitSchedule:
                instructio = editSpecialInstruction.getText().toString();
                if (type.equalsIgnoreCase("")) {
                    ToastUtils.shortToast(Constants.SELECT_SCHEDULE_TYPE);
                    return;
                }

                if (date.equalsIgnoreCase("")) {
                    ToastUtils.shortToast(Constants.SELECT_DATE);
                    return;
                }
                if (start_time.equalsIgnoreCase("")) {
                    ToastUtils.shortToast(Constants.SELECT_STIME);
                    return;
                }

                //date and time to convert UTC
                String convertUTC = localToUTC(date + " " + start_time);
                Log.e("TAG", "Date is checked->" + convertUTC);
                String convertEndUTC = localToUTC(date + " " + end_time);

                if (dailogListener != null) {
                    dailogListener.scheduleMeetingRequest(getDateOnly(convertUTC), getTimeOnly(convertUTC), getTimeOnly(convertEndUTC), instructio, type, created_type);
                }
                break;
        }
    }

    private String getTimeOnly(String outDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date value = formatter.parse(outDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss"); //this format changeable
            outDate = dateFormatter.format(value);

//            Log.d("ourDate", outDate);
        } catch (Exception e) {
            outDate = "00:00:00";
        }
        return outDate;
    }

    private String getDateOnly(String outDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //hh:mm
            Date value = formatter.parse(outDate);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd"); //this format changeable
            outDate = dateFormatter.format(value);

        } catch (Exception e) {
            outDate = "00:00:00";
        }
        return outDate;
    }

    private String localToUTC(String ourDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getDefault());
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            ourDate = dateFormatter.format(value);

        } catch (Exception e) {
            ourDate = "00-00-0000 00:00";
        }
        return ourDate;
    }

    private boolean startEndTimeCompareTo() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date inTime = sdf.parse(start_time);
            Date outTime = sdf.parse(end_time);

            int dateDelta = inTime.compareTo(outTime);
            switch (dateDelta) {
                case 0:
                    //startTime and endTime not **Equal**
                    ToastUtils.shortToast("startTime and endTime not Equal");
                    return false;

                case 1:
                    //endTime is **Greater** then startTime
                    ToastUtils.shortToast("EndTime is Greater then startTime");
                    return false;

                case -1:
                    //startTime is **Greater** then endTime
//                    ToastUtils.shortToast("StartTime is Greater then endTime");
                    return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateUI(View view1, View view2, View view3) {
        //UI Update
        view1.setBackgroundResource(R.drawable.selected_border_button);
        view2.setBackgroundResource(R.drawable.border_button);
        view3.setBackgroundResource(R.drawable.border_button);
    }

    public void updateDateChoose(View view1, View view2) {
        view1.setBackgroundResource(R.drawable.selected_border_button);
        view2.setBackgroundResource(R.drawable.border_button);
    }

    public void updateTimeChoose(View view1) {
        view1.setBackgroundResource(R.drawable.selected_border_button);
    }

    private void clearTime() {
        start_time = "";
        end_time = "";
        txtStartTime.setText("");
    }

    /*calender*/
    private void setDateTimeField() {
        clearTime();
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarSelected.set(year, month, dayOfMonth);
                txtDateSelected.setText(dateFormatter1.format(calendarSelected.getTime()));
                date = dateFormatter.format(calendarSelected.getTime());
                showStartTimePickerDialog(txtStartTime);
            }
        }, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
        datePickerDialog.show();
    }

    /*Start time picker*/
    /* Calendar datetime = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);
                //check selected dat is current date
                if(datetime.getTimeInMillis() == c.getTimeInMillis()){
                    //current date
                    if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                    } else {
                        //it's before current'
                        Toast.makeText(getActivity(), "Invalid Time", Toast.LENGTH_LONG).show();
                        showStartTimePickerDialog(txtStartTime);
                        return;
                    }
                }*/
    public String showStartTimePickerDialog(View viewTxt) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        /*new*/
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                /**********************************************/
                Calendar datetime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                boolean isCurrentDate = false;
                try {
                    if (sdf.format(calendarSelected.getTime()).equalsIgnoreCase(sdf.format(datetime.getTime()))) {
                        isCurrentDate = true;
                    } else {
                        isCurrentDate = false;
                    }
                    if (isCurrentDate) {
                        //current date
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                        } else {
                            //it's before current'
                            Toast.makeText(getActivity(), "Invalid Time", Toast.LENGTH_LONG).show();
                            showStartTimePickerDialog(txtStartTime);
                            return;
                        }
                    }

                    /*******************************************/
                    boolean isPM = (hourOfDay >= 12);
                    ((MaterialTextView) viewTxt).setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    start_time = hourOfDay + ":" + minute + ":00";
                    selectedS_H = hourOfDay;
                    selectedS_M = minute;

                    //open end time picker
                    updateTimeChoose(relativeStartDate);
                    end_time = showEndTimePickerDialog(txtEndTime);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, hour, minute, false);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();

        return start_time;

    }


    public String showEndTimePickerDialog(View viewTxt) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        int hourOfDay, minute;
        hourOfDay = selectedS_H;
        minute = selectedS_M;

        Date afterAdding30Mins = null;
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCal.set(Calendar.MINUTE, minute);
        newCal.set(Calendar.SECOND, 00);
        String newTime = "";
        try {
            afterAdding30Mins = new Date(newCal.getTimeInMillis() + (30 * 60 * 1000));
            newTime = df.format(afterAdding30Mins.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "End Time=>" + newCal.getTime());
        Log.e("TAG", "New End Time=>" + newTime);
        return newTime;
    }

    public void addListener(DialogListener dailog, String createdTYPE, JsonObject jsonObject, CommonData mData) {
        dailogListener = dailog;
        created_type = createdTYPE;
        editJsonData = jsonObject;
        mCommonDetailsData = mData;
    }

    public interface DialogListener {
        void scheduleMeetingRequest(String selectedDate, String selectedStartTime, String selectedEndTime, String mMessage, String selectedType, String createdType);

        void onCancelClick();

    }
}
