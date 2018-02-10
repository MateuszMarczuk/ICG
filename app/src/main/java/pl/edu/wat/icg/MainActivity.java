package pl.edu.wat.icg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import pl.edu.wat.icg.common.ICGFileParsedDto;
import pl.edu.wat.icg.common.client.AmuletRestClient;
import pl.edu.wat.icg.common.file.ICGFileDto;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textInfo);
        ListView listView = findViewById(R.id.listView);
        ArrayList<String> parameters = new ArrayList<>();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            final InputStream inputStream = getResources().openRawResource(R.raw.testpm);
            //tworzenie pliku
            ICGFileDto fileDto = null;

            try {
                //zapisanie zawartości pliku do tablicy bajtów
                byte[] fileContent = new byte[inputStream.available()];
                inputStream.read(fileContent);

                fileDto = new ICGFileDto("icg_mateusz_marczuk.txt", fileContent);
                ICGFileParsedDto icgFileParsedDto = ICGFileParsedDto.fromICGFileDto(fileDto);

                for (String s : icgFileParsedDto.getParameterNames()) {
                    try {
                        parameters.add(s + " " + icgFileParsedDto.getParameterValue(s));
                    } catch (Exception e) {
                        System.err.println("WYJATEK DLA " + s);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row, parameters);
            listView.setAdapter(adapter);

            final ICGFileDto finalFileDto = fileDto;
            new Thread(() -> {
                try {
                    runOnUiThread(() -> textView.setText("Wysyłanie"));

                    AmuletRestClient restClient = AmuletRestClient.fromConfiguration();
                    restClient.sendIcgFile(finalFileDto);

                    runOnUiThread(() -> textView.setText("Wysłano"));
                } catch (Exception e) {
                    runOnUiThread(() -> textView.setText("Wystąpił błąd połączenia."));
                    e.printStackTrace();
                }
            }).start();
        });

    }
}
