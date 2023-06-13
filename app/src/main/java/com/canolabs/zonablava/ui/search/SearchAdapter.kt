package com.canolabs.zonablava.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.canolabs.zonablava.R
import com.canolabs.zonablava.data.source.model.Destination

class SearchAdapter(private var suggestions: ArrayList<Destination>,
                    private val itemClickListener: (Destination) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_row, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchItem = suggestions[position]
        holder.bind(searchItem)
    }

    fun setSuggestions(newSuggestions: List<Destination>) {
        suggestions.clear()
        Log.d("search_fragment", "setSuggestion list given is: ${newSuggestions.size}")
        suggestions.addAll(newSuggestions)
        notifyDataSetChanged() //To provisionally avoid Invalid view holder adapter positionSearchViewHolder
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchSuggestionTitle: TextView = itemView.findViewById(R.id.searchSuggestionTitle)
        private val searchSuggestionDescription: TextView = itemView.findViewById(R.id.searchSuggestionDescription)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = suggestions[position]
                    itemClickListener.invoke(clickedItem)
                }
            }
        }

        fun bind(destination: Destination) {
            searchSuggestionTitle.text = destination.name
            searchSuggestionDescription.text = destination.description
        }
    }
}