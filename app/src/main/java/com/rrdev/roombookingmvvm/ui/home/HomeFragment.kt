package com.rrdev.roombookingmvvm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asksira.loopingviewpager.LoopingViewPager
import com.asksira.loopingviewpager.indicator.CustomShapePagerIndicator
import com.bumptech.glide.Glide
import com.rrdev.roombookingmvvm.R
import com.rrdev.roombookingmvvm.RoomBookingApps.Companion.prefManager
import com.rrdev.roombookingmvvm.data.db.AppDatabase
import com.rrdev.roombookingmvvm.data.db.entities.Rooms
import com.rrdev.roombookingmvvm.data.network.MyApi
import com.rrdev.roombookingmvvm.data.network.NetworkConnectionInterceptor
import com.rrdev.roombookingmvvm.data.repositories.RoomRepository
import com.rrdev.roombookingmvvm.databinding.FragmentHomeBinding
import com.rrdev.roombookingmvvm.util.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.content_fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewPager: LoopingViewPager
    private var adapter: ViewPagerAutoScroolAdapter? = null
    private lateinit var indicatorView: CustomShapePagerIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = RoomRepository(api,db)
        val factory = HomeViewModelFactory(repository)

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        viewModel = ViewModelProviders.of(
            this , factory).get(HomeViewModel::class.java)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        indikatorViewPager()
        imageProfile()
    }

    override fun onResume() {
        bindUI()
        toProfile()
        toMyBooking()
        viewPager.resumeAutoScroll()
        super.onResume()
    }

    override fun onPause() {
        viewPager.pauseAutoScroll()
        super.onPause()
    }

    private fun toProfile() {
        circleImageView2.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun toMyBooking() {
        fabMyBooking.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_MybookingFragment)
        }
    }

    //recycler with Groupie
    private fun bindUI() = Coroutines.main{
        pbHome.show()
        viewModel.room.await().observe(this ,Observer {
            pbHome.hide()
            initRecyclerView(it.toHomeItem())
        })
    }
    //recycler with Groupie
    private fun List<Rooms>.toHomeItem() : List<HomeItem>{
        return this.map {
            HomeItem(it)
        }
    }
    //init recycler with Groupie
    private fun initRecyclerView(homeItem: List<HomeItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(homeItem)
        }

        rvHome.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener{items, view->
            (items as? HomeItem)?.let {
                showRoomDetail(it.rooms.namaRoom,view)
                prefManager.spNamaRoom = it.rooms.namaRoom
            }
        }
    }
    //show detail item Rooms
    private fun showRoomDetail(namaRoom: String, view: View){
        val actionDetail = HomeFragmentDirections.actionHomeFragmentToDetailRoomFragment(namaRoom)
        Navigation.findNavController(view).navigate(actionDetail)
    }

    //init AutoScroolViewPager
    fun init() {
        viewPager = view?.findViewById(R.id.viewpager)!!
        indicatorView = view?.findViewById(R.id.indicator)!!
        adapter = ViewPagerAutoScroolAdapter(
            requireContext(),
            createDummyItems(),
            true
        )
        viewPager.adapter = adapter
    }
    //dummy data AutoScroolViewPager
    private fun createDummyItems(): ArrayList<Int> {
        val items = ArrayList<Int>()
        items.add(0, 1)
        items.add(1, 2)
        items.add(2, 3)
        items.add(3, 4)
        items.add(4, 5)
        items.add(5, 6)
        //0-> special items
        return items
    }
    //indikator AutoScroolViewPager
    private fun indikatorViewPager() {
        //Custom bind indicator
        indicatorView.highlighterViewDelegate = {
            val highlighter = View(activity)
            highlighter.layoutParams = FrameLayout.LayoutParams(16.dp(), 3.dp())
            highlighter.setBackgroundColor(activity?.resources!!.getColor(R.color.white))
            highlighter
        }
        indicatorView.unselectedViewDelegate = {
            val unselected = View(activity)
            unselected.layoutParams = LinearLayout.LayoutParams(16.dp(), 3.dp())
            unselected.setBackgroundColor(activity?.resources!!.getColor(R.color.white))
            unselected.alpha = 0.4f
            unselected
        }
        viewPager.onIndicatorProgress = { selectingPosition, progress ->
            indicatorView.onPageScrolled(
                selectingPosition,
                progress
            )
        }
        indicatorView.updateIndicatorCounts(viewPager.indicatorCount)
    }


    private fun imageProfile() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            val imageProfile = it.image
            Glide.with(this).load(BASE+imageProfile).into(circleImageView2)
        })
    }
}
