package com.codeontime.konik.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeontime.konik.Model.ContestsModel;
import com.codeontime.konik.R;
import com.codeontime.konik.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContestAdapter   extends RecyclerView.Adapter<ContestAdapter.ContestAdapter_Holder> {
    private Context mContext;
    private List<ContestsModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public ContestAdapter (android.content.Context mContext, List<ContestsModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public ContestAdapter.ContestAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_contest_item,parent,false); //connecting to cardview
        return new ContestAdapter.ContestAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContestAdapter.ContestAdapter_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getCoPhotoUrl();
        Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mItemImageView);

        String dsTitle = mData.get(position).getCoName();
        long diViews = mData.get(position).getCoiViewCount();
        String Syllabus = mData.get(position).getCoSyllabus();

        holder.mItemTittleText.setText(dsTitle);
        holder.mItemBioText.setText(Syllabus);
        holder.mItemViewText.setText(String.valueOf(diViews));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class ContestAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mItemImageView;
        TextView mItemTittleText;
        TextView mItemViewText;
        TextView mItemBioText;

        public ContestAdapter_Holder(@NonNull View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.level_d_item_img);
            mItemTittleText = (TextView)itemView.findViewById(R.id.level_d_title_id);
            mItemViewText = (TextView)itemView.findViewById(R.id.level_d_views);
            mItemBioText = (TextView)itemView.findViewById(R.id.level_d_bio_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });
        }
    }



}
