package com.example.silvercrest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.MyViewHolder> {
    public ArrayList<TransactionListModel> childModelArrayList;
    Context cxt;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView videoName;
        public  View viewItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.tv_book_name_main);
            viewItem=itemView;

        }
    }

    public TransactionListAdapter(ArrayList<TransactionListModel> arrayList, Context mContext) {
        this.cxt = mContext;
        this.childModelArrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TransactionListModel currentItem = childModelArrayList.get(position);
        String text="Account Name: "+currentItem.getName()+"\nAccount Number: "+currentItem.getNumber()
                +"\nBank Name: "+currentItem.getBank()+"\nAmount: "+currentItem.getAmount()+"\nSwift Code: "+currentItem.getSwift()+"\nPurpose of Payment: "+currentItem.getPurpose();
        holder.videoName.setText(text);

//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//// Set video url as data source
//        retriever.setDataSource(currentItem.getUrl(), new HashMap<String, String>());
//// Get frame at 2nd second as Bitmap image
//        Bitmap bitmap = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//// Display the Bitmap image in an ImageView
//        holder.videoImage.setImageBitmap(bitmap);
//        holder.viewItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(cxt, PdfViewerActivity.class);
//                intent.putExtra("pdfurl",currentItem.getBookurl());
//                cxt.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }

}
