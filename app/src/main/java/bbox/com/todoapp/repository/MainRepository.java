package bbox.com.todoapp.repository;

import android.util.Log;
import bbox.com.todoapp.models.Todo;
import bbox.com.todoapp.network.main.MainApi;
import io.reactivex.*;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainRepository {
    private static final String LOG_TAG = "shaon_mvvm";

    private MainApi mainApi;

    SimpleDateFormat dtFormatter;

   @Inject
    public MainRepository(MainApi mainApi, SimpleDateFormat dtFormatter) {
        this.mainApi = mainApi;
        this.dtFormatter = dtFormatter;
    }


    public Flowable<List<Todo>> fetchToDosFromServer(String filterDate, String filterStatus,  String filterName)
    {
        Observable<List<Todo>> returnedData =    mainApi.getTodos().subscribeOn(Schedulers.io());

        if(!isStringNull(filterDate) || !isStringNull(filterStatus) || !isStringNull(filterName))
        {
            if(isStringNull(filterDate))
                filterDate = "1800-01-01";

            return filterToDos(filterDate, filterStatus, filterName, returnedData);
        }


        return returnedData.toFlowable(BackpressureStrategy.BUFFER);
    }

    public Flowable<Todo> postTodoToServer(Todo todo)
    {
        Flowable<Todo> returnedData =    mainApi.createTodos(todo).subscribeOn(Schedulers.io());

        return returnedData;
    }


    public Flowable<Todo> updateTodoInServer(int id, Todo todo)
    {
        Flowable<Todo> returnedData =    mainApi.updateTodos(id, todo).subscribeOn(Schedulers.io());

        return returnedData;
    }

    public Call<Void> deleteTodoInServer(int id)
    {
        Call<Void> returnedData =    mainApi.deleteTodo(id);

        return returnedData;
    }


  public Flowable<List<Todo>> filterToDos(final String filterDate, final String filterStatus, final String filterName, Observable<List<Todo>> returnedDatafromAPi)
       {
              Flowable<List<Todo>> resultFilteredFlowable =
                                   returnedDatafromAPi
                                           .subscribeOn(Schedulers.io())
                                           .flatMap(new Function<List<Todo>, ObservableSource<Todo>>() {
                                               @Override
                                               public ObservableSource<Todo> apply(List<Todo> todos) throws Exception {
                                                   return Observable.fromIterable(todos).subscribeOn(Schedulers.io());
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   Date datenow = dtFormatter.parse(filterDate);
                                                   Date dateTodo= dtFormatter.parse(todo.getExpiry_date());

                                                   return dateTodo.after(datenow);
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   if(filterStatus==null || filterStatus.equals(""))
                                                       return true;

                                                   return todo.getStatus().toLowerCase().equals(filterStatus.toLowerCase());
                                               }
                                           })
                                           .filter(new Predicate<Todo>() {
                                               @Override
                                               public boolean test(Todo todo) throws Exception {
                                                   return todo.getName().toLowerCase().contains(filterName.toLowerCase());
                                               }
                                           })

                                           .toList()
                                           .toFlowable();

           return resultFilteredFlowable;
    }



    private boolean isStringNull(String myString)
    {
        if (myString == null || myString.equals(""))
            return true;

        return false;

    }





}























