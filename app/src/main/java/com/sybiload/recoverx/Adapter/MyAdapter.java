package com.sybiload.recoverx.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sybiload.recoverx.ActivityRecovery;
import com.sybiload.recoverx.R;
import com.sybiload.recoverx.Static;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    Activity activity;

    public MyAdapter(Activity activity)
    {
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recovery, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {

        holder.textViewRecovery.setText(Static.allRecovery.get(position).getRecovery());
        holder.textViewVersion.setText(Static.allRecovery.get(position).getVersion());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, ActivityRecovery.class);
                intent.putExtra("recovery", position);
                activity.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return Static.allRecovery.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView textViewRecovery;
        public TextView textViewVersion;

        public ViewHolder(View v)
        {
            super(v);

            textViewRecovery = (TextView) v.findViewById(R.id.textViewItemRecovery);
            textViewVersion = (TextView) v.findViewById(R.id.textViewItemVersion);
        }
    }
}