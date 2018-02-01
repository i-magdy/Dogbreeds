package breeds.dog.stratos.dogbreeds;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import breeds.dog.stratos.dogbreeds.utilities.JsonUtils;
import breeds.dog.stratos.dogbreeds.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements BreedsAdapter.BreedsAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String> {
    final static String ALL_BREEDS_URL = "https://dog.ceo/api/breeds/list";
    final static String BREED_IMAGES_URL = "https://dog.ceo/api/breed/?/images";

    private static final int BREEDS_LOADER_ID = 3;

    private List<String> breedsList = new ArrayList<>();
    private BreedsAdapter breedsAdapter;
    private EditText searchBreed;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private URL breedsNamesURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        breedsAdapter = new BreedsAdapter(breedsList, this);
        searchBreed = (EditText) findViewById(R.id.search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(breedsAdapter);
    }

    private String prepareUrl(String urlString, String param) {
        String result = urlString;
        if (urlString.contains("?")) {
            result = urlString.replace("?", param);
        }
        return result;
    }

    private void loadImageOnActivity(String breedName) {
        String urlString = prepareUrl(BREED_IMAGES_URL, breedName);
        URL breedImagesURL = NetworkUtils.buildUrl(urlString);
        String result = "";

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        try {
            result = myAsyncTask.execute(breedImagesURL).get();
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "problem", Toast.LENGTH_LONG).show();
        }

        List<String> results = new ArrayList<String>();
        results.addAll(JsonUtils.parseJSON(result));

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("url", results.get(0));
        startActivity(intent);
    }

    @Override
    public void onClick(String breedName) {
        loadImageOnActivity(breedName);
    }

    private void loadData(String urlString) {
        Bundle bundle = new Bundle();
        bundle.putString("urlString", urlString);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> breedsLoader = loaderManager.getLoader(BREEDS_LOADER_ID);
        if (breedsLoader == null) {
            loaderManager.initLoader(BREEDS_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(BREEDS_LOADER_ID, bundle, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            if (TextUtils.isEmpty(searchBreed.getText())) {
                loadData(ALL_BREEDS_URL);
            } else {
                loadImageOnActivity(searchBreed.getText().toString());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                progressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String b = bundle.getString("urlString");
                try {
                    URL breedsNamesURL = NetworkUtils.buildUrl(bundle.getString("urlString"));
                    String breedsData = NetworkUtils.getResponseFromHttpUrl(breedsNamesURL);
                    return breedsData;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String breedsData) {
        progressBar.setVisibility(View.INVISIBLE);
        if (breedsData != null && !breedsData.equals("")) {
            breedsList.addAll(JsonUtils.parseJSON(breedsData));
            //Toast.makeText(MainActivity.this, breedsData, Toast.LENGTH_LONG).show();
            breedsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(MainActivity.this, "problem", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.options, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    public class MyAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            try {
                String breedImages = NetworkUtils.getResponseFromHttpUrl(params[0]);
                return breedImages;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String breedsData) {
            if (breedsData != null && !breedsData.equals("")) {
                //Toast.makeText(MainActivity.this, breedsData, Toast.LENGTH_LONG).show();
                //breedsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "problem", Toast.LENGTH_LONG).show();
            }
        }
    }
}