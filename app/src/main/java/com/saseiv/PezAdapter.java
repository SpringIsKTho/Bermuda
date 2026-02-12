package com.saseiv;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class PezAdapter extends RecyclerView.Adapter<PezAdapter.ViewHolder> {

    private List<Pez> lista;
    private Context context;

    public PezAdapter(Context context, List<Pez> lista) {
        this.context = context;
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre;

        public ViewHolder(View view) {
            super(view);
            imagen = view.findViewById(R.id.imgPez);
            nombre = view.findViewById(R.id.txtNombre);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pez pez = lista.get(position);

        holder.nombre.setText(pez.getNombre());

        int radius = (int) (16 * context.getResources().getDisplayMetrics().density);

        Glide.with(context)
                .load(pez.getImagen_url())
                .transform(new CenterCrop(), new RoundedCorners(radius))
                .into(holder.imagen);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallePezActivity.class);
            intent.putExtra("nombre", pez.getNombre());
            intent.putExtra("descripcion", pez.getDescripcion());
            intent.putExtra("imagen", pez.getImagen_url());
            intent.putExtra("audio", pez.getAudio_url());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
