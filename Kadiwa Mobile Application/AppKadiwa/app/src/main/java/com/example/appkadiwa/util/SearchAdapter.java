package com.example.appkadiwa.util;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appkadiwa.util.Item;
import com.example.appkadiwa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Item> searchResults;
    private List<Item> originalSearchResults;

    public SearchAdapter(List<Item> searchResults) {
        this.searchResults = searchResults;
        this.originalSearchResults = new ArrayList<>(searchResults);
    }

    public void setItems(List<Item> searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    // Add this method to update the search results
    public void filterResults(String query) {
        query = query.toLowerCase();
        searchResults.clear();

        if (query.isEmpty()) {
            // If the query is empty, restore the original search results
            searchResults.addAll(originalSearchResults);
        } else {
            // Filter the search results based on the query
            for (Item item : originalSearchResults) {
                if (item.getName().toLowerCase().contains(query)) {
                    searchResults.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Item item = searchResults.get(position);

        holder.itemNameTextView.setText(item.getName());
        holder.itemDescriptionTextView.setText(item.getDescription());
        holder.itemPriceTextView.setText(String.format(Locale.getDefault(), "₱%.2f", item.getPrice()));

        Picasso.get().load(item.getImageUrl()).into(holder.itemImageView);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        CardView itemCardView;
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemDescriptionTextView;
        TextView itemPriceTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            itemImageView = itemView.findViewById(R.id.imageView);
            itemNameTextView = itemView.findViewById(R.id.nameTextView);
            itemDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            itemPriceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
