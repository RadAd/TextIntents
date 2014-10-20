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

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

public class ChoiceAdapter extends BaseAdapter
{
    private static final String PREFERENCES = "au.radsoft.webintents.TEMPLATES";
    private static final String CHOICE_LIST = "_CHOICE_LIST";
    private static final String URL_PREFIX = "_URL_";
    
    public static void init(Context context, boolean reset)
    {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (reset || !sp.contains(CHOICE_LIST))
        {
            SharedPreferences.Editor spe = sp.edit();
            if (reset)
                spe.clear();
            spe.putString(URL_PREFIX + "Google", "https://www.google.com/?gws_rd=ssl#q=[text]");
            spe.putString(URL_PREFIX + "Dictionary", "https://dictionary.reference.com/browse/[text]");
            spe.putString(URL_PREFIX + "Wikipedia", "https://en.wikipedia.org/wiki/[text]");
			spe.putString(URL_PREFIX + "Add to Google Bookmarks", "https://www.google.com/bookmarks/mark?op=edit&output=popup&bkmk=[text]&title=[subject]");
            spe.putString(CHOICE_LIST, "Google,Dictionary,Wikipedia,Add to Google Bookmarks");
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
        
        if (url.startsWith("google.com/bookmarks", offset))
            return R.drawable.bookmark_add_icon;
		else if (url.startsWith("google.com", offset))
            return R.drawable.google_icon;
        else if (url.startsWith("dictionary.reference.com", offset))
            return R.drawable.dictionary_icon;
        else if (url.startsWith("wikipedia.org", offset))
            return R.drawable.wikipedia_icon;
        else if (url.startsWith("bkmks.com", offset))
            return R.drawable.bkmks_icon;
        else
            return R.drawable.internet_icon;
    }
    
    private Context context_;
    private LayoutInflater layoutInflater_;
    private SharedPreferences sp_;
    private List<String> labels_ = new java.util.ArrayList<String>();

    public ChoiceAdapter(Context context)
    {
        context_ = context;
        layoutInflater_ = LayoutInflater.from(context_);
        sp_ = context_.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        labels_.addAll(Arrays.asList(sp_.getString(CHOICE_LIST, "Google").split(",")));
    }

    private Context getContext()
    {
        return context_;
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
        final String label = labels_.get(position);
        return label.hashCode();
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
    
    public void moveUp(int position)
    {
        if (position > 0)
        {
            doSwap(position, position - 1);
        }
    }
    
    public void moveDown(int position)
    {
        if (position < (labels_.size() - 1))
        {
            doSwap(position, position + 1);
        }
    }
    
    private String labelsAsString()
    {
        StringBuilder sb = new StringBuilder();
        for (String s : labels_)
        {
            sb.append(',').append(s);
        }
        if (sb.length() == 0)
            return "";
        else
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
    
    public void doSwap(int p1, int p2)
    {
        final String label = labels_.get(p1);
        labels_.set(p1, labels_.get(p2));
        labels_.set(p2, label);
        
        SharedPreferences.Editor spe = sp_.edit();
        spe.putString(CHOICE_LIST, labelsAsString());
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
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        Toast.makeText(getContext(), R.string.error_label, Toast.LENGTH_LONG).show();
                    }
                    else if (newurl.isEmpty())
                    {
                        Toast.makeText(getContext(), R.string.error_url_template, Toast.LENGTH_LONG).show();
                    }
                    else if (isnew && labels_.indexOf(newlabel) != -1)
                    {
                        Toast.makeText(getContext(), R.string.error_label_exists, Toast.LENGTH_LONG).show();
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
        final String deleteQuery = getContext().getResources().getString(R.string.delete_query, label);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(deleteQuery);
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
