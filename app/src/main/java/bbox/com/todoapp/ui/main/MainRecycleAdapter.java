package bbox.com.todoapp.ui.main;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import bbox.com.todoapp.R;
import bbox.com.todoapp.models.Todo;

import java.util.ArrayList;
import java.util.List;


public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.MyViewHolder>
{
    private Context mContext;
        private List<Todo> mTodoList = new ArrayList<>();;

    private onRecyclerViewItemClickListener mItemClickListener;


   public MainRecycleAdapter(Context mContext) {
        this.mContext = mContext;    }


//region interface 1
    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position, Todo mBeanObj);
    }
//endregion



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
         TextView mTxtVwName, mTxtVwExpDate, mTxtVwStatus, mTxtVwDesc;
         ImageView btnEdit;// btnDelete;
        CheckBox chkBxComplete;
         CardView cardVw;



        public MyViewHolder(final View view)
        {
            super(view);

            cardVw = (CardView)view.findViewById(R.id.card_view);

            mTxtVwName = (TextView) view.findViewById(R.id.txtVw_Name);
            mTxtVwExpDate = (TextView) view.findViewById(R.id.txtVw_expireDate);
            mTxtVwStatus = (TextView) view.findViewById(R.id.txtVw_status);
            mTxtVwDesc = (TextView) view.findViewById(R.id.txtVw_ExpDate);

            btnEdit = (ImageView)view.findViewById(R.id.btn_Edit);
            chkBxComplete = (CheckBox) view.findViewById(R.id.chkBox_todoComplete);

            cardVw.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            chkBxComplete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
            {
                Todo _beanObj = mTodoList.get(getAdapterPosition());

                mItemClickListener.onItemClickListener(v, getAdapterPosition(), _beanObj);
            }
        }
    }


    @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_recyler_celllayout, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
      final Todo _beanObj = mTodoList.get(position);

            holder.mTxtVwName.setText(_beanObj.getName());

            holder.mTxtVwExpDate.setText(String.valueOf(_beanObj.getExpiry_date()));
            holder.mTxtVwStatus.setText(_beanObj.getStatus());
            holder.mTxtVwDesc.setText(_beanObj.getDescription());

            if(_beanObj.getStatus().toLowerCase().equals(("Completed").toLowerCase()))
                holder.chkBxComplete.setChecked(true);

            else
                holder.chkBxComplete.setChecked(false);


    } //end of onBindViewHolder



    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void setTodosInAdapter(List<Todo> todos){
        this.mTodoList = todos;
        notifyDataSetChanged();
    }

    public void notifyDeleted(int position)
    {
        mTodoList.remove(position);
        notifyItemRemoved(position);
    }





}
