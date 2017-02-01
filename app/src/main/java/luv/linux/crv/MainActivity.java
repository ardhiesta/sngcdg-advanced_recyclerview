package luv.linux.crv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv_worksite)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    StringRequest stringRequest;
    RequestQueue mRequestQueue;
    private List<Worksite> worksites;
    public static final String TAG = "tag";
    LinearLayoutManager llm;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    RVAdapter adapter;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        worksites = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        recyclerView.setHasFixedSize(true);

        //inisialisasi layout RecyclerView
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
//                            Toast.makeText(MainActivity.this, "Last item reached", Toast.LENGTH_SHORT).show();
                            //here you can do the pagination code to request for new set of records
                            loadData(totalItemCount);
                        }
                    }
                }
            }
        });

        adapter = new RVAdapter(worksites);
        recyclerView.setAdapter(adapter);

        loadData(0);
    }

    private void loadData(int offset) {
        progressBar.setVisibility(View.VISIBLE);
        stringRequest = new StringRequest(Request.Method.GET, getServiceUrl(String.valueOf(offset)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("----------- response: " + response);
                        System.out.println("");

                        progressBar.setVisibility(View.GONE);

//                        worksites.add(null);
//                        adapter.notifyItemInserted(worksites.size() - 1);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Worksite worksite = new Worksite(jsonArray.getJSONObject(i).optString("id_rating"),
                                        jsonArray.getJSONObject(i).optString("id_user"),
                                        Integer.parseInt(jsonArray.getJSONObject(i).optString("service")),
                                        jsonArray.getJSONObject(i).optString("rooms"));
                                worksites.add(worksite);
                                adapter.notifyItemInserted(worksites.size() - 1);
                            }
//                            pb_worksite2.setVisibility(View.GONE);
                            loading = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println("---------- itemCount: " + recyclerView.getAdapter().getItemCount());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }

        });

        // Set the tag on the request.
        stringRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);
    }

    private String getServiceUrl(String offset) {
        String url = "http://192.168.56.1:8080/rating?starts=" + offset + "&total=7";
        return url;
    }

    class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Worksite> worksites;

        public RVAdapter(List<Worksite> worksites) {
            this.worksites = worksites;
        }

        @Override
        public int getItemViewType(int position) {
            return worksites.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            if (viewType == VIEW_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_worksite, parent, false);
                vh = new WorksiteViewHolder(view);
            } else {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_progressbar, parent, false);
//                vh = new ProgressViewHolder(view);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof WorksiteViewHolder) {
                ((WorksiteViewHolder) holder).worksiteName.setText(worksites.get(position).getName());
                ((WorksiteViewHolder) holder).worksiteAddress.setText(worksites.get(position).getAddress());
                ((WorksiteViewHolder) holder).worksiteWorker.setText(String.valueOf(worksites.get(position).getWorkerAssigned()));
                ((WorksiteViewHolder) holder).worksiteStatus.setText(worksites.get(position).getStatus());
            } else {
//                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return worksites.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

//    static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.pb_worksite)
//        ProgressBar progressBar;
//
//        public ProgressViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }

    static class WorksiteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv)
        CardView cv;

        @BindView(R.id.worksite_name)
        TextView worksiteName;

        @BindView(R.id.worksite_address)
        TextView worksiteAddress;

        @BindView(R.id.worksite_worker_assigned)
        TextView worksiteWorker;

        @BindView(R.id.worksite_status)
        TextView worksiteStatus;

        public WorksiteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
