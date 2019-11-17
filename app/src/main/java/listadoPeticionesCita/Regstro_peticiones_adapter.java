package listadoPeticionesCita;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fumigacionesmoncada.R;

import java.util.ArrayList;

public class Regstro_peticiones_adapter extends ArrayAdapter<Citas_Peticiones> {
    private ListView lista_citas;

    private ArrayList<Citas_Peticiones> usuarios_cita;
    public Regstro_peticiones_adapter(@NonNull Context context, ArrayList<Citas_Peticiones>lista_citas) {
        super(context, R.layout.item_lista_citas_peticiones_clientes,lista_citas);
        this.usuarios_cita = lista_citas;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_citas_peticiones_clientes, null, false);


        TextView fecha = convertView.findViewById(R.id.mostrarfecha);
        TextView hora = convertView.findViewById(R.id.mostrarcita);
        TextView estado = convertView.findViewById(R.id.mosstarestado);
        TextView direccion = convertView.findViewById(R.id.mosstardireccion);
        RelativeLayout relativeLayout1=convertView.findViewById(R.id.relativeLayout1);


        Citas_Peticiones citas = getItem(position);


        direccion.setText(citas.getDireccion());
        fecha.setText(citas.getFecha());
        hora.setText(citas.getHora());


        estado.setText(citas.getEstado());
        if(estado.getText() == "Pendiente"){

            relativeLayout1.setBackgroundColor(Color.parseColor("#81D4FA"));
        }else{
        if(estado.getText() == "Aceptado"){

            relativeLayout1.setBackgroundColor(Color.parseColor("#C5E1A5"));
        } else{if(estado.getText() == "Cancelado"){

            relativeLayout1.setBackgroundColor(Color.parseColor("#FFCC80"));
        }else{
        if(estado.getText() == "Rechazado"){

            relativeLayout1.setBackgroundColor(Color.parseColor("#FFAB91"));
        }}}}

        return convertView;

    }
    @Override
    public int getCount() {
        return usuarios_cita.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Citas_Peticiones getItem(int position) {
        return usuarios_cita.get(position);
    }

    public void filtrar(ArrayList<Citas_Peticiones> permisosgetYset) {
        this.usuarios_cita = new ArrayList<>();
        this.usuarios_cita.addAll(permisosgetYset);
        notifyDataSetChanged();


    }
}
