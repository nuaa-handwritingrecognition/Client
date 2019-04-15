package com.nuaa.handwriting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

public class CheckIndentityAdapter extends RecyclerView.Adapter<CheckIndentityAdapter.ViewHolder> {

    private Context mContext;

    private List<String> mList;

    private HashSet<Integer> mSelectIndex = new HashSet<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;

        public CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public CheckIndentityAdapter(Context context, String type, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }

    public HashSet<Integer> getSelectList() {
        return mSelectIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_check_indentity_recyclerview, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tv = view.findViewById(R.id.tv);
        holder.cb = view.findViewById(R.id.cb);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int pos) {
        String text = mList.get(pos);
        viewHolder.tv.setText(text);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectIndex.contains(pos)) {
                    mSelectIndex.remove(pos);
                    viewHolder.cb.setChecked(false);
                } else {
                    mSelectIndex.add(pos);
                    viewHolder.cb.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
