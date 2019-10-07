package bbox.com.todoapp.ui.main;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import bbox.com.todoapp.R;
import bbox.com.todoapp.models.Todo;
import bbox.com.todoapp.ui.detail.DetailActivity;
import bbox.com.todoapp.util.VerticalSpacingItemDecorator;
import bbox.com.todoapp.viewmodels.ViewModelProviderFactory;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.DaggerAppCompatActivity;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends DaggerAppCompatActivity {

//region..declare variables
    private  final String LOG_TAG = "TodoMainAct";

    private MainViewModel mainViewModel;

    RecyclerView mRecycleVw;
    private MainRecycleAdapter mAdapter;

    boolean isSwipeDeleteable = true;

    @Inject
    ViewModelProviderFactory providerFactory;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        mainViewModel = ViewModelProviders.of(this, providerFactory).get(MainViewModel.class);

        observeTodos();

        mainViewModel.getToDosFromServer("","","");

    }

    private void observeTodos()
    {
        mainViewModel.observeToDosFromServer()
                .observe(this, new Observer<List<Todo>>() {
                    @Override
                    public void onChanged(List<Todo> todos) {
                        if(todos.size() > 0)
                        {
                            mAdapter.setTodosInAdapter(todos);
                            swipeRecyclerDelete(todos);
                        }
                    }
                });
    }


    private void initRecyclerView(){
        mRecycleVw = (RecyclerView) findViewById(R.id.rclrVw);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecycleVw.setLayoutManager(gridLayoutManager);
        mRecycleVw.addItemDecoration(new VerticalSpacingItemDecorator(10));

        mAdapter = new MainRecycleAdapter(this);
        mRecycleVw.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MainRecycleAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, Todo mBeanObj) {
                switch(view.getId())
                {
                    case(R.id.btn_Edit):
                        Intent intntEdit = new Intent(MainActivity.this , DetailActivity.class);
                        intntEdit.putExtra("intent_Main_obj", mBeanObj);
                        intntEdit.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOVIEW);
                        intntEdit.putExtra("intent_Main_setEditModeEnabled", true);
                        intntEdit.putExtra("intent_Main_setMenuEnabled", false);
                        startActivity(intntEdit);

                        break;
                    case(R.id.card_view):
                        Intent intnt = new Intent(MainActivity.this , DetailActivity.class);
                        intnt.putExtra("intent_Main_obj", mBeanObj);
                        intnt.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOVIEW);
                        intnt.putExtra("intent_Main_setEditModeEnabled", false);
                        intnt.putExtra("intent_Main_setMenuEnabled", true);
                        startActivity(intnt);

                        break;

                    case(R.id.chkBox_todoComplete):
                       mainViewModel.completeTodoInServer(mBeanObj.getId(),mBeanObj , ((CheckBox)view).isChecked());
                        observeCheckedTodos();
                        break;
                }
            }
        });
    }

    private void deleteTodo(int delete_id, final int adapterPos) {

        Call<Void> call = mainViewModel.deleteTodoInServer(Integer.valueOf(delete_id));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful())
                {Toast.makeText(MainActivity.this, "Deleted Successfully" , Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDeleted(adapterPos); }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error while deleting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToDo(View view) {
        Intent intnt = new Intent(MainActivity.this , DetailActivity.class);
        intnt.putExtra("intent_Main_mode", DetailActivity.IntentMode.TOADD);
        intnt.putExtra("intent_Main_setEditModeEnabled", true);
        intnt.putExtra("intent_Main_setMenuEnabled", false);

        startActivity(intnt);
    }

    private void swipeRecyclerDelete(final List<Todo> todos)
    {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT )
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                if(!isSwipeDeleteable)
                    return false;

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    final int position = viewHolder.getAdapterPosition();

                    deleteTodo(todos.get(position).getId() , position);

                } catch(Exception e) {
                    Log.e("MainActivity", e.getMessage());
                }
            }



            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.recycler_view_item_swipe_right_background))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .addSwipeRightLabel(getString(R.string.action_delete))
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecycleVw);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                observeFilteredTodos();
                return true;

            case R.id.menu_refresh:
                isSwipeDeleteable = true;
                mainViewModel.getToDosFromServer("","","");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void observeFilteredTodos()
    {
        isSwipeDeleteable = false;

        setUpFilterDialog();
    }

    private void  setUpFilterDialog()
    {

        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(this);

        LayoutInflater li = LayoutInflater.from(this);
       final View promptsView = li.inflate(R.layout.dialog_filter, null);

        mAlertBuilder.setPositiveButton("ok", null);
        mAlertBuilder.setNegativeButton("cancel", null);
        mAlertBuilder.setView(promptsView);

        final RadioGroup mRadioGroupDate = (RadioGroup) promptsView.findViewById(R.id.radioGroupDate);
        final RadioGroup mRadioGroupStatus = (RadioGroup) promptsView.findViewById(R.id.radioGroupStatus);
        final EditText mEdTxtVwName = (EditText) promptsView.findViewById(R.id.edDlName);

        final AlertDialog mAlertDialog = mAlertBuilder.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button btnDialog_positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnDialog_positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int radioButtonId_date= mRadioGroupDate.getCheckedRadioButtonId();
                        RadioButton mRdBtn_Date = (RadioButton)promptsView.findViewById(radioButtonId_date);

                        int radioButtonId_status = mRadioGroupStatus.getCheckedRadioButtonId();
                        RadioButton mRdBtn_status = (RadioButton)promptsView.findViewById(radioButtonId_status);

                        String strdt = "";

                        if(radioButtonId_date == R.id.rdBtnWeek)
                            strdt = getCalculatedDate(-7);
                        else if(radioButtonId_date == R.id.rdBtnMonth)
                            strdt = getCalculatedDate(-30);
                        else if(radioButtonId_date == R.id.rdBtn6Month)
                            strdt = getCalculatedDate(-180);


                        String strRd="";
                        if(mRdBtn_status != null)
                            strRd = mRdBtn_status.getText().toString();

                       mainViewModel.getToDosFromServer(strdt, strRd, mEdTxtVwName.getText().toString());

                        mAlertDialog.dismiss();

                    }
                });
            }
        });
        mAlertDialog.show();


    }

    public static String getCalculatedDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, days);
        Date newDate = calendar.getTime();


        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        return s.format(newDate);
    }


    private void observeCheckedTodos() {
        mainViewModel.observeCheckedToDoFromServer()

                .observe(this, new Observer<Todo>() {
                    @Override
                    public void onChanged(Todo todo) {

                        if (todo != null) {
                            mAdapter.notifyDataSetChanged();
                              Toast.makeText(MainActivity.this, "Item completed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
