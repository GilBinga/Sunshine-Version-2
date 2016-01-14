package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        // ArrayAdapter<String> criado para popular a ListView
        private ArrayAdapter<String> mForecastAdapter;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //Criar alguns dados fakes para popular a ListView
            String[] forecastArray = {
                    "Today - Sunny - 88/63",
                    "Tomorrow - Foggy - 70/46",
                    "Weds - Cloudy - 72/63",
                    "Thurs - Rainy - 64/51",
                    "Fri - Foggy - 70/46",
                    "Sat - Sunny - 76/68"
            };

            //Cria um ArrayList com os dados da String[]
            List<String> weekForecast = new ArrayList<>(
                    Arrays.asList(forecastArray));

            //Cria o ArrayAdapter<String>
            mForecastAdapter = new ArrayAdapter<>(
                    //context
                    getActivity(),
                    //ID da layout da lista de item
                    R.layout.list_item_forecast,
                    //ID da TextView para popular
                    R.id.list_item_forecast_text_view,
                    //Dados
                    weekForecast
            );

            //Cria referência para a ListView
            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

            //Anexa o adpter ao ListView
            listView.setAdapter(mForecastAdapter);

            //Implementação da comunicação HTTP
            //Declaração de variáveis fora do try/catch para serem fechadas no bloco finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Vai conter a linha resposta JSON como uma String
            String forecastJsonStr = null;

            try {
                //construi a URL para o OpenWheatherMap
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?id=3467747&mode=json&units=metric&cnt=7");

                //Cria o request para o OpenWheaterMap e abre a conexão
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Cria e grava a InputStream em uma String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    //Nada a fazer
                    forecastJsonStr = null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    //Fazendo isso para ficar mais fácil o debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //Stream vazio
                    forecastJsonStr = null;
                }

                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error", e);
                //Se o código não conseguiu pegar o dado corretamente, garante que a variável
                //forecastJsonStr fica em null
                forecastJsonStr = null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }

                }
            }



            return rootView;
        }
    }
}
