package breeds.dog.stratos.dogbreeds;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static breeds.dog.stratos.dogbreeds.R.layout.breed_list_item;

/**
 * Created by botsaef on 20/11/2017.
 */

public class BreedsAdapter extends RecyclerView.Adapter<BreedsAdapter.MyViewHolder> {

    private List<String> breedsList;
    private final BreedsAdapterOnClickHandler mClickHandler;

    public interface BreedsAdapterOnClickHandler {
        void onClick(String breed);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView breedName;

        public MyViewHolder(View view) {
            super(view);
            breedName = (TextView) view.findViewById(R.id.breedName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String breed = breedsList.get(adapterPosition);
            mClickHandler.onClick(breed);
        }
    }

    public BreedsAdapter(List<String> breedsList, BreedsAdapterOnClickHandler clickHandler) {
        this.breedsList = breedsList;
        mClickHandler = clickHandler;
    }

    @Override
    public BreedsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.breed_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BreedsAdapter.MyViewHolder holder, int position) {
        String breedName = breedsList.get(position);
        holder.breedName.setText(breedName);
    }

    @Override
    public int getItemCount() {
        return breedsList.size();
    }
}