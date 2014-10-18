package au.radsoft.webintents;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

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
    private List<String> labels_ = new java.util.ArrayList<String>();

    public ChoiceAdapter(LayoutInflater li, SharedPreferences sp)
    {
        layoutInflater_ = li;
        sp_ = sp;
        labels_.addAll(Arrays.asList(sp_.getString(CHOICE_LIST, "Google").split(",")));
    }

    public int getCount()
    {
        return labels_.size();
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
        final String label = labels_.get(position);
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
    
    public void add()
    {
        doEditDialog(R.string.add, null);
    }
    
    public void edit(int position)
    {
        final String label = labels_.get(position);
        doEditDialog(R.string.edit, label);
    }
    
    public void delete(int position)
    {
        final String label = labels_.get(position);
        doDeleteDialog(label);
    }
    
    private String labelsAsString()
    {
        StringBuilder sb = new StringBuilder();
        for (String s : labels_)
        {
            sb.append(',').append(s);
        }
        return sb.substring(1);
    }
    
    private void doDelete(String label)
    {
        int i = labels_.indexOf(label);
        if (i != -1)
            labels_.remove(i);
        
        SharedPreferences.Editor spe = sp_.edit();
        spe.putString(CHOICE_LIST, labelsAsString());
        spe.remove(URL_PREFIX + label);
        spe.commit();
        
        notifyDataSetChanged();
    }
    
    private void doUpdate(String label, String newlabel, String newurl)
    {
        boolean isnew = !newlabel.equals(label);
        
        if (isnew)
        {
            int i = labels_.indexOf(label);
            if (i == -1)
                labels_.add(newlabel);
            else
                labels_.set(i, newlabel);
        }
        
        SharedPreferences.Editor spe = sp_.edit();
        if (isnew)
        {
            spe.putString(CHOICE_LIST, labelsAsString());
            spe.remove(URL_PREFIX + label).commit();
        }
        spe.putString(URL_PREFIX + newlabel, newurl);
        spe.commit();
        
        notifyDataSetChanged();
    }
    
    private void doEditDialog(int title, final String label)
    {
        String url = sp_.getString(URL_PREFIX + label, "");
        View v = layoutInflater_.inflate(R.layout.edit, null);
        final EditText labelv = (EditText) v.findViewById(R.id.label);
        final EditText urlv = (EditText) v.findViewById(R.id.url);
        
        if (label != null)
            labelv.setText(label);
        urlv.setText(url);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater_.getContext());
        builder.setTitle(title);
        //builder.setMessage(url);
        builder.setView(v);
		//builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog ad = builder.show();
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    String newlabel = labelv.getText().toString();
                    String newurl = urlv.getText().toString();
                    boolean isnew = !newlabel.equals(label);
                    if (newlabel.isEmpty())
                    {
                        Toast.makeText(layoutInflater_.getContext(), "Please enter a label.", Toast.LENGTH_LONG).show();
                    }
                    else if (newurl.isEmpty())
                    {
                        Toast.makeText(layoutInflater_.getContext(), "Please enter a url.", Toast.LENGTH_LONG).show();
                    }
                    else if (isnew && labels_.indexOf(newlabel) != -1)
                    {
                        Toast.makeText(layoutInflater_.getContext(), "The label already exists.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        doUpdate(label, newlabel, newurl);
                        ad.dismiss();
                    }
                }
            });
    }
    
    private void doDeleteDialog(final String label)
    {
        final String deleteQuery = layoutInflater_.getContext().getResources().getString(R.string.delete_query);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(layoutInflater_.getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(String.format(deleteQuery, label));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    doDelete(label);
                }
            });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }
}
