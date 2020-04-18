package com.example.readingdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<Note> notes;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public RecyclerViewAdapter(List<Note> notes) {
        this.notes = notes;
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item, viewGroup, false); // or R.layout.activity_catalog
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Note note = notes.get(i);
        viewHolder.path.setText(note.getPath());
        viewHolder.title.setText(note.getTitle());
        viewHolder.author.setText(note.getAuthor());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clearAdapter() {
        notes.clear();
        notifyDataSetChanged();
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView path;
        private TextView title;
        private TextView author;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            path = (TextView) itemView.findViewById(R.id.pathViewCatalog);
            title = (TextView) itemView.findViewById(R.id.titleViewCatalog);
            author = (TextView) itemView.findViewById(R.id.authorViewCatalog);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }


}
