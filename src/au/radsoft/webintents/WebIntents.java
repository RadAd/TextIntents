package au.radsoft.webintents;

import android.app.Activity;

import android.content.Intent;
import android.content.ClipboardManager;
import android.content.ClipData;

import android.net.Uri;

import android.os.Bundle;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URLEncoder;

public class WebIntents extends Activity implements AdapterView.OnItemClickListener
{
    EditText text_;
    ListView list_;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //boolean customTitle = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //boolean customIcon = requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.main);
        //if (customTitle)
        //{
            //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        //}
        //if (customIcon)
        //{
            //getWindow().setFeatureInt(Window.FEATURE_LEFT_ICON, R.drawable.wikipedia_icon);
        //}
        
        text_ = (EditText) findViewById(R.id.text);
        list_ = (ListView) findViewById(R.id.list);
        
        Intent intent = getIntent();
        if (intent != null && intent.getAction() == Intent.ACTION_SEND)
        {
            text_.setText(intent.getCharSequenceExtra(Intent.EXTRA_TEXT));
        }
        else
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip())
            {
                ClipData data = clipboard.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0); // TODO Check all items
                text_.setText(item.coerceToText(this).toString());
            }
        }
        
        ChoiceAdapter.init(getPreferences(MODE_PRIVATE));
        final ChoiceAdapter adapter = new ChoiceAdapter(getLayoutInflater(), getPreferences(MODE_PRIVATE));
        list_.setAdapter(adapter);
		list_.setOnItemClickListener(this);
        registerForContextMenu(list_);
		
		setFinishOnTouchOutside(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.action_add:
            ChoiceAdapter adapter = (ChoiceAdapter) list_.getAdapter();
            adapter.add();
            break;
            
        default:
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item, menu);

        super.onCreateContextMenu(menu, view, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ChoiceAdapter adapter = (ChoiceAdapter) list_.getAdapter();
        switch (item.getItemId())
        {
        case R.id.action_edit:
            adapter.edit(info.position);
            break;
            
        case R.id.action_delete:
            adapter.delete(info.position);
            break;
            
        case R.id.action_move_up:
            adapter.moveUp(info.position);
            break;
            
        case R.id.action_move_down:
            adapter.moveDown(info.position);
            break;
            
        default:
            return false;
        }
        
        return true;
    }
    
    @Override // AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        open(view.getTag().toString());
    }
    
    void open(String s)
    {
        CharSequence text = text_.getText();
        if (text != null)
            s = s.replace("[text]", URLEncoder.encode(text.toString()));
            
        Uri uri = Uri.parse(s);
        toast(s);
        
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }
    
    void toast(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
