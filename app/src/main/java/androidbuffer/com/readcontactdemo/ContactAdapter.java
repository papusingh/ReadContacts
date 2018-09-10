package androidbuffer.com.readcontactdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by incred-dev on 6/7/18.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{

    private List<ContactModel> contactModelList;

    ContactAdapter(List<ContactModel> contactModelList) {
        this.contactModelList = contactModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ContactModel model = contactModelList.get(position);
        if (model != null){
            if (model.getName() != null){
                holder.name.setText(model.getName());
            }

            if (model.getNumber() != null){
                StringBuffer buffer = new StringBuffer();
                for (String number:model.getNumber()){
                    buffer.append(number).append("\n");
                }
                holder.number.setText(buffer);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;

        MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            number = itemView.findViewById(R.id.tvNumber);
        }
    }
}
