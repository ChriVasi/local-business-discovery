package local.business.discovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> implements Filterable {

    private List<Business> businesses;
    private List<Business> filteredList;

    public BusinessAdapter(List<Business> businesses) {
        this.businesses = businesses;
        this.filteredList = new ArrayList<>(businesses);
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        Business business = filteredList.get(position);
        holder.bind(business);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView categoryTextView;
        private final TextView addressTextView;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
        }

        public void bind(Business business) {
            nameTextView.setText(business.getName());
            categoryTextView.setText(business.getCategory());
            addressTextView.setText(business.getAddress());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();

                if (filterPattern.isEmpty()) {
                    filteredList = new ArrayList<>(businesses);
                } else {
                    filteredList.clear();
                    for (Business business : businesses) {
                        if (business.getName().toLowerCase().contains(filterPattern)
                                || business.getCategory().toLowerCase().contains(filterPattern)
                                || business.getAddress().toLowerCase().contains(filterPattern)) {
                            filteredList.add(business);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Business>) results.values;
                notifyDataSetChanged();  // You can optimize this if needed
            }
        };
    }

    public List<Business> getData() {
        return businesses;
    }
    public void updateData(List<Business> newBusinesses) {
        businesses = new ArrayList<>(newBusinesses);
        filteredList.clear();
        filteredList.addAll(businesses);
        notifyDataSetChanged();
    }
}
