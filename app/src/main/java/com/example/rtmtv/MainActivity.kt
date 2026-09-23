package com.example.rtmtv

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ChannelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.channelList)
        val progress = findViewById<ProgressBar>(R.id.progress)

        adapter = ChannelAdapter { channel ->
            startActivity(
                Intent(this, PlayerActivity::class.java)
                    .putExtra("url", channel.url)
                    .putExtra("name", channel.name)
                    .putExtra("referer", channel.headers["Referer"])
                    .putExtra("userAgent", channel.headers["User-Agent"])
            )
        }

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        lifecycleScope.launch {
            val channels = PlaylistRepository.loadChannels()
            adapter.submit(channels)
            progress.visibility = View.GONE
        }
    }
}

class ChannelAdapter(
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.VH>() {

    private val items = mutableListOf<Channel>()

    fun submit(list: List<Channel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.chName)
        val group: TextView = v.findViewById(R.id.chGroup)
        val logo: ImageView = v.findViewById(R.id.chLogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = items[position]
        holder.name.text = ch.name
        holder.group.text = ch.group ?: ""
        if (ch.logo != null) {
            Glide.with(holder.logo).load(ch.logo).into(holder.logo)
        } else {
            holder.logo.setImageDrawable(null)
        }
        holder.itemView.setOnClickListener { onClick(ch) }
    }

    override fun getItemCount() = items.size
}
