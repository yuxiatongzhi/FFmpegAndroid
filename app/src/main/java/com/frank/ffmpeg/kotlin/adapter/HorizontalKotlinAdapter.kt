package com.frank.ffmpeg.kotlin.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.frank.ffmpeg.R
import com.frank.ffmpeg.kotlin.listener.OnItemClickListener

import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

/**
 * RecyclerView适配器
 * Created by frank on 2020/3/3.
 */
 open class HorizontalKotlinAdapter(itemList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList = arrayListOf<String>()
    private var onItemClickListener: OnItemClickListener? = null
    private var lastClickPosition: Int = 0

    init {
        this.itemList = itemList as ArrayList<String>
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OkViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_select, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val okViewHolder = holder as OkViewHolder
        okViewHolder.btn_select.text = itemList[position]
        okViewHolder.btn_select.setTextColor(Color.DKGRAY)
        if (onItemClickListener != null) {
            okViewHolder.btn_select.setOnClickListener {
                notifyItemChanged(lastClickPosition)
                Log.i(TAG, "lastClickPosition=$lastClickPosition")
                //设置当前选中颜色
                okViewHolder.btn_select.setTextColor(Color.BLUE)
                onItemClickListener!!.onItemClick(okViewHolder.adapterPosition)
                lastClickPosition = okViewHolder.adapterPosition
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private inner class OkViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var btn_select: Button

        init {
            btn_select = itemView.findViewById<View>(R.id.btn_select) as Button
        }
    }

    companion object {
        private val TAG = HorizontalKotlinAdapter::class.java.simpleName
    }
}
