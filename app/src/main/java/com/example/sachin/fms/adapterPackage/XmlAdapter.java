package com.example.sachin.fms.adapterPackage;

/**
 * Created by Sachin on 24,September,2016
 * Hawkinski,
 * Dubai, UAE.
 */
public class XmlAdapter /*extends RecyclerView.Adapter<XmlAdapter.XmlHolder>*/ {

   /* List<XmlData> data= Collections.emptyList();
    Context context;
    SetOnItemClick click;
    public XmlAdapter (Context context,List<XmlData> data){
        this.context=context;
        this.data=data;

    }



    @Override
    public XmlHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list,parent,false);
        XmlHolder holder= new XmlHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(XmlHolder holder, int position) {

        holder.name.setText(data.get(position).name);
        holder.quantity.setText(data.get(position).quantity);
        holder.color.setText(data.get(position).color);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class XmlHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView name,color,quantity;
        public XmlHolder(View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.name);
            color=(TextView)itemView.findViewById(R.id.color);
            quantity=(TextView)itemView.findViewById(R.id.quantity);

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {

            if(click!= null){
                click.SetOnItemClick(v,getAdapterPosition());
                }

            }
        }



    public interface SetOnItemClick{
        void SetOnItemClick(View v,int position);
    }*/
}
