package bbox.com.todoapp.ui.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.*;
import bbox.com.todoapp.models.Todo;
import bbox.com.todoapp.repository.MainRepository;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;

import javax.inject.Inject;
import java.util.List;

public class MainViewModel extends ViewModel {

    private static final String LOG_TAG = "shaon_mvvm";

    MediatorLiveData<List<Todo>> liveToDos = new MediatorLiveData();
    final MediatorLiveData<Todo> liveToDos_chk = new MediatorLiveData();

    @Inject
    MainRepository mainRepository;


    @Inject
    public MainViewModel()
        {    }



    public void getToDosFromServer(String filterDate, String filterStatus,  String filterName) {
      final  LiveData<List<Todo>> source  =  LiveDataReactiveStreams.fromPublisher( mainRepository.fetchToDosFromServer(filterDate,filterStatus, filterName));

      liveToDos.addSource(source, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                liveToDos.setValue(todos);
                liveToDos.removeSource(source);
            }});
    }

    public Call<Void> deleteTodoInServer(int id) {

        return mainRepository.deleteTodoInServer(id);
    }


    public LiveData<List<Todo>> observeToDosFromServer()
    {
        return liveToDos;
    }


    public void completeTodoInServer(int id,Todo todo, boolean isChek) {
        if(isChek)
            todo.setStatus("Completed");
        else
            todo.setStatus("todo");


        final LiveData<Todo> sourceUpdatedCheck  =  LiveDataReactiveStreams.fromPublisher( mainRepository.updateTodoInServer(id, todo));

        liveToDos_chk.addSource(sourceUpdatedCheck, new Observer<Todo>() {
            @Override
            public void onChanged(Todo todos) {
                getToDosFromServer("","","");
                liveToDos_chk.setValue(todos);
                liveToDos_chk.removeSource(sourceUpdatedCheck);
            }});


    }

    public LiveData<Todo> observeCheckedToDoFromServer()
    {
        return liveToDos_chk;
    }



}
