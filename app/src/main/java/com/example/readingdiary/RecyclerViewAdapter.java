package com.example.readingdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


// адаптер элементов каталога Note
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<Note> notes;
    private OnItemClickListener mListener;
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == TYPE_ITEM1){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item0, viewGroup, false);
        }
        else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item1, viewGroup, false);
        }
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (type == TYPE_ITEM1){
            RealNote realNote = (RealNote) notes.get(i);
            viewHolder.path1.setText(realNote.getPath());
            viewHolder.author.setText(realNote.getAuthor());
            viewHolder.title.setText(realNote.getTitle());
        }
        if (type == TYPE_ITEM2){
            Directory directory = (Directory) notes.get(i);
            viewHolder.path2.setText(directory.getDirectory());
        }

//
//
//        Note note = notes.get(i);
//        viewHolder.path.setText(note.getPath());
//        viewHolder.title.setText(note.getTitle());
//        viewHolder.author.setText(note.getAuthor());

    }

    @Override
    public int getItemViewType(int position) {
        // определяем какой тип в текущей позиции
        int type = notes.get(position).getItemType();
        if (type == 0) return TYPE_ITEM1;
        else return TYPE_ITEM2;

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
        private TextView path1;
        private TextView path2;

        private TextView title;
        private TextView author;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            path1 = (TextView) itemView.findViewById(R.id.pathViewCatalog);
            title = (TextView) itemView.findViewById(R.id.titleViewCatalog);
            author = (TextView) itemView.findViewById(R.id.authorViewCatalog);
            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);

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
