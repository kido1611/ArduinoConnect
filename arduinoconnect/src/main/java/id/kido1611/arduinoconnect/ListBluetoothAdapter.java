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
    private ArrayList<BluetoothItem> mItems;

    public ListBluetoothAdapter(Context context, ArrayList<BluetoothItem> items){
        mContext = context;
        mItems = items;
    }

    public boolean isAvailable(BluetoothDevice device){
        if(device==null) return true;
        BluetoothDevice currDevice;
        boolean found = false;
        int i=0;
        while(i<mItems.size() && !found){
            currDevice = mItems.get(i).getDevice();
            if(currDevice!=null) {
                if (currDevice.getAddress().equals(device.getAddress()))
                    found = true;
            }
            i++;
        }
        return found;
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

        BluetoothItem item = mItems.get(i);

        ViewHolder mViewHolder;
        if(convertView==null){
            LayoutInflater mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mViewHolder = new ViewHolder();
//            if(item.isCategory()){
//                convertView = mInflater.inflate(R.layout.layout_bluetooth_category_item, null);
//            }else{
//                convertView = mInflater.inflate(R.layout.layout_bluetooth_item, null);
//            }
            convertView = mInflater.inflate(R.layout.layout_bluetooth_item, null);
            mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
            mViewHolder.mHWAddr = (TextView) convertView.findViewById(R.id.title_hw);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

//        if(item.isCategory()){
//            mViewHolder.mTitle.setText(item.getName());
//        }else{
//            BluetoothDevice mItem = mItems.get(i).getDevice();
//            mViewHolder.mTitle.setText(mItem.getName());
//            mViewHolder.mHWAddr.setText(mItem.getAddress());
//        }
        BluetoothDevice mItem = mItems.get(i).getDevice();
        mViewHolder.mTitle.setText(mItem.getName());
        mViewHolder.mHWAddr.setText(mItem.getAddress());

        if(item.isPaired()) mViewHolder.mTitle.setText(mViewHolder.mTitle.getText()+" (Paired)");

        return convertView;
    }

    static class ViewHolder{
        TextView mTitle, mHWAddr;
    }
}
