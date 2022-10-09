package com.darpan.project.veggiesadmin.activity.product;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.darpan.project.veggiesadmin.R;
import com.darpan.project.veggiesadmin.adapter.product.ProductListAdapter;
import com.darpan.project.veggiesadmin.firebaseModal.ProductModalForeSale;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.darpan.project.veggiesadmin.constant.Constants.CATEGORY_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.LIMIT;
import static com.darpan.project.veggiesadmin.constant.Constants.NOT_VISIBLE;
import static com.darpan.project.veggiesadmin.constant.Constants.NO_ID;
import static com.darpan.project.veggiesadmin.constant.Constants.NO_ID_VALUE;
import static com.darpan.project.veggiesadmin.constant.Constants.O_F_S;
import static com.darpan.project.veggiesadmin.constant.Constants.PAGE_COUNT;
import static com.darpan.project.veggiesadmin.constant.Constants.PRODUCT_LIST_COLLECTION;
import static com.darpan.project.veggiesadmin.constant.Constants.UNIQUE_PID;
import static com.darpan.project.veggiesadmin.constant.Constants.VISIBLE;
import static com.darpan.project.veggiesadmin.constant.Constants.ZERO_DISC;
import static com.darpan.project.veggiesadmin.constant.Constants.ZERO_PRICE;
import static com.darpan.project.veggiesadmin.constant.Constants.categoryName;
import static com.darpan.project.veggiesadmin.constant.Constants.isPublished;
import static com.darpan.project.veggiesadmin.constant.Constants.productId;
import static com.darpan.project.veggiesadmin.constant.Constants.productImage;

public class ProductListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ProductListAdapter.OnItemClick {
    private ProductListAdapter productListAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "ProductListActivity:";
    private ProgressDialog pd;
    private String name = "Default";
    private Spinner productFilter;
    String filterOption = "All";
    private ArrayList<String> filterOptionStrings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        getSupportActionBar().setTitle("Product List");
        pd = new ProgressDialog(this);

        recyclerView = findViewById(R.id.product_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        productFilter = findViewById(R.id.product_spinner);
      /*  fillFilterOption(filterOptionStrings);*/

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                new String[]{filterOption//all
                        , VISIBLE
                        , NO_ID,
                        ZERO_PRICE,
                        NOT_VISIBLE,
                        O_F_S});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productFilter.setAdapter(statusAdapter);

        productFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getData(parent.getSelectedItem().toString().trim());
                Log.d(TAG, "onItemSelected: =" + parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getData(name);


    }

