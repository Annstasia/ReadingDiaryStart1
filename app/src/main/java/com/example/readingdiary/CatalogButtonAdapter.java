package com.example.readingdiary;

//package com.example.readingdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CatalogButtonAdapter extends RecyclerView.Adapter<CatalogButtonAdapter.ViewHolder>{

    private List<String> buttons;
    private CatalogButtonAdapter.OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CatalogButtonAdapter(List<String> buttons) {
        this.buttons = buttons;
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_button, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String tokens[] = buttons.get(i).split("/");

        viewHolder.path1.setText(tokens[tokens.length - 1] + " > ");
//        int type = getItemViewType(i);
//        if (type == TYPE_ITEM1){
//            RealNote realNote = (RealNote) notes.get(i);
//            viewHolder.path1.setText(realNote.getPath());
//            viewHolder.author.setText(realNote.getAuthor());
//            viewHolder.title.setText(realNote.getTitle());
//        }
//        if (type == TYPE_ITEM2){
//            Directory directory = (Directory) notes.get(i);
//            viewHolder.path2.setText(directory.getDirectory());
//        }

//
//
//        Note note = notes.get(i);
//        viewHolder.path.setText(note.getPath());
//        viewHolder.title.setText(note.getTitle());
//        viewHolder.author.setText(note.getAuthor());

    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // определяем какой тип в текущей позиции
//        int type = notes.get(position).getItemType();
//        if (type == 0) return TYPE_ITEM1;
//        else return TYPE_ITEM2;
//
//    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clearAdapter() {
        buttons.clear();
        notifyDataSetChanged();
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView path1;
//        private TextView path2;
//
//        private TextView title;
//        private TextView author;
        private TextView path1;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            path1 = (TextView) itemView.findViewById(R.id.catalog_button);
//            title = (TextView) itemView.findViewById(R.id.titleViewCatalog);
//            author = (TextView) itemView.findViewById(R.id.authorViewCatalog);
//            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);

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



