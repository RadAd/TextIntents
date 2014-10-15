package au.radsoft.textintents;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChoiceAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater_;

    public ChoiceAdapter(LayoutInflater li)
    {
        layoutInflater_ = li;
    }

    public int getCount()
    {
        return 3;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        String label;
        String url;
        int img;
        
        switch (position)
        {
        case 0:
            label = "Google";
            url = "https://www.google.com.au/?gws_rd=ssl#q=[text]";
            img = R.drawable.google_icon;
            break;
        case 1:
            label = "Dictionary";
            url = "https://dictionary.reference.com/browse/[text]";
            img = R.drawable.dictionary_icon;
            break;
        case 2:
            label = "Wikipedia";
            url = "https://en.wikipedia.org/wiki/[text]";
            img = R.drawable.wikipedia_icon;
            break;
        default:
            label = "Unknown";
            url = "[text]";
            img = R.drawable.internet_icon;
            // TODO Throw exception
            break;
        }
        
        View v = layoutInflater_.inflate(R.layout.item, parent, false);
        {
            TextView labelv = (TextView) v.findViewById(R.id.label);
            if (labelv != null)
            {
                labelv.setText(label);
            }
            TextView secondLinev = (TextView) v.findViewById(R.id.secondLine);
            if (secondLinev != null)
            {
                secondLinev.setText(url);
            }
            ImageView iconv = (ImageView) v.findViewById(R.id.icon);
            if (iconv!= null)
            {
                iconv.setImageResource(img);
            }
            v.setTag(url);
        }
        return v;
    }
}