    private void fillFilterOption(ArrayList<String> filterOptionStrings) {
        filterOptionStrings.add(filterOption);
        filterOptionStrings.add(VISIBLE);
        filterOptionStrings.add(NO_ID);
        filterOptionStrings.add(ZERO_PRICE);
        filterOptionStrings.add(NOT_VISIBLE);
        filterOptionStrings.add(O_F_S);

        CollectionReference cateGorRef =
                FirebaseFirestore.getInstance()
                        .collection(CATEGORY_COLLECTION);

        cateGorRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot ds: queryDocumentSnapshots) {
                        if (ds.exists()){
                            filterOptionStrings.add(ds.getString(categoryName));
                        }
                    }
                }

            }
        });


    }


    private void setTitle(String s) {
        getSupportActionBar().setTitle(String.format("Total Item: %s", s));
    }

    private void getData(String name) {
        Query query;
        CollectionReference productListRef = firebaseFirestore.
                collection(PRODUCT_LIST_COLLECTION);

        Log.d(TAG, "uf8ff: " + name + "\uf8ff");


        switch (name) {
            case "Default":
                query = productListRef.
                        whereEqualTo("productName", name)
                        .limit(LIMIT);

                break;
            case NO_ID:
                query = productListRef.
                        whereEqualTo(UNIQUE_PID, NO_ID_VALUE)
                        .limit(LIMIT);

                break;
            case ZERO_DISC:
                query = productListRef.
                        whereEqualTo("productDiscount", 0)
                        .limit(LIMIT);
                break;
            case ZERO_PRICE:
                query = productListRef.
                        whereEqualTo("productPrice", 0)
                        .limit(LIMIT);
                break;
            case VISIBLE:
                query = productListRef.
                        whereEqualTo(isPublished, true)
                        .limit(LIMIT);
                break;

            case NOT_VISIBLE:
                query = productListRef.
                        whereEqualTo(isPublished, false)
                        .limit(LIMIT);
                break;

            case O_F_S:
                query = productListRef.
                        whereLessThanOrEqualTo("stockQuantity", 1)
                        .limit(LIMIT);
                break;

            default:
                query = productListRef.orderBy("productName").
                        startAt(name.toUpperCase()).
                        endAt(name.toLowerCase() + "\uf8ff");
                break;
        }


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_COUNT)
                .setPageSize(PAGE_COUNT)
                .build();

        FirestorePagingOptions<ProductModalForeSale> options =
                new FirestorePagingOptions.Builder<ProductModalForeSale>()
                        .setQuery(query, config, snapshot -> {
                            ProductModalForeSale Modal =
                                    snapshot.toObject(ProductModalForeSale.class);
                            //ModalList.add(Modal);
                            String id = snapshot.getId();
                            Log.d(TAG, "parseSnapshot: id=" + id);
                          /*  if (Modal != null)
                                Modal.setSnapId(id);*/
                            Log.d(TAG, "getData: visibility=" + snapshot.getBoolean(
                                    "isPublished"
                            ));
                            //Modal.setPublished(snapshot.getBoolean("isPublished"));
                            return Objects.requireNonNull(Modal);
                        })
                        .build();

        productListAdapter = new ProductListAdapter(options);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productListAdapter);
        productListAdapter.startListening();

        productListAdapter.setItemClick(ProductListActivity.this);

        productListAdapter.setOnloadingStateChange(loadingState -> {
            switch (loadingState) {
                case LOADING_INITIAL:
                    System.out.println("LOADING_INITIAL");
                    swipeRefreshLayout.setRefreshing(true);
                    break;

                case LOADING_MORE:
                    System.out.println("LOADING_MORE");
                    swipeRefreshLayout.setRefreshing(true);
                    /* progressBar.setVisibility(View.GONE);*/

                    break;

                case LOADED:
                    System.out.println("LOADED");
                    swipeRefreshLayout.setRefreshing(false);
                    setTitle(String.valueOf(productListAdapter.getItemCount()));

                    /*   progressBar.setVisibility(View.GONE);*/
                    break;

                case ERROR:
                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                    System.out.println("ERROR");
                    /*      progressBar.setVisibility(View.GONE);*/
                    swipeRefreshLayout.setRefreshing(false);

                    break;

                case FINISHED:
                    swipeRefreshLayout.setRefreshing(false);
                    System.out.println("FINISHED");
                    setTitle(String.valueOf(productListAdapter.getItemCount()));
                    /* progressBar.setVisibility(View.GONE);*/
                    break;
            }
        });

    }

    @Override
    public void onRefresh() {
        productListAdapter.refresh();
    }


    @Override
    public void setOnItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, "setOnItemClick: id=" + "\n" +
                "name=" + snapshot.getId() + "\n" + snapshot.get("productName"));
        Intent i = new Intent(ProductListActivity.this,
                ChangeProductActivity.class);
        i.putExtra(productId, snapshot.getId());
        startActivity(i);

    }

    @Override
    public void OnDeleteClick(DocumentSnapshot snapshot, int pos) {
        deleteProduct(snapshot, pos);
    }

    @Override
    public void OnVisibilityClick(DocumentSnapshot snapshot, int pos) {
        changeVisibility(snapshot, pos);
    }

    private void changeVisibility(DocumentSnapshot ds, int pos) {
        showPd("changing...");

        CollectionReference productRef = FirebaseFirestore.
                getInstance().collection(PRODUCT_LIST_COLLECTION);
        Map<String, Object> visibility = new HashMap<>();

        if (ds.getBoolean("isPublished")) {
            visibility.put("isPublished", false);

        } else {
            visibility.put("isPublished", true);
        }

        productRef
                .document(ds.getId()).update(visibility)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        productListAdapter.refresh();
                        hidePd();
                        showToast("Updated");

                    }
                });

    }

    private void deleteProduct(DocumentSnapshot sp, int p) {
        showPd("deleting...");
        CollectionReference productRef = FirebaseFirestore.
                getInstance().collection(PRODUCT_LIST_COLLECTION);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dialog.dismiss();
                    if (sp.getString(productImage) != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imageRef = storage.
                                getReferenceFromUrl(sp.getString(productImage));
                        imageRef.delete().addOnSuccessListener(aVoid -> {
                            productRef.document(sp.getId()).delete()
                                    .addOnSuccessListener(aVoid1 -> {
                                        showToast("Item Deleted");
                                        productListAdapter.refresh();
                                        hidePd();
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showToast("can not be deleted");
                                            hidePd();
                                        }
                                    });

                        }).addOnFailureListener(e -> {
                            showToast(e.getMessage());
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            hidePd();

                        });


                    } else {
                        showToast("Null Values");
                    }
                }).setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss());


        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*adapter.getFilter().filter(newText);*/
                getData(newText.trim());
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.visibility:
                new AlertDialog.Builder(this)
                        .setTitle("Do You Want to Show All?")
                        .setMessage("This Will Make All products To show To\n" +
                                "User Even if They don't Have description,price OR unique id\n" +
                                "Area You Really want to do that?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VisibilityToALL(true);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                return true;

            case R.id.Invisibility:

                new AlertDialog.Builder(this)
                        .setTitle("Do You Want to Hide All?")
                        .setMessage("This Will Make sure that All products are To be Hidden From " +
                                "Users No Matter what Type Of Product It is. All Products will be " +
                                "  hidden\nArea You Really want to do that?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VisibilityToALL(false);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void VisibilityToALL(Boolean visibility) {

        showPd("setting visibility");
        CollectionReference re = FirebaseFirestore.getInstance()
                .collection(PRODUCT_LIST_COLLECTION);
        Map<String, Object> v = new HashMap<>();
        v.put("isPublished", visibility);


        re.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot ds : queryDocumentSnapshots) {
                    if (ds.exists()) {
                        re.document(ds.getId())
                                .update(v)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePd();
                                        if (visibility)
                                            showToast("Showing All");
                                        else {
                                            showToast("All Are hidden");
                                        }
                                    }
                                });
                    }
                }

                hidePd();
                showToast("updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePd();
                showToast(e.getMessage());
            }
        });
    }


    private void showPd(String title) {
        pd.setTitle(title);
        pd.show();

    }

    private void hidePd() {
        pd.cancel();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productListAdapter != null) {
            productListAdapter.stopListening();

        }
    }
}