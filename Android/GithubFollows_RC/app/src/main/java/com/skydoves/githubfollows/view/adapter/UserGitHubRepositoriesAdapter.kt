package com.skydoves.githubfollows.view.adapter

import android.util.Log
import android.view.View
import com.skydoves.githubfollows.R
import com.skydoves.githubfollows.models.ItemDetail
import com.skydoves.githubfollows.view.viewholder.BaseViewHolder
import com.skydoves.githubfollows.view.viewholder.RepoViewHolder

/**
 * Created by Arjan on 3/25/2018.
 */
class UserGitHubRepositoriesAdapter : BaseAdapter() {

    private val section_itemDetail = 0

    init {
        addSection(ArrayList<ItemDetail>())
    }

    fun addItemDetail(itemDetail: ItemDetail) {
        sections[section_itemDetail].add(itemDetail)
        Log.i("repoAdapter","itemDetail:"+itemDetail.content)
        notifyItemChanged(sections[section_itemDetail].size)
    }

    override fun layout(sectionRow: BaseAdapter.SectionRow): Int {
        return R.layout.item_detail_info
    }

    override fun viewHolder(layout: Int, view: View): BaseViewHolder {
        return RepoViewHolder(view)
    }
}