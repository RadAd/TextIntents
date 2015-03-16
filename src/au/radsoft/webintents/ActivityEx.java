package au.radsoft.webintents;

import android.app.Activity;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

// This class was created because I could not find a way to use an ActionBar in a Dialog app.
// This overrides the OptionsMenu display to be a button on the title to make it more discoverable.

public class ActivityEx extends Activity
{
    private View menubutton_;
    private TextView title_;
    private ImageView left_icon_;
    
    public void showMenu(View v)
    {
        openOptionsMenu();
    }
    
    @Override
    public void setContentView(int layoutResID)
    {
        boolean customTitle = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //boolean customIcon = requestWindowFeature(Window.FEATURE_LEFT_ICON);
        
        super.setContentView(layoutResID);
        
        if (customTitle)
        {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_dialog_title);
        }
        //if (customIcon)
        //{
            //getWindow().setFeatureInt(Window.FEATURE_LEFT_ICON, R.drawable.wikipedia_icon);
        //}
        
        //View titleContainer = findViewById(android.R.id.title_container);
        menubutton_ = findViewById(R.id.custom_menu_button);
        title_ = (TextView) findViewById(R.id.custom_title);
        left_icon_ = (ImageView) findViewById(R.id.custom_left_icon);
        
        title_.setText(getTitle());
        left_icon_.setImageDrawable(getPackageManager().getApplicationIcon(getApplicationInfo()));
    }
    
    // NOTE: When using FEATURE_CUSTOM_TITLE, the Window no longer has access to the TextView containing the title.
    // I have not discovered any means to reset it so for now we override the title functions.
    
    @Override
    public void setTitle(int title)
    {
        super.setTitle(title);
        title_.setText(title);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        title_.setText(title);
    }
    
    @Override
    public void openOptionsMenu()
    {
        PopupMenu popup = new PopupMenu(this, menubutton_);
        onCreateOptionsMenu(popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onOptionsItemSelected(item);
                }
                
            });
        popup.show();
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            openOptionsMenu();
            
            return true;
        }
        else
        {
            return super.onKeyUp(keyCode, event);
        }
    }
}
