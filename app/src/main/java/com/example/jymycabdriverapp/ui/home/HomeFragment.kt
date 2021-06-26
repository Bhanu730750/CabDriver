package com.example.jymycabdriverapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.jymycabdriverapp.Activity.OrderAcceptActivity
import com.example.jymycabdriverapp.MainActivity
import com.example.jymycabdriverapp.R
import com.example.jymycabdriverapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
//    private var viewPager: ViewPager? = null
//    private var tabLayout: TabLayout? = null
    private var mContext: Context? = null
    lateinit var relativelayout1: RelativeLayout

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        viewPager = root.findViewById(R.id.viewpager);
//        tabLayout = root.findViewById(R.id.tabs);
//        setupViewPager(viewPager!!,root);
//        tabLayout!!.setupWithViewPager(viewPager);
        relativelayout1 = root.findViewById(R.id.relativelayout1)
        relativelayout1.setOnClickListener {
            val intent = Intent(requireContext(), OrderAcceptActivity::class.java)
            startActivity(intent)
        }

        return root
    }
//    private fun setupViewPager(viewPager: ViewPager, view: View) {
//        val adapter = ViewPagerAdapter(childFragmentManager)
//        adapter.addFragment(Processride(), "Process Rides")
//        adapter.addFragment(UpcomingRides(), "Upcoming Rides")
//        viewPager.adapter = adapter
//    }
//    internal class ViewPagerAdapter(manager: FragmentManager?) :
//        FragmentPagerAdapter(manager!!) {
//        private val mFragmentList: MutableList<Fragment> = ArrayList()
//        private val mFragmentTitleList: MutableList<String> = ArrayList()
//        override fun getItem(position: Int): Fragment {
//            return mFragmentList[position]
//        }
//
//        override fun getCount(): Int {
//            return mFragmentList.size
//        }
//
//        fun addFragment(fragment: Fragment, title: String) {
//            mFragmentList.add(fragment)
//            mFragmentTitleList.add(title)
//        }
//
//        override fun getPageTitle(position: Int): CharSequence? {
//            return mFragmentTitleList[position]
//        }
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mContext = context
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mContext = null
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}