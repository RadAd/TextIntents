package au.radsoft.webintents;

import android.app.Activity;

import android.os.Bundle;

import android.content.Intent;
import android.content.ClipboardManager;
import android.content.ClipData;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import android.net.Uri;

import java.net.URLEncoder;

public class WebIntents extends Activity implements AdapterView.OnItemClickListener
{
    EditText text_;
    CharSequence subject_;
    ChoiceAdapter adapter_;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        enableActionBar();
        
        setContentView(R.layout.main);
        
        super.onCreate(savedInstanceState);
        
        text_ = (EditText) findViewById(R.id.text);
        ListView list = (ListView) findViewById(R.id.list);
        
        Intent intent = getIntent();
        if (intent != null && intent.getAction() == Intent.ACTION_SEND)
        {
            text_.setText(intent.getCharSequenceExtra(Intent.EXTRA_TEXT));
            subject_ = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT);
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
		
		ChoiceAdapter.init(this, false);
        adapter_ = new ChoiceAdapter(this);
        list.setAdapter(adapter_);
		list.setOnItemClickListener(this);
        registerForContextMenu(list);
		
		setFinishOnTouchOutside(true);
    }

    private void enableActionBar()
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        
        android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        LayoutParams params = getWindow().getAttributes(); 
        params.width = metrics.widthPixels/2;
        params.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
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
            adapter_.add();
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
        switch (item.getItemId())
        {
        case R.id.action_edit:
            adapter_.edit(info.position);
            break;
            
        case R.id.action_delete:
            adapter_.delete(info.position);
            break;
            
        case R.id.action_move_up:
            adapter_.moveUp(info.position);
            break;
            
        case R.id.action_move_down:
            adapter_.moveDown(info.position);
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
        try
        {
            CharSequence text = text_.getText();
            if (text != null)
                s = s.replace("[text]", URLEncoder.encode(text.toString()));
            if (text != null)
                s = s.replace("[rawtext]", text.toString());
            if (subject_ != null)
                s = s.replace("[subject]", URLEncoder.encode(subject_.toString()));
            
            Uri uri = Uri.parse(s);
            //toast(s);
        
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }
        catch (android.content.ActivityNotFoundException e)
        {
            toast("No activity found");
        }
    }
    
    void toast(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
