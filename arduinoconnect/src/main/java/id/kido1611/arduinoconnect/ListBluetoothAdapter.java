package id.kido1611.arduinoconnect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kido1611 on 20-Apr-16.
 */
public class ListBluetoothAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BluetoothDevice> mItems;

    public ListBluetoothAdapter(Context context, ArrayList<BluetoothDevice> items){
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder mViewHolder;
        if(convertView==null){
            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_bluetooth_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
            mViewHolder.mHWAddr = (TextView) convertView.findViewById(R.id.title_hw);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice mItem = mItems.get(i);
        mViewHolder.mTitle.setText(mItem.getName());
        mViewHolder.mHWAddr.setText(mItem.getAddress());

        return convertView;
    }

    static class ViewHolder{
        TextView mTitle, mHWAddr;
    }
}
