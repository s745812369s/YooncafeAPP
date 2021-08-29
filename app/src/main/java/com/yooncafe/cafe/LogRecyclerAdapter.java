package com.yooncafe.cafe;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.LogViewHolder> {
    Context context;
    List<LogLog> logs;
    int log_layout;
    private Intent intent;

    public LogRecyclerAdapter(Context context, List<LogLog> logs, int log_layout) {
        this.context = context;
        this.logs = logs;
        this.log_layout = log_layout;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, null);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, final int position) {
        final LogLog log = logs.get(position);
        holder.str.setText(log.getstr());
        holder.content.setText(log.getcontent());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView str;
        CardView logview;

        public LogViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.content);
            str = (TextView) itemView.findViewById(R.id.str);
            logview = (CardView) itemView.findViewById(R.id.logview);
        }
    }
}