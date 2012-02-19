package com.yoanaydavid.recetas;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yoanaydavid.recetas.R;
import com.yoanaydavid.recetas.java.RecetasMode;


public class RecetasModeAdapter extends BaseAdapter {
	private Context context;

    private List<RecetasMode> modeList;

    public RecetasModeAdapter(Context context, List<RecetasMode> modeList) {
        this.context = context;
        this.modeList = modeList;
    }
	@Override
	public int getCount() {
		
		return modeList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return modeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RecetasMode entry = modeList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recetas_mode, null);
        }
        

        TextView titulo = (TextView) convertView.findViewById(R.id.txtRecetasModeTitulo);
        titulo.setText(entry.getTitulo());

        TextView texto = (TextView) convertView.findViewById(R.id.txtRecetasModeTexto);
        texto.setText(entry.getTexto());

        
        

        return convertView;
	}

}
