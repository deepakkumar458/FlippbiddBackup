package com.flippbidd.Fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.flippbidd.Model.Response.calendardata.CalendarResponse
import com.flippbidd.Model.Response.calendardata.CalendarResponse.Datum
import com.flippbidd.R
import com.flippbidd.activity.my_calender.RequestCallListActivity.MyCalendarItemList
import com.flippbidd.activity.my_calender.SecondFragment.MyCalendarItem
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_request_call_list.*

class RequestCallListFragment : Fragment(), MyCalendarItem {

    private val moContext: Context? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    var navController: NavController? = null
    var txtHeaderMyCalendar: MaterialTextView? = null
    var imageBellIcon: AppCompatImageView? = null
    var tvNotificationView: AppCompatImageView? = null
    var toolbar: Toolbar? = null

    var moMyCalendarItem: MyCalendarItemList? = null

    interface MyCalendarItemList {
        fun onRefreshData(moJsonArrayList: List<Datum?>?)
    }

    fun setMyCalendarItemList(moMyCalendarItem: MyCalendarItemList?) {
        this.moMyCalendarItem = moMyCalendarItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_call_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBarColorChanged()

        setHasOptionsMenu(true)

        navController =
            Navigation.findNavController(
                requireActivity(),
                R.id.nav_host_fragment_content_request_call_list
            )
        appBarConfiguration = AppBarConfiguration.Builder(navController!!.graph).build()

        //        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        imageRequestCallListBack.setOnClickListener(View.OnClickListener {
            if (navController!!.popBackStack()) {
                toolbarSet(true)
            } else {
                requireActivity().onBackPressed()
            }
        })

        imageBellIcon!!.setOnClickListener {
            toolbarSet(false)
            navController!!.navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    fun toolbarSet(isValue: Boolean) {
        if (isValue) {
            imageBellIcon!!.visibility = View.VISIBLE
            txtHeaderMyCalendar!!.text =
                moContext!!.resources.getString(R.string.second_fragment_label)
        } else {
            imageBellIcon!!.visibility = View.GONE
            tvNotificationView!!.visibility = View.GONE
            txtHeaderMyCalendar!!.text =
                moContext!!.resources.getString(R.string.first_fragment_label)
        }
    }

    private fun statusBarColorChanged() {
        val window: Window = requireActivity().getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#3FA3D1")
    }

    protected fun updateCounts(counts: String) {
        if (counts.equals("0", ignoreCase = true)) {
            tvNotificationView!!.visibility = View.GONE
            return
        }
        tvNotificationView!!.visibility = View.VISIBLE
        imageBellIcon!!.visibility = View.GONE

//        tvNotificationCounts.setText(counts);
    }



    /*override fun onSupportNavigateUp(): Boolean {
        val navController =
            Navigation.findNavController(
                requireActivity(),
                R.id.nav_host_fragment_content_request_call_list
            )
        return (NavigationUI.navigateUp(navController, appBarConfiguration!!)
                || super.onSupportNavigateUp())
    }*/


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity()!!.menuInflater.inflate(R.menu.call_notification, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main_notification -> {
                navController!!.navigate(R.id.action_SecondFragment_to_FirstFragment)
                true
            }
            android.R.id.home -> {
                requireActivity()!!.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRefreshData(moJsonArrayList: MutableList<CalendarResponse.Datum>?) {
        if (moMyCalendarItem != null) {
            moMyCalendarItem!!.onRefreshData(moJsonArrayList)
        }
    }

    override fun onClear() {

    }

}