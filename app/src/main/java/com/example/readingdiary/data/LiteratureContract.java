package com.example.readingdiary.data;

import android.provider.BaseColumns;

public final class LiteratureContract {
    private LiteratureContract() {
    };
    public static final class NoteTable implements BaseColumns {
        public final static String TABLE_NAME = "note";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PATH = "path";
        public final static String COLUMN_AUTHOR = "author";
        public final static String COLUMN_TITLE = "title";
//        public final static String COLUMN_DIRECTORY = "directory";



    }

    public static final class PathTable implements BaseColumns {
        public final static String TABLE_NAME = "pathTable";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PARENT = "parent";
        public final static String COLUMN_CHILD = "child";

    }


}
