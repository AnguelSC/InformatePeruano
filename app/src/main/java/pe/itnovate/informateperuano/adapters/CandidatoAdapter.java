package pe.itnovate.informateperuano.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pe.itnovate.informateperuano.Entities.Candidato;
import pe.itnovate.informateperuano.R;

/**
 * Created by Angel Sirlopu C on 06/12/2015.
 */
public class CandidatoAdapter extends ArrayAdapter<Candidato> {

    private LayoutInflater mInflater;
    private List<Candidato> contracts;
    private int mResource;
    public CandidatoAdapter(Context context, int resource, List<Candidato> candidatos) {
        super(context, resource, candidatos);
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ){
        View view = convertView == null ? mInflater.inflate( mResource, parent, false ) : convertView;
        Candidato candidato = getItem(position);
        ImageView imageVIew = (ImageView) view.findViewById(R.id.imageView2);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        TextView porcentaje = (TextView) view.findViewById(R.id.porcentaje_text);
        TextView votos = (TextView) view.findViewById(R.id.votos_text);
        Picasso.with(getContext())
                .load(candidato.getFoto())
                .into(imageVIew);
        textView.setText(candidato.getApellidos() + " " + candidato.getNombres());
        porcentaje.setText(candidato.getPopularidad()+"");
        votos.setText(candidato.getVotos()+"");
        return view;
    }
}
