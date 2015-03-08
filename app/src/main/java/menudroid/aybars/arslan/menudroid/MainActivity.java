package menudroid.aybars.arslan.menudroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements OnClickListener, View.OnLongClickListener {

    Button btnOrder, btnBill, btnWaiter, btnMenu, btnContact, btnRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initalize buttons
        initialize();
    }

    private void initialize() {
        btnOrder = (Button) findViewById(R.id.btnOrder);
        btnBill = (Button) findViewById(R.id.btnBill);
        btnWaiter = (Button) findViewById(R.id.btnWaiter);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnContact = (Button) findViewById(R.id.btnContact);
        btnRestaurant = (Button) findViewById(R.id.btnRestaurant);

        //onClick Events
        btnOrder.setOnClickListener(this);
        btnBill.setOnClickListener(this);
        btnWaiter.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnRestaurant.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnOrder:
                showToast("Clicked Order");
                break;
            case R.id.btnBill:
                showToast("Clicked Bill");
                break;
            case R.id.btnWaiter:
                showToast("Clicked Waiter");
                break;
            case R.id.btnMenu:
                showToast("Clicked Menu");
                break;
            case R.id.btnContact:
                showToast("Clicked Contact");
                break;
            case R.id.btnRestaurant:
                showToast("Clicked Restaurant Test");
                break;
        }

    }

    Toast m_currentToast;

    void showToast(String text)
    {
        if(m_currentToast != null)
        {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        m_currentToast.show();

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btnOrder:
                showToast("Clicked Order");
                break;
        }
        return false;
    }
}