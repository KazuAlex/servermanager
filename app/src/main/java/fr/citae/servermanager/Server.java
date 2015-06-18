package fr.citae.servermanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class Server extends GlobalActivity {

  private String hostname = "";
  private String username = "";
  private String password = "";
  private JSch jSch = null;

  TextView output;
  EditText input;
  InputStream inputStream;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_server);

    hostname = getIntent().getStringExtra(HOSTNAME);
    username = getIntent().getStringExtra(USERNAME);
    password = getIntent().getStringExtra(PASSWORD);

    jSch = new JSch();

    new AsyncTask<Integer, Void, Void>() {

      @Override
      protected Void doInBackground(Integer... integers) {
        try {
          Session session = jSch.getSession(username, hostname, 22);
          session.setPassword(password);

          java.util.Properties config = new Properties();
          config.put("StrictHostKeyChecking", "no");
          session.setConfig(config);

          session.connect();

          Channel channel = session.openChannel("shell");
          output = (TextView) findViewById(R.id.output);
          input = (EditText) findViewById(R.id.input);

          OutputStream os = new OutputStream() {
            @Override
            public void write(int i) throws IOException {
              if (output != null) {
                output.setText(output.getText() + String.valueOf((char) i));
              }
            }
          };

          channel.setOutputStream(os);

        } catch (JSchException e) {
          Log.e("", e.toString());
          for (StackTraceElement ste : e.getStackTrace())
            Log.e("", ste.toString());
        }
        return null;
      }
    }.execute(1);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_server, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
