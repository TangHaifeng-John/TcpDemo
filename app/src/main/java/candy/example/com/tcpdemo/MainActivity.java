package candy.example.com.tcpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SocketChannelClient socketChannelClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socketChannelClient =new SocketChannelClient("127.0.0.1",3838);
        socketChannelClient.start();
    }
}
