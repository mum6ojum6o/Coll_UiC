package com.skydoves.githubfollows.view.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.skydoves.githubfollows.R
import com.skydoves.githubfollows.factory.AppViewModelFactory
import com.skydoves.githubfollows.models.Follower
import com.skydoves.githubfollows.models.GithubUser
import com.skydoves.githubfollows.models.Resource
import com.skydoves.githubfollows.utils.PowerMenuUtils
import com.skydoves.githubfollows.view.RecyclerViewPaginator
import com.skydoves.githubfollows.view.adapter.GithubUserAdapter
import com.skydoves.githubfollows.view.ui.detail.DetailActivity
import com.skydoves.githubfollows.view.ui.search.SearchActivity
import com.skydoves.githubfollows.view.viewholder.GithubUserHeaderViewHolder
import com.skydoves.githubfollows.view.viewholder.GithubUserViewHolder
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Developed by skydoves on 2018-01-19.
 * Copyright (c) 2018 skydoves rights reserved.
 */

class MainActivity : AppCompatActivity(), GithubUserHeaderViewHolder.Delegate, GithubUserViewHolder.Delegate {

    @Inject lateinit var viewModelFactory: AppViewModelFactory
    private  val TAG: String = "MainActivity"
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java) }
    private val adapter by lazy { GithubUserAdapter(this, this) }

    private lateinit var paginator: RecyclerViewPaginator
    private lateinit var powerMenu: PowerMenu

    private val onPowerMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
        if(!item.isSelected) {
            Log.i(TAG,"powwerMenuItemClickListener")
            viewModel.putPreferenceMenuPosition(position)
            powerMenu.setSelected(position)
            powerMenu.dismiss()
            restPagination(viewModel.getUserName())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG,"onCreate");
        main_recyclerView.adapter = adapter
        main_recyclerView.layoutManager = LinearLayoutManager(this)
        paginator = RecyclerViewPaginator(
                recyclerView = main_recyclerView,
                isLoading = { viewModel.isLoading },
                loadMore = { loadMore(it) },
                onLast = { viewModel.isOnLast }
        )

        initializeUI()
        observeViewModel()
    }

    private fun initializeUI() {
        Log.i(TAG,"intializeUI");
        powerMenu = PowerMenuUtils.getOverflowPowerMenu(this, this, onPowerMenuItemClickListener)
        powerMenu.setSelected(viewModel.getPreferenceMenuPosition())
        toolbar_main_overflow.setOnClickListener { powerMenu.showAsDropDown(it) }
        toolbar_main_search.setOnClickListener { startActivityForResult<SearchActivity>(SearchActivity.intent_requestCode) }
    }

    private fun observeViewModel() {
        Log.i(TAG,"ObserverModel");
        viewModel.githubUserLiveData.observe(this, Observer { it?.let { adapter.updateHeader(it) } })
        viewModel.followersLiveData.observe(this, Observer { updateGithubUserList(it) })
        viewModel.toast.observe(this, Observer { toast(it.toString()) })
    }

    private fun loadMore(page: Int) {
        Log.i(TAG,"loadMore");
        viewModel.postPage(page)
    }

    private fun updateGithubUserList(resource: Resource<List<Follower>>?) {
        Log.i(TAG,"updateGithubUserList");
        resource?.data?.let { adapter.addFollowList(it) }
    }

    private fun restPagination(user: String) {
        Log.i(TAG,"restPagination");
        adapter.clearAll()
        paginator.resetCurrentPage()
        viewModel.refresh(user)
    }

    override fun onCardClicked(githubUser: GithubUser) {
        Log.i(TAG,"onCardClicked");
        startActivity<DetailActivity>(DetailActivity.intent_login to githubUser.login, DetailActivity.intent_avatar to githubUser.avatar_url)
    }

    override fun onItemClick(githubUser: Follower, view: View) {
        Log.i(TAG,"onItemClick");
        DetailActivity.startActivity(this, githubUser, view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            DetailActivity.intent_requestCode, SearchActivity.intent_requestCode -> data?.let {
                restPagination(data.getStringExtra(viewModel.getUserKeyName()))
            }
        }
    }

    override fun onBackPressed() {
        Log.i(TAG,"onBackPressed");
       when(powerMenu.isShowing) {
           true -> powerMenu.dismiss()
           else -> super.onBackPressed()
       }
    }
}