package au.radsoft.textintents;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.SharedPreferences;

public class ChoiceAdapter extends BaseAdapter
{
    private static final String CHOICE_LIST = "_CHOICE_LIST";
    private static final String URL_PREFIX = "_URL_";
    
    public static void init(SharedPreferences sp)
    {
        if (!sp.contains(CHOICE_LIST))
        {
            SharedPreferences.Editor spe = sp.edit();
            //spe.clear();
            spe.putString(URL_PREFIX + "Google", "https://www.google.com/?gws_rd=ssl#q=[text]");
            spe.putString(URL_PREFIX + "Dictionary", "https://dictionary.reference.com/browse/[text]");
            spe.putString(URL_PREFIX + "Wikipedia", "https://en.wikipedia.org/wiki/[text]");
            spe.putString(CHOICE_LIST, "Google,Dictionary,Wikipedia");
            spe.commit();
        }
    }
    
    private static int getImgId(String url)
    {
        int offset = 0;
        String[] b = { "http://", "https://", "www.", "en." };
        for (String i : b)
        {
            if (url.startsWith(i, offset))
                offset += i.length();
        }
        
        if (url.startsWith("google.com", offset))
            return R.drawable.google_icon;
        else if (url.startsWith("dictionary.reference.com", offset))
            return R.drawable.dictionary_icon;
        else if (url.startsWith("wikipedia.org", offset))
            return R.drawable.wikipedia_icon;
        else
            return R.drawable.internet_icon;
    }
    
    private LayoutInflater layoutInflater_;
    private SharedPreferences sp_;
    private String[] labels_;

    public ChoiceAdapter(LayoutInflater li, SharedPreferences sp)
    {
        layoutInflater_ = li;
        sp_ = sp;
        labels_ = sp_.getString(CHOICE_LIST, "Google").split(",");
    }

    public int getCount()
    {
        return labels_.length;
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
        final String label = labels_[position];
        final String url = sp_.getString(URL_PREFIX + label, "");
        final int img = getImgId(url);
        
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
