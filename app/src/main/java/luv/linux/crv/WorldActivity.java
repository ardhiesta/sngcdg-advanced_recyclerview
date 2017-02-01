package luv.linux.crv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import luv.linux.crv.model.Contact;
import luv.linux.crv.utils.ContactsAdapter;
import luv.linux.crv.utils.EndlessRecyclerViewScrollListener;

public class WorldActivity extends AppCompatActivity {
    @BindView(R.id.rv_world)
    RecyclerView rvWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world);
        ButterKnife.bind(this);

        final List<Contact> allContacts = Contact.createContactsList(10, 0);
        final ContactsAdapter adapter = new ContactsAdapter(allContacts);
        rvWorld.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvWorld.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                List<Contact> moreContacts = Contact.createContactsList(10, page);
                final int curSize = adapter.getItemCount();
                allContacts.addAll(moreContacts);

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeInserted(curSize, allContacts.size() - 1);
                    }
                });
            }
        };
        rvWorld.addOnScrollListener(scrollListener);
    }

}
