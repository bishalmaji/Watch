package com.bishal.watch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.SimpleTimeZone;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private Context context;
    private List<Bitmap> data;

    private List<String> textData;

    public ImageListAdapter(Context context, List<Bitmap> data,List<String> textData) {
        this.context = context;
        this.data = data;
        this.textData=textData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bitmap bitmap=data.get(position);
        String[] textStr = textData.get(position).trim().split("/", 2);
        String timeStr=textStr[1].trim();
        String locationStr=textStr[0].trim();
        String [] lat_lang_str=locationStr.split(",",2);
        holder.time.setText(timeStr);
        holder.location.setText(locationStr);
        holder.imageView.setImageBitmap(bitmap);
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri="http://maps.google.com/maps?q="+lat_lang_str[0]+","+lat_lang_str[1];

                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView location,time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.row_image);
            location=itemView.findViewById(R.id.location);
            time=itemView.findViewById(R.id.time);

        }
    }
}
