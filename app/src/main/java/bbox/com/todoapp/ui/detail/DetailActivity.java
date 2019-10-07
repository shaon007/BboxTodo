package bbox.com.todoapp.ui.detail;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import bbox.com.todoapp.R;
import bbox.com.todoapp.models.Todo;
import bbox.com.todoapp.network.main.MainApi;
import bbox.com.todoapp.util.Constants;
import bbox.com.todoapp.viewmodels.ViewModelProviderFactory;
import com.google.gson.Gson;
import dagger.android.support.DaggerAppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Inject;
import java.util.Calendar;

public class DetailActivity extends DaggerAppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "TodoDetailAct";

//region.. declare variables
    EditText edTxtName, edTxtDesc;
    TextView txtExpireDate, txtVwId;
    RadioGroup rdGroupStatus;
    RadioButton rdBtnTodo, rdBtnCompleted;
    Button btnSave;

    boolean isEditModeOn = false;
    boolean isMenuEnabled = false;
    String strIntentMode="";

   public enum IntentMode
    {
        TOADD, TOVIEW;
    }

    private DetailViewModel detailViewModel;

    @Inject
    ViewModelProviderFactory providerFactory;

    private int mYear, mMonth, mDay;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
        getIntentFromMain();

        detailViewModel = ViewModelProviders.of(this, providerFactory).get(DetailViewModel.class);

        observeTodos();
    }

    private void initialize() {
        edTxtName = (EditText) findViewById(R.id.edTxtVw_Detail_Name);
        txtExpireDate = (TextView) findViewById(R.id.txtVw_Detail_ExpDate);

         rdGroupStatus = (RadioGroup) findViewById(R.id.radioGpStatus);
         rdBtnTodo = (RadioButton)findViewById(R.id.radBtnTodo) ;
         rdBtnCompleted = (RadioButton)findViewById(R.id.radBtnCompleted);

        edTxtDesc = (EditText) findViewById(R.id.edTxtVw_Detail_Desc);

        txtVwId = (TextView) findViewById(R.id.txtVw_Desc_Id);

        btnSave = (Button) findViewById(R.id.btn_Detail_Save);

        txtExpireDate.setOnClickListener(this);

        disbleEditing();


    }

    private void getIntentFromMain()
    {
        if(getIntent().hasExtra("intent_Main_mode"))
        {
            strIntentMode = ((IntentMode)getIntent().getSerializableExtra("intent_Main_mode")).name();

            isEditModeOn = getIntent().getBooleanExtra("intent_Main_setEditModeEnabled", false);
            isMenuEnabled = getIntent().getBooleanExtra("intent_Main_setMenuEnabled", false);

            if(strIntentMode.equals(IntentMode.TOVIEW.name()))
            {   Todo intentTodo = (Todo) getIntent().getParcelableExtra("intent_Main_obj");

                edTxtName.setText(intentTodo.getName());
                txtExpireDate.setText(intentTodo.getExpiry_date());
                edTxtDesc.setText(intentTodo.getDescription());
                txtVwId.setText(String.valueOf(intentTodo.getId()));

                if(intentTodo.getStatus().toLowerCase().contains("completed"))
                    rdBtnCompleted.setChecked(true);
                else
                    rdBtnTodo.setChecked(true);
            }

            if(isEditModeOn) {
                enableEditing();
                btnSave.setVisibility(View.VISIBLE);            }
            else
                btnSave.setVisibility(View.GONE);


        }
    }

    private void observeTodos() {
        detailViewModel.observeInsertedToDoFromServer()
                .observe(this, new Observer<Todo>() {
                    @Override
                    public void onChanged(Todo todo) {
                        if (todo != null) {
                            edTxtName.setText(todo.getName());
                            if (todo.getExpiry_date() == null || todo.getExpiry_date() == "")
                                txtExpireDate.setText("null");
                            else
                                txtExpireDate.setText(todo.getExpiry_date());

                            if(todo.getStatus().toLowerCase().contains("completed"))
                                rdBtnCompleted.setChecked(true);
                            else
                                rdBtnTodo.setChecked(true);


                            edTxtDesc.setText("" + todo.getDescription());
                            txtVwId.setText("" + todo.getId());

                            disbleEditing();
                        }
                    }
                });
    }


    public void clkSave(View view) {
        if(isEmpty(edTxtName)  || isEmpty(edTxtDesc) || txtExpireDate.getText().toString().trim().length()==0)
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show();
        else {
            int radioButtonId_date= rdGroupStatus.getCheckedRadioButtonId();
            RadioButton mRdBtn_Sts = (RadioButton)findViewById(radioButtonId_date);

            Todo objToDo = new Todo(0, edTxtName.getText().toString(), mRdBtn_Sts.getText().toString(), edTxtDesc.getText().toString(), txtExpireDate.getText().toString());

            if (strIntentMode.equals(IntentMode.TOADD.name())) {
                detailViewModel.createNewTodoInServer(objToDo);
                strIntentMode = ((IntentMode) IntentMode.TOVIEW).name();
                btnSave.setVisibility(View.GONE);
            } else if (strIntentMode.equals(IntentMode.TOVIEW.name())) {
                detailViewModel.updateTodoInServer(Integer.valueOf(txtVwId.getText().toString()), objToDo);
                btnSave.setVisibility(View.GONE);
            }

            isMenuEnabled = true;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtVw_Detail_ExpDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            txtExpireDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }


    private void disbleEditing() {
        disableEditText(edTxtName);
        disableEditText(edTxtDesc);
        rdBtnCompleted.setEnabled(false);
        rdBtnTodo.setEnabled(false);
        txtExpireDate.setEnabled(false);
        txtExpireDate.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditing() {
        isEditModeOn = true;
        enableEditText(edTxtName);
        enableEditText(edTxtDesc);
        rdBtnCompleted.setEnabled(true);
        rdBtnTodo.setEnabled(true);

        edTxtName.requestFocus();
        txtExpireDate.setEnabled(true);
        txtExpireDate.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem));
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorListItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        if(isMenuEnabled == false){
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }else{
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                    enableEditing();
                    btnSave.setVisibility(View.VISIBLE);
                return true;

            case R.id.menu_delete:
                deleteTodo();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteTodo() {
            Call<Void> call = detailViewModel.deleteTodoInServer(Integer.valueOf(txtVwId.getText().toString()));

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful())
                     Toast.makeText(DetailActivity.this, "Todo deleted successfully" , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });

        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("blnIsEditModeOn", isEditModeOn);
        savedInstanceState.putBoolean("blnIsMenuEnabled", isMenuEnabled);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        isEditModeOn = savedInstanceState.getBoolean("blnIsEditModeOn");
        isMenuEnabled = savedInstanceState.getBoolean("blnIsMenuEnabled");

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


}
