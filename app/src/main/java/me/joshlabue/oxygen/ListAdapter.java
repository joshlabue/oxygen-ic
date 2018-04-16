package me.joshlabue.oxygen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ArrayList<ClassData>>{

    private ArrayList<ClassData> classData;
    Context ctx;

    public ListAdapter(Context ctx, ArrayList<ClassData> classData) {
        super(ctx, 0);
        this.ctx = ctx;
        this.classData = classData;
    }

    @Override
    public int getCount() {
        return classData.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.list_row, null, true);

        TextView className = row.findViewById(R.id.className);
        className.setText(classData.get(position).className);

        TextView teacherName = row.findViewById(R.id.teacherName);
        teacherName.setText(classData.get(position).teacherName);

        TextView grade = row.findViewById(R.id.classFinalGrade);
        grade.setText(String.valueOf(classData.get(position).grade) + "%");

        return row;

    }
}
